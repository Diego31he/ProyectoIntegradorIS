package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // <-- IMPORT AÑADIDO

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // --- INICIO DE MÉTODOS AÑADIDOS ---

    /**
     * Devuelve una lista de todos los usuarios.
     * Este era el método 'listarTodos' que faltaba.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Devuelve los roles disponibles para el ComboBox.
     * Este era el método 'getAvailableRoles' que faltaba.
     */
    public List<String> getAvailableRoles() {
        // Nombres de los roles que tu lógica 'cambiarRol' soporta
        return List.of("ESTUDIANTE", "CATEDRA", "ADMIN");
    }

    // --- FIN DE MÉTODOS AÑADIDOS ---

    /**
     * Cambia el rol del usuario indicado.
     * nuevoRol: "ESTUDIANTE" | "CATEDRA" | "ADMIN"
     */
    @Transactional
    public void cambiarRol(Long usuarioId, String nuevoRol) {
        Usuario actual = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado id=" + usuarioId));

        String target = (nuevoRol == null) ? "" : nuevoRol.trim().toUpperCase();

        switch (target) {
            case "ADMIN" -> toAdmin(actual);
            case "CATEDRA" -> toCatedra(actual);
            case "ESTUDIANTE" -> toEstudiante(actual);
            default -> throw new IllegalArgumentException("Rol no soportado: " + nuevoRol);
        }
    }

    private void toAdmin(Usuario u) {
        if (u instanceof Catedra c) {
            if (Boolean.TRUE.equals(c.isAdmin())) return; // ya es admin
            c.setAdmin(true);
            usuarioRepository.save(c);
            return;
        }
        Catedra nuevo = catedraFrom(u, true);
        replaceUser(u, nuevo);
    }

    private void toCatedra(Usuario u) {
        if (u instanceof Catedra c) {
            if (!Boolean.TRUE.equals(c.isAdmin())) return; // ya es cátedra no admin
            c.setAdmin(false);
            usuarioRepository.save(c);
            return;
        }
        Catedra nuevo = catedraFrom(u, false);
        replaceUser(u, nuevo);
    }

    private void toEstudiante(Usuario u) {
        if (u instanceof Estudiante) return; // ya es estudiante
        Estudiante nuevo = estudianteFrom(u);
        replaceUser(u, nuevo);
    }

    // ---------- helpers de conversión ----------

    private Catedra catedraFrom(Usuario base, boolean admin) {
        Catedra c = new Catedra();
        c.setEmail(base.getEmail());
        c.setNombre(base.getNombre());
        c.setPassword(base.getPassword());
        c.setAdmin(admin);
        // Aquí puedes copiar más campos si es necesario (ej: apellido)
        c.setApellido(base.getApellido());
        return c;
    }

    private Estudiante estudianteFrom(Usuario base) {
        Estudiante e = new Estudiante();
        e.setEmail(base.getEmail());
        e.setNombre(base.getNombre());
        e.setPassword(base.getPassword());
        e.setApellido(base.getApellido());
        // Aquí puedes copiar más campos si es necesario (ej: legajo)
        if (base instanceof Catedra) {
            // Si era cátedra, no tendrá legajo, inventamos uno
            e.setLegajo("LEG-" + base.getId());
        }
        return e;
    }

    private void replaceUser(Usuario viejo, Usuario nuevo) {
        // Asignamos el ID del usuario viejo al nuevo para "reemplazarlo"
        nuevo.setId(viejo.getId());
        usuarioRepository.delete(viejo);
        usuarioRepository.flush();
        usuarioRepository.save(nuevo);
    }
}