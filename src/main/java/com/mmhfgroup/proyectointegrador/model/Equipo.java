package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;
import java.util.List; // <-- IMPORT AÑADIDO
import java.util.Set; // <-- Opcional, pero Set es a veces mejor para relaciones

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

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String auditor;

    // --- INICIO DE CAMBIO ---

    /**
     * Define la lista de estudiantes que son integrantes de este equipo.
     * "mappedBy = "equipo"" le dice a JPA que la entidad Estudiante
     * es la dueña de la relación (a través de su campo "equipo").
     * * FetchType.EAGER: Carga los integrantes automáticamente al cargar el equipo.
     * (Puedes cambiarlo a LAZY si prefieres cargarlos manualmente).
     */
    @OneToMany(mappedBy = "equipo", fetch = FetchType.EAGER)
    private Set<Estudiante> integrantes; // <-- CAMPO AÑADIDO (Set o List)

    // --- FIN DE CAMBIO ---


    public Equipo() {}

    public Equipo(Integer numero, String nombre, String auditor) {
        this.numero = numero;
        this.nombre = nombre;
        this.auditor = auditor;
    }

    // --- Getters / Setters ---

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

    // --- GETTER Y SETTER AÑADIDOS PARA 'integrantes' ---

    public Set<Estudiante> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(Set<Estudiante> integrantes) {
        this.integrantes = integrantes;
    }
}