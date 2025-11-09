package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*; // Importamos todo
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ZonaEntrega {

    @Id
    @GeneratedValue
    private Long id;

    private String titulo;
    private LocalDate fechaCierre;

    @ManyToOne
    private Seccion seccion;

    @OneToMany(mappedBy = "zonaEntrega")
    private List<Entrega> entregas = new ArrayList<>();

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public LocalDate getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDate fechaCierre) { this.fechaCierre = fechaCierre; }
    public Seccion getSeccion() { return seccion; }
    public void setSeccion(Seccion seccion) { this.seccion = seccion; }
    public List<Entrega> getEntregas() { return entregas; }
    public void setEntregas(List<Entrega> entregas) { this.entregas = entregas; }
}
