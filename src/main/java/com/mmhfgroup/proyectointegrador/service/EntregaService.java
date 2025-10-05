package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntregaService {

    private static final List<Entrega> entregas = new ArrayList<>();

    public List<Entrega> listarEntregas() {
        return Collections.unmodifiableList(entregas);
    }

    public void registrarEntrega(Entrega entrega) {
        entregas.add(entrega);
    }

    public void eliminarEntrega(Entrega entrega) {
        entregas.remove(entrega);
    }
}
