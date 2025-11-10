package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.repository.EntregaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EntregaService {

    private final EntregaRepository entregaRepo;

    public EntregaService(EntregaRepository entregaRepo) {
        this.entregaRepo = entregaRepo;
    }

    @Transactional
    public Entrega registrarEntrega(Equipo equipo, String nombreArchivo) {
        Entrega e = new Entrega(nombreArchivo, LocalDateTime.now(), equipo);
        return entregaRepo.save(e);
    }

    @Transactional(readOnly = true)
    public List<Entrega> listarPorEquipo(Long equipoId) {
        return entregaRepo.findByEquipoIdOrderByFechaHoraDesc(equipoId);
    }

    @Transactional
    public void eliminar(Long entregaId) {
        entregaRepo.deleteById(entregaId);
    }
}
