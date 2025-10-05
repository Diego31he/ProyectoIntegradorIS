package com.mmhfgroup.proyectointegrador.model;

public class Equipo {
    private int numero;
    private String nombre;
    private String auditor;

    public Equipo(int numero, String nombre, String auditor) {
        this.numero = numero;
        this.nombre = nombre;
        this.auditor = auditor;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
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
