package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;
import java.time.LocalDate; // <-- Mantenemos LocalDate como en tu original
import java.time.LocalDateTime;

@Entity
public class EventoCalendario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mantenemos los campos de tu constructor original
    private LocalDate fecha;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaCreacion;

    @ManyToOne // Para saber quién lo creó
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    @ManyToOne // Para saber a qué equipo notificar
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    public EventoCalendario() {
        // Constructor vacío para JPA
    }

    // Tu constructor original (lo adaptamos un poco)
    public EventoCalendario(LocalDate fecha, String titulo, String descripcion) {
        this.fecha = fecha;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public Usuario getCreador() { return creador; }
    public void setCreador(Usuario creador) { this.creador = creador; }
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
}