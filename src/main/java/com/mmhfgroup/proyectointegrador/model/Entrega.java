package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity // <-- 1. Indicar que es una tabla de BD
public class Entrega {

    @Id // <-- 2. Indicar que este es el ID
    @GeneratedValue // <-- 3. Dejar que la BD genere el ID automáticamente
    private Long id;

    private String nombreArchivo;
    private LocalDateTime fechaHora;

    // 4. JPA necesita un constructor vacío
    public Entrega() {}

    public Entrega(String nombreArchivo, LocalDateTime fechaHora) {
        this.nombreArchivo = nombreArchivo;
        this.fechaHora = fechaHora;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
