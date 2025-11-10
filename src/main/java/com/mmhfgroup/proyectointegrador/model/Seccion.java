package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList; // Importado para inicializar la lista
import java.util.List;

@Entity
public class Seccion {

    @Id
    @GeneratedValue
    private Long id;

    private String titulo;
    private String descripcion;

    // Usamos FetchType.EAGER para que las zonas se carguen junto con la sección
    // Inicializamos la lista para evitar NullPointerExceptions
    @OneToMany(mappedBy = "seccion", fetch = FetchType.EAGER)
    private List<ZonaEntrega> zonasDeEntrega = new ArrayList<>();

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<ZonaEntrega> getZonasDeEntrega() { return zonasDeEntrega; }
    public void setZonasDeEntrega(List<ZonaEntrega> zonasDeEntrega) { this.zonasDeEntrega = zonasDeEntrega; }
}