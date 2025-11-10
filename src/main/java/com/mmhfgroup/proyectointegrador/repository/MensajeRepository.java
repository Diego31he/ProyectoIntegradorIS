package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    // JpaRepository ya proporciona save(), findById(), findAll(), delete(), etc.
    // Puedes agregar métodos de consulta personalizados aquí si los necesitas,
    // por ejemplo: List<Mensaje> findByAutor(String autor);
}