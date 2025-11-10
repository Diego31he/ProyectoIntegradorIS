package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Notificacion;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Query para encontrar todas las notificaciones de un usuario específico,
    // ordenadas por fecha (las más nuevas primero).
    List<Notificacion> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    // Contar cuántas notificaciones no vistas tiene un usuario
    long countByUsuarioAndVista(Usuario usuario, boolean vista);
}