package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Catedra;
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
        // 1. Revisa si ya hay usuarios. Si hay, no hace nada.
        if (usuarioRepository.count() == 0) {

            System.out.println("No hay usuarios. Creando usuario ADMIN de prueba...");

            // 2. Crea un usuario de Cátedra que también es Admin
            Catedra admin = new Catedra(
                    "Admin",
                    "MMHF",
                    "admin@mmhf.com", // Este será el usuario de login
                    passwordEncoder.encode("admin"), // ¡IMPORTANTE! Encripta el password "admin"
                    "Administrador del Sistema",
                    true // <-- true = es Admin
            );

            // 3. Guarda el usuario en la base de datos
            usuarioRepository.save(admin);

            System.out.println("Usuario ADMIN creado: admin@mmhf.com / (pass: admin)");
        }
    }
}