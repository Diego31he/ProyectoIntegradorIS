package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.MensajePrivado;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORT AÑADIDO
import org.springframework.data.repository.query.Param; // <-- IMPORT AÑADIDO
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MensajePrivadoRepository extends JpaRepository<MensajePrivado, Long> {

    // --- MÉTODOS CORREGIDOS ---

    /**
     * Encontrar todos los mensajes donde un usuario es el remitente O
     * es UNO DE LOS destinatarios.
     */
    @Query("SELECT m FROM MensajePrivado m WHERE m.remitente = :usuario OR :usuario MEMBER OF m.destinatarios")
    List<MensajePrivado> findByRemitenteOrDestinatariosContaining(@Param("usuario") Usuario usuario);

    /**
     * Encontrar la conversación entre dos usuarios específicos.
     * Busca donde A es remitente Y B está en destinatarios,
     * O donde B es remitente Y A está en destinatarios.
     * Ordenado por fecha.
     */
    @Query("SELECT m FROM MensajePrivado m WHERE " +
            "(m.remitente = :usuarioA AND :usuarioB MEMBER OF m.destinatarios) OR " +
            "(m.remitente = :usuarioB AND :usuarioA MEMBER OF m.destinatarios) " +
            "ORDER BY m.fechaHora ASC")
    List<MensajePrivado> findConversacionEntre(
            @Param("usuarioA") Usuario usuarioA,
            @Param("usuarioB") Usuario usuarioB
    );

    // --- MÉTODOS ANTIGUOS (ELIMINADOS PORQUE CAUSABAN EL ERROR) ---

    // List<MensajePrivado> findByRemitenteOrDestinatario(Usuario remitente, Usuario destinatario); // <-- Incorrecto

    // List<MensajePrivado> findByRemitenteAndDestinatarioOrRemitenteAndDestinatarioOrderByFechaHoraAsc( // <-- Incorrecto
    //        Usuario usuarioA, Usuario usuarioB, Usuario usuarioB2, Usuario usuarioA2
    // );
}