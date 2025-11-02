package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiantes") // Esta tabla contendrá solo el legajo
public class Estudiante extends Usuario {

    private String legajo;

    // --- Constructores, Getters y Setters ---

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
}