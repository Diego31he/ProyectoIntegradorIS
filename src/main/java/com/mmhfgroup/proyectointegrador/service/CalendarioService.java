package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.EventoCalendario;
import com.mmhfgroup.proyectointegrador.model.Notificacion;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.EventoCalendarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarioService {

    // --- INYECTADO (NO MÁS 'static') ---
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
     * Agrega un evento y notifica a los miembros del equipo del creador.
     * @param evento El evento a guardar
     * @param creador El Usuario que crea el evento
     */
    public void agregarEvento(EventoCalendario evento, Usuario creador) {
        evento.setCreador(creador);

        // Asignar equipo al evento (Punto 4)
        if (creador instanceof Estudiante est && est.getEquipo() != null) {
            evento.setEquipo(est.getEquipo());
        }

        eventoRepo.save(evento);

        // --- LÓGICA DE NOTIFICACIÓN (Punto 4) ---
        if (evento.getEquipo() != null) {
            String msgNotif = creador.getNombre() + " creó un evento en el calendario del equipo: '" + evento.getTitulo() + "'.";

            for (Estudiante integrante : evento.getEquipo().getIntegrantes()) {
                // No notificarse a sí mismo
                if (integrante.getId().equals(creador.getId())) {
                    continue;
                }
                Notificacion notif = new Notificacion(integrante, msgNotif, false);
                notificacionService.guardar(notif);
            }
        }
    }

    public void eliminarEvento(EventoCalendario evento) {
        eventoRepo.delete(evento);
    }
}