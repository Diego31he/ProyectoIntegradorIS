package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreArchivo;
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_entrega_id")
    private ZonaEntrega zonaEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    // ✔ Para que las entregas sean por equipo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    public Entrega() {}

    public Entrega(String nombreArchivo, LocalDateTime fechaHora, ZonaEntrega zonaEntrega, Usuario autor) {
        this.nombreArchivo = nombreArchivo;
        this.fechaHora = fechaHora;
        this.zonaEntrega = zonaEntrega;
        this.autor = autor;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public ZonaEntrega getZonaEntrega() { return zonaEntrega; }
    public void setZonaEntrega(ZonaEntrega zonaEntrega) { this.zonaEntrega = zonaEntrega; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
}
