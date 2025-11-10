package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Notificacion; // <-- IMPORT AÑADIDO
import com.mmhfgroup.proyectointegrador.model.Usuario; // <-- IMPORT AÑADIDO
import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import com.mmhfgroup.proyectointegrador.repository.EntregaRepository;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository; // <-- IMPORT AÑADIDO
import com.mmhfgroup.proyectointegrador.repository.ZonaEntregaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntregaService {

    private final EntregaRepository entregaRepo;
    private final ZonaEntregaRepository zonaRepo;

    // --- INICIO DE CAMBIOS ---
    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public EntregaService(EntregaRepository entregaRepo,
                          ZonaEntregaRepository zonaRepo,
                          NotificacionService notificacionService,
                          UsuarioRepository usuarioRepository) {
        this.entregaRepo = entregaRepo;
        this.zonaRepo = zonaRepo;
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Entrega registrarEntrega(Entrega e) {
        Estudiante autorEstudiante = null;

        // Si el autor es estudiante y no tiene equipo seteado explícitamente, inferir
        if (e.getAutor() instanceof Estudiante est && e.getEquipo() == null) {
            e.setEquipo(est.getEquipo()); // puede ser null si no tiene equipo
            autorEstudiante = est;
        }

        Entrega entregaGuardada = entregaRepo.save(e);

        // --- LÓGICA DE NOTIFICACIÓN AÑADIDA ---

        // 1. Notificar al alumno que realizó la entrega
        if(autorEstudiante != null) {
            String msgAlumno = "Tu entrega '" + entregaGuardada.getNombreArchivo() + "' se realizó correctamente.";
            Notificacion notifAlumno = new Notificacion(autorEstudiante, msgAlumno, false);
            notificacionService.guardar(notifAlumno);
        }

        // 2. Notificar a toda la Cátedra
        List<Usuario> catedra = usuarioRepository.findAllByRol("CATEDRA");
        for (Usuario miembroCatedra : catedra) {
            String msgCatedra = "El alumno " + (autorEstudiante != null ? autorEstudiante.getNombre() : "ID " + e.getAutor().getId()) +
                    " realizó la entrega: '" + entregaGuardada.getNombreArchivo() + "'.";
            Notificacion notifCatedra = new Notificacion(miembroCatedra, msgCatedra, false);
            notificacionService.guardar(notifCatedra);
        }
        // --- FIN DE LÓGICA DE NOTIFICACIÓN ---

        return entregaGuardada;
    }
    // --- FIN DE CAMBIOS ---

    @Transactional(readOnly = true)
    public List<Entrega> listarPorZona(Long zonaId) {
        return entregaRepo.findByZonaEntregaIdOrderByFechaHoraDesc(zonaId);
    }

    @Transactional(readOnly = true)
    public List<Entrega> listarPorEquipo(Long equipoId) {
        return entregaRepo.findByEquipoIdOrderByFechaHoraDesc(equipoId);
    }

    @Transactional(readOnly = true)
    public List<Entrega> listarPorZonaYEquipo(Long zonaId, Long equipoId) {
        return entregaRepo.findByZonaEntregaIdAndEquipoIdOrderByFechaHoraDesc(zonaId, equipoId);
    }

    @Transactional(readOnly = true)
    public ZonaEntrega getZonaOrThrow(Long zonaId) {
        return zonaRepo.findById(zonaId)
                .orElseThrow(() -> new IllegalArgumentException("Zona de entrega no encontrada: " + zonaId));
    }
}