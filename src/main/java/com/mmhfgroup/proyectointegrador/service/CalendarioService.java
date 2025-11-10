package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.EventoCalendario;
import com.mmhfgroup.proyectointegrador.model.Notificacion;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.EventoCalendarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- AÑADIDO

import java.util.List;

@Service
public class CalendarioService {

    private final EventoCalendarioRepository eventoRepo;
    private final NotificacionService notificacionService;

    public CalendarioService(EventoCalendarioRepository eventoRepo, NotificacionService notificacionService) {
        this.eventoRepo = eventoRepo;
        this.notificacionService = notificacionService;
    }

    public List<EventoCalendario> listarEventos() {
        return eventoRepo.findAll();
    }

    /**
     * Agrega un evento y notifica a los involucrados.
     * 1. Notifica al creador.
     * 2. Si el evento tiene un equipo asignado (como las Auditorías),
     * notifica a todos los integrantes de ese equipo.
     *
     * @param evento El evento a guardar
     * @param creador El Usuario que crea el evento
     */
    @Transactional // <-- AÑADIDO
    public void agregarEvento(EventoCalendario evento, Usuario creador) {
        evento.setCreador(creador);

        // Si el creador es Estudiante Y el evento NO tiene equipo
        // (ej. un evento personal de equipo), asigna el equipo del estudiante.
        if (creador instanceof Estudiante est && evento.getEquipo() == null) {
            evento.setEquipo(est.getEquipo());
        }

        eventoRepo.save(evento);

        // --- INICIO DE LÓGICA DE NOTIFICACIÓN ACTUALIZADA ---

        // 1. Notificar SIEMPRE al creador (Auditor o Estudiante)
        String msgCreador = "Creaste un nuevo evento en el calendario: '" + evento.getTitulo() + "'.";
        Notificacion notifCreador = new Notificacion(creador, msgCreador, false);
        notificacionService.guardar(notifCreador);

        // 2. Notificar a los integrantes del equipo ASIGNADO AL EVENTO
        // (Esto funciona para Auditorías creadas por Cátedra y eventos de Estudiantes)
        if (evento.getEquipo() != null && evento.getEquipo().getIntegrantes() != null) {

            String msgIntegrantes = "Se agregó un evento al calendario de tu equipo: '" +
                    evento.getTitulo() + "' (Creado por: " + creador.getNombreCompleto() + ").";

            for (Estudiante integrante : evento.getEquipo().getIntegrantes()) {
                // Evitar notificar al creador dos veces (si es estudiante de ese equipo)
                if (integrante.getId().equals(creador.getId())) {
                    continue;
                }

                Notificacion notif = new Notificacion(integrante, msgIntegrantes, false);
                notificacionService.guardar(notif);
            }
        }
        // --- FIN DE LÓGICA DE NOTIFICACIÓN ACTUALIZADA ---
    }

    @Transactional // <-- AÑADIDO
    public void eliminarEvento(EventoCalendario evento) {
        // Opcional: Notificar eliminación si se desea

        eventoRepo.delete(evento);
    }
}