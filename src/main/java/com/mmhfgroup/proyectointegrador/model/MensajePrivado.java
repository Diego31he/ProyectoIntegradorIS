package com.mmhfgroup.proyectointegrador.model;

import java.time.LocalDateTime;
import java.util.List;

public class MensajePrivado {
    private String titulo;
    private List<String> destinatarios;
    private String contenido;
    private String remitente;
    private LocalDateTime fechaHora;

    public MensajePrivado(String titulo, List<String> destinatarios, String contenido, String remitente, LocalDateTime fechaHora) {
        this.titulo = titulo;
        this.destinatarios = destinatarios;
        this.contenido = contenido;
        this.remitente = remitente;
        this.fechaHora = fechaHora;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public String getContenido() {
        return contenido;
    }

    public String getRemitente() {
        return remitente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
