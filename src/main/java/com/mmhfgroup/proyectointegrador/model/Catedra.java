package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "catedra")
public class Catedra extends Usuario {

    @Column
    private String cargo;

    @Column(nullable = false)
    private boolean isAdmin; // true = ADMIN, false = CATEDRA

    public Catedra() { super(); }

    public Catedra(String nombre, String apellido, String email, String password, String cargo, boolean isAdmin) {
        super(nombre, apellido, email, password);
        this.cargo = cargo;
        this.isAdmin = isAdmin;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
