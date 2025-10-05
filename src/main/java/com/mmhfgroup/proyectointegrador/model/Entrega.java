package com.mmhfgroup.proyectointegrador.model;

import java.time.LocalDateTime;

public class Entrega {
    private String nombreArchivo;
    private LocalDateTime fechaHora;

    public Entrega(String nombreArchivo, LocalDateTime fechaHora) {
        this.nombreArchivo = nombreArchivo;
        this.fechaHora = fechaHora;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
