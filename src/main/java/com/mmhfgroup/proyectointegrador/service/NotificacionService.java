package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Notificacion;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Scope("singleton") // explícito: un solo bean en memoria
public class NotificacionService {

    // Puedes persistir en BD más adelante; por ahora en memoria
    private final List<Notificacion> notificaciones = new ArrayList<>();

    public List<Notificacion> listarNotificaciones() {
        // evitamos que modifiquen desde afuera
        return Collections.unmodifiableList(notificaciones);
    }

    public void agregarNotificacion(String mensaje) {
        notificaciones.add(new Notificacion(mensaje, LocalDateTime.now()));
    }

    public void limpiarNotificaciones() {
        notificaciones.clear();
    }
}
