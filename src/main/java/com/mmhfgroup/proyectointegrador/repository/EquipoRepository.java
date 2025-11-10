package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    boolean existsByNumero(Integer numero);
    Optional<Equipo> findByNumero(Integer numero);
    List<Equipo> findAllByOrderByNumeroAsc();
    Optional<Equipo> findTopByOrderByNumeroDesc();
}
