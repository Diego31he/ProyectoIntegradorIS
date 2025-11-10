package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.EventoCalendario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoCalendarioRepository extends JpaRepository<EventoCalendario, Long> {
    // JpaRepository ya proporciona save(), findById(), findAll(), delete(), etc.
    // Puedes agregar métodos de consulta personalizados aquí si los necesitas.
}