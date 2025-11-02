package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mmhfgroup.proyectointegrador.repository.EntregaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service // <-- 1. Marcar como Servicio de Spring
public class EntregaService {

    // 2. Inyectar el repositorio
    @Autowired
    private EntregaRepository repository;

    public List<Entrega> listarEntregas() {
        return repository.findAll(); // <-- 3. Usar el repositorio
    }

    public void registrarEntrega(Entrega entrega) {
        repository.save(entrega); // <-- 3. Usar el repositorio
    }

    public void eliminarEntrega(Entrega entrega) {
        repository.delete(entrega); // <-- 3. Usar el repositorio
    }
}