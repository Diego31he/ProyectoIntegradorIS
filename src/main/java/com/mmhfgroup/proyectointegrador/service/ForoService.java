package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Mensaje;
import com.mmhfgroup.proyectointegrador.model.MensajePrivado;
import com.mmhfgroup.proyectointegrador.model.Notificacion;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.MensajePrivadoRepository;
import com.mmhfgroup.proyectointegrador.repository.MensajeRepository;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForoService {

    // --- INYECTADO (NO MÁS 'static') ---
    private final MensajeRepository mensajeRepo;
    private final MensajePrivadoRepository mensajePrivadoRepo;
    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public ForoService(MensajeRepository mensajeRepo,
                       MensajePrivadoRepository mensajePrivadoRepo,
                       NotificacionService notificacionService,
                       UsuarioRepository usuarioRepository) {
        this.mensajeRepo = mensajeRepo;
        this.mensajePrivadoRepo = mensajePrivadoRepo;
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    // ---- Foro público (Punto 3) ----

    /**
     * Publica un mensaje y notifica a TODOS los demás usuarios.
     */
    public void publicarMensaje(Usuario autor, String contenido) {
        if (autor != null && contenido != null && !contenido.isEmpty()) {

            Mensaje mensaje = new Mensaje(autor, contenido, LocalDateTime.now());
            mensajeRepo.save(mensaje);

            // Notificar al resto de usuarios (Punto 3)
            List<Usuario> otrosUsuarios = usuarioRepository.findAll().stream()
                    .filter(u -> !u.getId().equals(autor.getId()))
                    .collect(Collectors.toList());

            String msgNotif = "Nuevo mensaje en el foro de " + autor.getNombre() + ": " + contenido.substring(0, Math.min(contenido.length(), 20)) + "...";
            for(Usuario user : otrosUsuarios) {
                Notificacion notif = new Notificacion(user, msgNotif, false);
                notificacionService.guardar(notif);
            }
        }
    }

    public List<Mensaje> listarMensajesPublicos() {
        return mensajeRepo.findAll();
    }

    // ---- Mensajes privados (Punto 2) ----

    /**
     * Envía un mensaje privado y notifica a los DESTINATARIOS.
     */
    public void enviarPrivado(MensajePrivado mensaje) {
        mensajePrivadoRepo.save(mensaje);

        // Notificar a todos los destinatarios (Punto 2)
        String msgNotif = "Recibiste un mensaje privado de: " + mensaje.getRemitente().getNombre();

        if(mensaje.getDestinatarios() != null) {
            for(Usuario destinatario : mensaje.getDestinatarios()) {
                Notificacion notif = new Notificacion(destinatario, msgNotif, false);
                notificacionService.guardar(notif);
            }
        }
    }

    public List<MensajePrivado> listarPrivados() {
        return mensajePrivadoRepo.findAll();
    }
}