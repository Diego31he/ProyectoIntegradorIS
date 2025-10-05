package com.mmhfgroup.proyectointegrador.model;

import java.time.LocalDateTime;

public class Mensaje {
    private String autor;
    private String contenido;
    private LocalDateTime fechaHora;

    public Mensaje(String autor, String contenido, LocalDateTime fechaHora) {
        this.autor = autor;
        this.contenido = contenido;
        this.fechaHora = fechaHora;
    }

    public String getAutor() {
        return autor;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
