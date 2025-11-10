package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import com.mmhfgroup.proyectointegrador.repository.EntregaRepository;
import com.mmhfgroup.proyectointegrador.repository.ZonaEntregaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntregaService {

    private final EntregaRepository entregaRepo;
    private final ZonaEntregaRepository zonaRepo;

    public EntregaService(EntregaRepository entregaRepo, ZonaEntregaRepository zonaRepo) {
        this.entregaRepo = entregaRepo;
        this.zonaRepo = zonaRepo;
    }

    @Transactional
    public Entrega registrarEntrega(Entrega e) {
        // Si el autor es estudiante y no tiene equipo seteado explícitamente, inferir
        if (e.getAutor() instanceof Estudiante est && e.getEquipo() == null) {
            e.setEquipo(est.getEquipo()); // puede ser null si no tiene equipo
        }
        return entregaRepo.save(e);
    }

    @Transactional(readOnly = true)
    public List<Entrega> listarPorZona(Long zonaId) {
        return entregaRepo.findByZonaEntregaIdOrderByFechaHoraDesc(zonaId);
    }

    @Transactional(readOnly = true)
    public List<Entrega> listarPorEquipo(Long equipoId) {
        return entregaRepo.findByEquipoIdOrderByFechaHoraDesc(equipoId);
    }

    @Transactional(readOnly = true)
    public List<Entrega> listarPorZonaYEquipo(Long zonaId, Long equipoId) {
        return entregaRepo.findByZonaEntregaIdAndEquipoIdOrderByFechaHoraDesc(zonaId, equipoId);
    }

    @Transactional(readOnly = true)
    public ZonaEntrega getZonaOrThrow(Long zonaId) {
        return zonaRepo.findById(zonaId)
                .orElseThrow(() -> new IllegalArgumentException("Zona de entrega no encontrada: " + zonaId));
    }
}
