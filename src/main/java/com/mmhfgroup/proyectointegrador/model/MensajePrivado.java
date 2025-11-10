package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class MensajePrivado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    // CAMBIADO: Relación para el remitente
    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    // CAMBIADO: Relación para múltiples destinatarios
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mensaje_privado_destinatarios",
            joinColumns = @JoinColumn(name = "mensaje_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> destinatarios;

    @Column(columnDefinition = "TEXT")
    private String contenido;
    private LocalDateTime fechaHora;

    public MensajePrivado() {
        // Constructor vacío para JPA
    }

    // Constructor actualizado
    public MensajePrivado(String titulo, Usuario remitente, List<Usuario> destinatarios, String contenido) {
        this.titulo = titulo;
        this.remitente = remitente;
        this.destinatarios = destinatarios;
        this.contenido = contenido;
        this.fechaHora = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Usuario getRemitente() { return remitente; }
    public void setRemitente(Usuario remitente) { this.remitente = remitente; }
    public List<Usuario> getDestinatarios() { return destinatarios; }
    public void setDestinatarios(List<Usuario> destinatarios) { this.destinatarios = destinatarios; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    // --- Helpers para el Grid ---
    public String getRemitenteNombre() {
        return remitente != null ? remitente.getNombre() : "N/A";
    }

    public String getDestinatariosNombres() {
        if (destinatarios == null || destinatarios.isEmpty()) {
            return "N/A";
        }
        return destinatarios.stream()
                .map(Usuario::getNombre)
                .collect(Collectors.joining(", "));
    }
}