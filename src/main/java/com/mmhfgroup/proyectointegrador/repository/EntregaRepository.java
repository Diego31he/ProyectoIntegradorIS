package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    List<Entrega> findByZonaEntregaIdOrderByFechaHoraDesc(Long zonaId);
    List<Entrega> findByEquipoIdOrderByFechaHoraDesc(Long equipoId);
    List<Entrega> findByZonaEntregaIdAndEquipoIdOrderByFechaHoraDesc(Long zonaId, Long equipoId);
    List<Entrega> findByZonaEntregaId(Long zonaId);
}
