package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // <-- AÑADIDO
public class Notificacion {

    @Id // <-- AÑADIDO
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- AÑADIDO
    private Long id;

    // AÑADIDO: Para saber a quién pertenece esta notificación
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(columnDefinition = "TEXT") // <-- AÑADIDO
    private String mensaje;
    private LocalDateTime fechaHora;

    private boolean vista; // <-- AÑADIDO: Para saber si el usuario ya la leyó

    // Constructor para JPA
    public Notificacion() {
        this.vista = false;
        this.fechaHora = LocalDateTime.now();
    }

    // Constructor actualizado
    public Notificacion(Usuario usuario, String mensaje, boolean vista) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.vista = vista;
        this.fechaHora = LocalDateTime.now();
    }

    // CONSTRUCTOR ANTIGUO (para compatibilidad, aunque debería eliminarse)
    public Notificacion(String mensaje, LocalDateTime fechaHora) {
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.vista = false;
        // OJO: this.usuario será null, lo que causará un error en la BD
        // Es mejor eliminar este constructor.
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public boolean isVista() { return vista; }
    public void setVista(boolean vista) { this.vista = vista; }
}