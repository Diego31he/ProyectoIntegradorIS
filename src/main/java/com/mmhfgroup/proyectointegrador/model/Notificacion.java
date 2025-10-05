package com.mmhfgroup.proyectointegrador.model;

import java.time.LocalDateTime;

public class Notificacion {
    private String mensaje;
    private LocalDateTime fechaHora;

    public Notificacion(String mensaje, LocalDateTime fechaHora) {
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
