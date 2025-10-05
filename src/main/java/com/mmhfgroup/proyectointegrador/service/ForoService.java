package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Mensaje;
import com.mmhfgroup.proyectointegrador.model.MensajePrivado;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForoService {

    private static final List<Mensaje> mensajesPublicos = new ArrayList<>();
    private static final List<MensajePrivado> mensajesPrivados = new ArrayList<>();

    // ---- Foro público ----
    public void publicarMensaje(String autor, String contenido) {
        if (autor != null && !autor.isEmpty() && contenido != null && !contenido.isEmpty()) {
            mensajesPublicos.add(new Mensaje(autor, contenido, java.time.LocalDateTime.now()));
        }
    }

    public List<Mensaje> listarMensajesPublicos() {
        return Collections.unmodifiableList(mensajesPublicos);
    }

    // ---- Mensajes privados ----
    public void enviarPrivado(MensajePrivado mensaje) {
        mensajesPrivados.add(mensaje);
    }

    public List<MensajePrivado> listarPrivados() {
        return Collections.unmodifiableList(mensajesPrivados);
    }
}
