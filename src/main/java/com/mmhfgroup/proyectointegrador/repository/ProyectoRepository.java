package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
}