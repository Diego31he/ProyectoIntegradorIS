package com.mmhfgroup.proyectointegrador.security;

import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // <-- IMPORT AÑADIDO
import org.springframework.security.core.GrantedAuthority; // <-- IMPORT AÑADIDO
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Collection; // <-- IMPORT AÑADIDO

@Service
public class SecurityService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene el objeto Usuario completo desde la base de datos
     * basado en el usuario actualmente logueado.
     */
    public Usuario getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Usuario logueado '" + username + "' no encontrado en la BD."));
    }

    // --- INICIO DE MÉTODOS AÑADIDOS ---

    /**
     * Comprueba si el usuario actual tiene el rol de ADMIN.
     */
    public boolean isUserAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * Comprueba si el usuario actual tiene el rol de CATEDRA.
     */
    public boolean isUserCatedra() {
        return hasRole("ROLE_CATEDRA");
    }

    /**
     * Método helper privado para verificar un rol específico.
     */
    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    // --- FIN DE MÉTODOS AÑADIDOS ---

    public void logout() {
        UI.getCurrent().getPage().setLocation("/");
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}