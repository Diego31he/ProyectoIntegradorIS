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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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

        // Permite acceso público a imágenes y favicons (necesario)
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/icons/**")).permitAll()
        );

        // Llama a la configuración de Vaadin (esto es clave)
        super.configure(http);

        // Configura el logout (el que usan tus botones de "Salir")
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );

        // ¡ESTA ES LA MAGIA!
        // Le dice a Vaadin: usa LoginView para /login
        // y redirige a /post-login al tener éxito.
        setLoginView(http, LoginView.class, "/post-login");
    }
    // 2. Define cómo buscar un usuario en tu base de datos
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + email));

            List<GrantedAuthority> authorities = new ArrayList<>();

            // 1. Todos los usuarios logueados son, como mínimo, "ESTUDIANTE"
            //    (O ajusta esto si tienes usuarios que NO son estudiantes)
            authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));

            if (usuario instanceof Catedra) {
                // 2. Si es Cátedra, AÑADE el rol CATEDRA
                authorities.add(new SimpleGrantedAuthority("ROLE_CATEDRA"));

                // 3. Si además es Admin, AÑADE el rol ADMIN
                if (((Catedra) usuario).isAdmin()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
            }

            return new org.springframework.security.core.userdetails.User(
                    usuario.getEmail(),
                    usuario.getPassword(),
                    authorities
            );
        };
    }
}