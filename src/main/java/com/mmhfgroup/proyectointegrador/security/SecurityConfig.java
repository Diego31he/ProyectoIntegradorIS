package com.mmhfgroup.proyectointegrador.security;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import com.mmhfgroup.proyectointegrador.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Define un "codificador" de contraseñas.
    // Todas las contraseñas en la BD deben guardarse usando esto.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Redirige a la vista de Login que crearemos
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    // 2. Define cómo buscar un usuario en tu base de datos
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + email));

            // 3. Asigna roles basados en el TIPO de usuario
            List<GrantedAuthority> authorities = new ArrayList<>();

            if (usuario instanceof Estudiante) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));

            } else if (usuario instanceof Catedra) {
                authorities.add(new SimpleGrantedAuthority("ROLE_CATEDRA"));
                // Si es Cátedra Y es Admin, tiene ambos roles
                if (((Catedra) usuario).isAdmin()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
            }

            // 4. Devuelve el usuario que Spring Security entiende
            return new org.springframework.security.core.userdetails.User(
                    usuario.getEmail(),
                    usuario.getPassword(), // La contraseña ya debe estar encriptada en la BD
                    authorities
            );
        };
    }
}