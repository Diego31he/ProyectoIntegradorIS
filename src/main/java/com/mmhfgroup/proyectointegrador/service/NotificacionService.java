package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Notificacion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionService {

    private static final List<Notificacion> notificaciones = new ArrayList<>();

    public List<Notificacion> listarNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }

    public void agregarNotificacion(String mensaje) {
        notificaciones.add(new Notificacion(mensaje, java.time.LocalDateTime.now()));
    }

    public void limpiarNotificaciones() {
        notificaciones.clear();
    }
}
