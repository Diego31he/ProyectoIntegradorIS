package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

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
        // Estudiante -> Admin (pasa a Catedra con admin=true)
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
        // Estudiante -> Cátedra (admin=false)
        Catedra nuevo = catedraFrom(u, false);
        replaceUser(u, nuevo);
    }

    private void toEstudiante(Usuario u) {
        if (u instanceof Estudiante) return; // ya es estudiante
        // Cátedra/Admin -> Estudiante (sin equipo asignado)
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
        // Copiá aquí otros campos comunes si existen (apellido, activo, etc.)
        return c;
    }

    private Estudiante estudianteFrom(Usuario base) {
        Estudiante e = new Estudiante();
        e.setEmail(base.getEmail());
        e.setNombre(base.getNombre());
        e.setPassword(base.getPassword());
        // No asignamos equipo aquí (queda "sin equipo asignado")
        return e;
    }

    /**
     * Reemplaza una entidad polimórfica por otra:
     * - borra la anterior
     * - guarda la nueva (con mismo email/nombre/password)
     * IMPORTANTE: si tenés relaciones (FK) hacia Usuario,
     * puede requerir migración adicional.
     */
    private void replaceUser(Usuario viejo, Usuario nuevo) {
        // Borramos el viejo y guardamos el nuevo
        usuarioRepository.delete(viejo);
        usuarioRepository.flush();
        usuarioRepository.save(nuevo);
    }
}
