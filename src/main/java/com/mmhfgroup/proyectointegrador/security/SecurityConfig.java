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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Carga usuarios por EMAIL
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return (String usernameEmail) -> {
            Usuario u = usuarioRepository.findByEmail(usernameEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usernameEmail));

            List<GrantedAuthority> auths;
            if (u instanceof Catedra c) {
                auths = Boolean.TRUE.equals(c.isAdmin())
                        ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        : List.of(new SimpleGrantedAuthority("ROLE_CATEDRA"));
            } else if (u instanceof Estudiante) {
                auths = List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
            } else {
                auths = List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
            }

            return User.withUsername(u.getEmail())
                    .password(u.getPassword()) // contraseña ENCRIPTADA desde la BD
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
        // Config base de Vaadin (recursos estáticos, rutas internas, etc.)
        super.configure(http);

        // Vista de login Vaadin
        setLoginView(http, com.mmhfgroup.proyectointegrador.view.LoginView.class);

        // 1) NO recordar la última URL (evita "/?continue")
        http.requestCache(rc -> rc.requestCache(new NullRequestCache()));

        // 2) Al autenticar, SIEMPRE ir a /post-login (tu LoginRedirectView decide por rol)
        http.formLogin(fl -> fl
                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/post-login"))
                .failureUrl("/login?error")
        );

        // 3) Logout correcto (POST + CSRF) -> /login
        http.logout(lo -> lo
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );
    }
}
