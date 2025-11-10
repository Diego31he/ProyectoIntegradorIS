package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Notificacion;
import com.mmhfgroup.proyectointegrador.model.Usuario; // <-- AÑADIDO
import com.mmhfgroup.proyectointegrador.repository.NotificacionRepository; // <-- AÑADIDO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- AÑADIDO

import java.util.List;

@Service
// @Scope("singleton") // No es necesario, Service ya es singleton por defecto
public class NotificacionService {

    // --- INICIO DE CAMBIOS ---

    // Eliminada la lista en memoria
    // private final List<Notificacion> notificaciones = new ArrayList<>();

    // Inyectamos el repositorio
    private final NotificacionRepository notificacionRepo;

    public NotificacionService(NotificacionRepository notificacionRepo) {
        this.notificacionRepo = notificacionRepo;
    }

    /**
     * Lista las notificaciones para un usuario específico.
     */
    @Transactional(readOnly = true)
    public List<Notificacion> listarNotificacionesPorUsuario(Usuario usuario) {
        return notificacionRepo.findByUsuarioOrderByFechaHoraDesc(usuario);
    }

    /**
     * Guarda una nueva notificación en la Base de Datos.
     * Esta es la función que los otros servicios (Foro, Entrega) deben llamar.
     */
    @Transactional
    public void guardar(Notificacion notificacion) {
        if (notificacion.getUsuario() == null) {
            // Seguridad para evitar notificaciones sin dueño
            throw new IllegalArgumentException("La notificación debe tener un usuario destinatario.");
        }
        notificacionRepo.save(notificacion);
    }

    /**
     * Cuenta cuántas notificaciones nuevas (no vistas) tiene un usuario.
     * Útil para el ícono de la campanita en el menú.
     */
    @Transactional(readOnly = true)
    public long contarNuevasPorUsuario(Usuario usuario) {
        return notificacionRepo.countByUsuarioAndVista(usuario, false);
    }

    /**
     * Marca una lista de notificaciones como "vistas".
     */
    @Transactional
    public void marcarComoVistas(List<Notificacion> notificaciones) {
        for (Notificacion n : notificaciones) {
            n.setVista(true);
        }
        notificacionRepo.saveAll(notificaciones);
    }

    // --- FIN DE CAMBIOS ---

    // El método 'agregarNotificacion(String mensaje)' fue eliminado
    // porque ahora las notificaciones deben estar asociadas a un Usuario.

    // El método 'limpiarNotificaciones' fue eliminado,
    // se reemplaza por lógica de "marcar como vista".
}