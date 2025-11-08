package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import com.mmhfgroup.proyectointegrador.repository.CatedraRepository;
import com.mmhfgroup.proyectointegrador.repository.EstudianteRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UsuarioRepository usuarioRepo,
            EstudianteRepository estudianteRepo,
            CatedraRepository catedraRepo,
            PasswordEncoder encoder // <- usa el bean definido en SecurityConfig
    ) {
        return args -> seedBaseUsers(usuarioRepo, estudianteRepo, catedraRepo, encoder);
    }

    @Transactional
    public void seedBaseUsers(
            UsuarioRepository usuarioRepo,
            EstudianteRepository estudianteRepo,
            CatedraRepository catedraRepo,
            PasswordEncoder encoder
    ) {
        // ADMIN (Cátedra con flag admin=true)
        createAdminIfMissing(usuarioRepo, catedraRepo, encoder,
                "admin@demo.com", "admin", "Administrador");

        // CÁTEDRA
        createCatedraIfMissing(usuarioRepo, catedraRepo, encoder,
                "catedra@demo.com", "catedra", "Profesor");

        // ESTUDIANTE
        createEstudianteIfMissing(usuarioRepo, estudianteRepo, encoder,
                "alumno@demo.com", "alumno", "12345");
    }

    private void createAdminIfMissing(
            UsuarioRepository usuarioRepo,
            CatedraRepository catedraRepo,
            PasswordEncoder encoder,
            String email,
            String rawPassword,
            String cargo
    ) {
        if (usuarioRepo.findByEmail(email).isEmpty()) {
            Catedra admin = new Catedra();
            admin.setEmail(email);
            admin.setPassword(encoder.encode(rawPassword));
            admin.setCargo(cargo);   // si tu entidad lo tiene
            admin.setAdmin(true);    // flag de admin
            catedraRepo.save(admin);
        }
    }

    private void createCatedraIfMissing(
            UsuarioRepository usuarioRepo,
            CatedraRepository catedraRepo,
            PasswordEncoder encoder,
            String email,
            String rawPassword,
            String cargo
    ) {
        if (usuarioRepo.findByEmail(email).isEmpty()) {
            Catedra cat = new Catedra();
            cat.setEmail(email);
            cat.setPassword(encoder.encode(rawPassword));
            cat.setCargo(cargo);     // si tu entidad lo tiene
            cat.setAdmin(false);
            catedraRepo.save(cat);
        }
    }

    private void createEstudianteIfMissing(
            UsuarioRepository usuarioRepo,
            EstudianteRepository estudianteRepo,
            PasswordEncoder encoder,
            String email,
            String rawPassword,
            String legajo
    ) {
        if (usuarioRepo.findByEmail(email).isEmpty()) {
            Estudiante est = new Estudiante();
            est.setEmail(email);
            est.setPassword(encoder.encode(rawPassword));
            est.setLegajo(legajo);   // si tu entidad lo tiene
            estudianteRepo.save(est);
        }
    }
}