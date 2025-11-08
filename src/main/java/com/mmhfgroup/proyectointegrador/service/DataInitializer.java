package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (usuarioRepository.count() == 0) {

            System.out.println("No hay usuarios. Creando usuarios de prueba...");

            // 1. Usuario ADMIN (Cátedra con isAdmin = true)
            Catedra admin = new Catedra(
                    "Admin",
                    "MMHF",
                    "admin@mmhf.com",
                    passwordEncoder.encode("admin"), // pass: admin
                    "Administrador del Sistema",
                    true // <-- es Admin
            );
            usuarioRepository.save(admin);

            // 2. Usuario DOCENTE (Cátedra con isAdmin = false)
            Catedra docente = new Catedra(
                    "Docente",
                    "Prueba",
                    "docente@mmhf.com",
                    passwordEncoder.encode("docente"), // pass: docente
                    "Jefe de Trabajos Prácticos",
                    false // <-- NO es Admin
            );
            usuarioRepository.save(docente);

            // 3. Usuario ESTUDIANTE
            Estudiante estudiante = new Estudiante(
                    "Estudiante",
                    "Prueba",
                    "estudiante@mmhf.com",
                    passwordEncoder.encode("estudiante"), // pass: estudiante
                    "12345" // Legajo
            );
            usuarioRepository.save(estudiante);

            System.out.println("Usuarios de prueba creados.");
        }
    }
}