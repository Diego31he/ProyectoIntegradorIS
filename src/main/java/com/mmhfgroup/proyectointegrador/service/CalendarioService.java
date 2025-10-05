package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.EventoCalendario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarioService {

    private static final List<EventoCalendario> eventos = new ArrayList<>();

    public List<EventoCalendario> listarEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public void agregarEvento(EventoCalendario evento) {
        eventos.add(evento);
    }

    public void eliminarEvento(EventoCalendario evento) {
        eventos.remove(evento);
    }
}
