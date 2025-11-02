package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
}