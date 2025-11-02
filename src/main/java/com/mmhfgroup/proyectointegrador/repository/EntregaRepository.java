package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

// Esto te da mágicamente todos los métodos: save(), findAll(), deleteById(), etc.
public interface EntregaRepository extends JpaRepository<Entrega, Long> {
}