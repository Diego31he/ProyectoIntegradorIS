package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CAMBIADO: Antes era String, ahora es una relación
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @Column(columnDefinition = "TEXT")
    private String contenido;
    private LocalDateTime fechaHora;

    public Mensaje() {
        // Constructor vacío para JPA
    }

    // CAMBIADO: Constructor actualizado
    public Mensaje(Usuario autor, String contenido, LocalDateTime fechaHora) {
        this.autor = autor;
        this.contenido = contenido;
        this.fechaHora = fechaHora;
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    // Helper para el Grid
    public String getAutorNombre() {
        return autor != null ? autor.getNombre() : "N/A";
    }
}