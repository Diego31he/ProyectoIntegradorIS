package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.repository.CatedraRepository;
import com.mmhfgroup.proyectointegrador.repository.EquipoRepository;
import com.mmhfgroup.proyectointegrador.repository.EstudianteRepository;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UsuarioRepository usuarioRepo,
            EstudianteRepository estudianteRepo,
            CatedraRepository catedraRepo,
            EquipoRepository equipoRepo,
            PasswordEncoder encoder // <- usa el bean de SecurityConfig
    ) {
        return args -> {
            seedEquipos(equipoRepo);
            seedUsuarios(usuarioRepo, estudianteRepo, catedraRepo, equipoRepo, encoder);
        };
    }

    @Transactional
    void seedEquipos(EquipoRepository equipoRepo) {
        createEquipoIfMissing(equipoRepo, 1, "Equipo 1", "Auditor 1");
        createEquipoIfMissing(equipoRepo, 2, "Equipo 2", "Auditor 2");
        createEquipoIfMissing(equipoRepo, 3, "Equipo 3", "Auditor 3");
    }

    private void createEquipoIfMissing(EquipoRepository equipoRepo, int numero, String nombre, String auditor) {
        if (!equipoRepo.existsByNumero(numero)) {
            Equipo eq = new Equipo(numero, nombre, auditor);
            equipoRepo.save(eq);
        }
    }

    @Transactional
    void seedUsuarios(
            UsuarioRepository usuarioRepo,
            EstudianteRepository estudianteRepo,
            CatedraRepository catedraRepo,
            EquipoRepository equipoRepo,
            PasswordEncoder encoder
    ) {
        // ADMIN (Cátedra con flag admin=true)
        createAdminIfMissing(usuarioRepo, catedraRepo, encoder,
                "admin@demo.com", "Admin", "MMHF", "admin", "Administrador");

        // CÁTEDRA (no admin)
        createCatedraIfMissing(usuarioRepo, catedraRepo, encoder,
                "catedra@demo.com", "Docente", "MMHF", "catedra", "Profesor");

        // ESTUDIANTE 1 -> Equipo 1
        createEstudianteIfMissing(usuarioRepo, estudianteRepo, equipoRepo, encoder,
                "alumno@demo.com", "Juan", "Pérez", "alumno", "LEG-1001", 1);

        // ESTUDIANTE 2 -> Equipo 2
        createEstudianteIfMissing(usuarioRepo, estudianteRepo, equipoRepo, encoder,
                "alumno2@demo.com", "María", "Gómez", "alumno", "LEG-1002", 2);
    }

    private void createAdminIfMissing(
            UsuarioRepository usuarioRepo,
            CatedraRepository catedraRepo,
            PasswordEncoder encoder,
            String email,
            String nombre,
            String apellido,
            String rawPassword,
            String cargo
    ) {
        if (usuarioRepo.findByEmail(email).isEmpty()) {
            Catedra admin = new Catedra();
            admin.setNombre(nombre);
            admin.setApellido(apellido);
            admin.setEmail(email);
            admin.setPassword(encoder.encode(rawPassword));
            admin.setCargo(cargo);
            admin.setAdmin(true); // ADMIN
            catedraRepo.save(admin);
        }
    }

    private void createCatedraIfMissing(
            UsuarioRepository usuarioRepo,
            CatedraRepository catedraRepo,
            PasswordEncoder encoder,
            String email,
            String nombre,
            String apellido,
            String rawPassword,
            String cargo
    ) {
        if (usuarioRepo.findByEmail(email).isEmpty()) {
            Catedra cat = new Catedra();
            cat.setNombre(nombre);
            cat.setApellido(apellido);
            cat.setEmail(email);
            cat.setPassword(encoder.encode(rawPassword));
            cat.setCargo(cargo);
            cat.setAdmin(false); // CÁTEDRA simple
            catedraRepo.save(cat);
        }
    }

    private void createEstudianteIfMissing(
            UsuarioRepository usuarioRepo,
            EstudianteRepository estudianteRepo,
            EquipoRepository equipoRepo,
            PasswordEncoder encoder,
            String email,
            String nombre,
            String apellido,
            String rawPassword,
            String legajo,
            Integer equipoNumero // null = sin equipo asignado
    ) {
        if (usuarioRepo.findByEmail(email).isEmpty()) {
            Estudiante est = new Estudiante();
            est.setNombre(nombre);
            est.setApellido(apellido);
            est.setEmail(email);
            est.setPassword(encoder.encode(rawPassword));
            est.setLegajo(legajo);

            if (equipoNumero != null) {
                equipoRepo.findByNumero(equipoNumero)
                        .ifPresent(est::setEquipo);
            }
            estudianteRepo.save(est);
        }
    }
}
