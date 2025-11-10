package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_id", nullable = false, foreignKey = @ForeignKey(name = "fk_entrega_equipo"))
    private Equipo equipo;

    public Entrega() {}

    public Entrega(String nombreArchivo, LocalDateTime fechaHora, Equipo equipo) {
        this.nombreArchivo = nombreArchivo;
        this.fechaHora = fechaHora;
        this.equipo = equipo;
    }

    public Long getId() { return id; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
}
