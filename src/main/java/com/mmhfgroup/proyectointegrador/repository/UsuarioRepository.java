package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORT AÑADIDO
import org.springframework.data.repository.query.Param; // <-- IMPORT AÑADIDO

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    // --- CORRECCIÓN ---
    // En lugar de buscar por un campo "rol" que no existe,
    // usamos una @Query para buscar por el tipo de entidad (DTYPE).
    // El :rolName se reemplazará por "Catedra", "Estudiante", etc.
    @Query("SELECT u FROM Usuario u WHERE TYPE(u) = (SELECT TYPE(e) FROM Usuario e WHERE TYPE(e) = :rolName)")
    List<Usuario> findAllByRol(@Param("rolName") String rolName);

    // NOTA: Si lo anterior no funciona (depende de la config de JPA), usa esta versión más directa:
    // @Query("SELECT u FROM Usuario u WHERE TYPE(u) = :rolName")
    // List<Usuario> findAllByRol(@Param("rolName") String rolName);
}