package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaEntregaRepository extends JpaRepository<ZonaEntrega, Long> {
    // Spring Data creará esta consulta automáticamente
    List<ZonaEntrega> findBySeccionId(Long seccionId);
}
