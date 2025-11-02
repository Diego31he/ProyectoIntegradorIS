package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "catedra") // Esta tabla contendrá el cargo y si es admin
public class Catedra extends Usuario {

    private String cargo;
    private boolean isAdmin;

    // --- Constructores, Getters y Setters ---

    public Catedra() {
        super();
    }

    public Catedra(String nombre, String apellido, String email, String password, String cargo, boolean isAdmin) {
        super(nombre, apellido, email, password);
        this.cargo = cargo;
        this.isAdmin = isAdmin;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}