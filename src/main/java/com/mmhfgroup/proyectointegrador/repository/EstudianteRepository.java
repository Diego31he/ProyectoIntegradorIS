package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Estudiante;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    @EntityGraph(attributePaths = "equipo")
    List<Estudiante> findAll(); // este traerá equipo pre-cargado
    List<Estudiante> findByEquipo_IdOrderByApellidoAscNombreAsc(Long equipoId);
    List<Estudiante> findByEquipoIsNull(); // <-- AÑADIR ESTO
}

