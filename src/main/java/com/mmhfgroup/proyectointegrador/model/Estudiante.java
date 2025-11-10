package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "estudiantes",
        indexes = {
                @Index(name = "idx_estudiante_legajo", columnList = "legajo", unique = true)
        }
)
public class Estudiante extends Usuario {

    @Column(nullable = false, unique = true)
    private String legajo;

    // null = sin equipo asignado
    // EAGER para evitar LazyInitializationException al renderizar en el Grid de Vaadin
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipo_id", foreignKey = @ForeignKey(name = "fk_estudiante_equipo"))
    private Equipo equipo;

    public Estudiante() {
        super();
    }

    public Estudiante(String nombre, String apellido, String email, String password, String legajo) {
        super(nombre, apellido, email, password);
        this.legajo = legajo;
    }

    public String getLegajo() {
        return legajo;
    }
    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public Equipo getEquipo() {
        return equipo;
    }
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
    public String getNombreCompleto(){
        return getNombre() + " " + getApellido();
    }
}
