package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    List<Entrega> findByZonaEntregaIdOrderByFechaHoraDesc(Long zonaId);
    List<Entrega> findByEquipoIdOrderByFechaHoraDesc(Long equipoId);
    List<Entrega> findByZonaEntregaIdAndEquipoIdOrderByFechaHoraDesc(Long zonaId, Long equipoId);
    List<Entrega> findByZonaEntregaId(Long zonaId);

    @Query("""
           select distinct e
           from Entrega e
           left join fetch e.autor a
           left join fetch TREAT(a as com.mmhfgroup.proyectointegrador.model.Estudiante).equipo eq
           where e.zonaEntrega.id = :zonaId
           order by e.fechaHora desc
           """)
    List<Entrega> findByZonaEntregaIdWithAutorEquipo(@Param("zonaId") Long zonaId);
}
