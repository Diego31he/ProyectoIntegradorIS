package com.mmhfgroup.proyectointegrador.model;

import java.time.LocalDate;

public class EventoCalendario {
    private LocalDate fecha;
    private String titulo;
    private String descripcion;

    public EventoCalendario(LocalDate fecha, String titulo, String descripcion) {
        this.fecha = fecha;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
