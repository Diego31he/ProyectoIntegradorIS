package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne; // <-- IMPORTANTE
import java.time.LocalDateTime;

@Entity
public class Entrega {

    @Id
    @GeneratedValue
    private Long id;

    private String nombreArchivo;
    private LocalDateTime fechaHora;

    // --- CAMPOS NUEVOS ---
    @ManyToOne
    private ZonaEntrega zonaEntrega; // A qué zona pertenece esta entrega

    @ManyToOne
    private Usuario autor; // Quién la subió
    // --- FIN CAMPOS NUEVOS ---

    public Entrega() {}

    // --- CONSTRUCTOR MODIFICADO ---
    public Entrega(String nombreArchivo, LocalDateTime fechaHora, ZonaEntrega zona, Usuario autor) {
        this.nombreArchivo = nombreArchivo;
        this.fechaHora = fechaHora;
        this.zonaEntrega = zona;
        this.autor = autor;
    }

    // --- Getters y Setters (incluir los nuevos) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public ZonaEntrega getZonaEntrega() { return zonaEntrega; }
    public void setZonaEntrega(ZonaEntrega zonaEntrega) { this.zonaEntrega = zonaEntrega; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
}
