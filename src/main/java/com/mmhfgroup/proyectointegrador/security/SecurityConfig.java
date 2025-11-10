package com.mmhfgroup.proyectointegrador.security;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.util.List;

@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    // Encoder único del sistema
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cargamos usuario por EMAIL
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return (String usernameEmail) -> {
            Usuario u = usuarioRepository.findByEmail(usernameEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usernameEmail));

            List<GrantedAuthority> auths;
            if (u instanceof Catedra c) {
                if (Boolean.TRUE.equals(c.isAdmin())) {
                    auths = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                } else {
                    auths = List.of(new SimpleGrantedAuthority("ROLE_CATEDRA"));
                }
            } else if (u instanceof Estudiante) {
                auths = List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
            } else {
                auths = List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
            }

            return User.withUsername(u.getEmail())
                    .password(u.getPassword()) // ya encriptada en BD
                    .authorities(auths)
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Login view de Vaadin
        setLoginView(http, com.mmhfgroup.proyectointegrador.view.LoginView.class);

        // 🔒 Desactivar RequestCache para que NO use "?continue"
        http.requestCache(c -> c.requestCache(new NullRequestCache()));

        // Form login con success handler por ROL (sin SavedRequestAware)
        http.formLogin(form -> form
                .loginPage("/login")
                .successHandler((req, res, auth) -> {
                    var roles = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();

                    String ctx = req.getContextPath(); // usualmente "", por si deployan con context-path
                    String target;
                    if (roles.contains("ROLE_ADMIN")) {
                        target = ctx + "/admin";
                    } else if (roles.contains("ROLE_CATEDRA")) {
                        target = ctx + "/catedra";
                    } else {
                        target = ctx + "/"; // estudiantes
                    }

                    // Redirección final
                    res.sendRedirect(target);
                })
                .failureUrl("/login?error")
        );

        // Logout claro y completo
        http.logout(l -> l
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        // Llamar al super al final para que Vaadin aplique su config adicional
        super.configure(http);
    }
}
