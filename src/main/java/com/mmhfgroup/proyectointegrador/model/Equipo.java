package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "equipos",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_equipo_numero", columnNames = {"numero"})
        }
)
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número visible del equipo (1, 2, 3, ...). Único.
    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String auditor;

    public Equipo() {}

    public Equipo(Integer numero, String nombre, String auditor) {
        this.numero = numero;
        this.nombre = nombre;
        this.auditor = auditor;
    }

    // Getters / Setters
    public Long getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAuditor() {
        return auditor;
    }
    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }
}
