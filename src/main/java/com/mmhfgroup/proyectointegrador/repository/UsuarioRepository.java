package com.mmhfgroup.proyectointegrador.repository;

import com.mmhfgroup.proyectointegrador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método útil para buscar por email (usado por Spring Security)
    Optional<Usuario> findByEmail(String email);
}