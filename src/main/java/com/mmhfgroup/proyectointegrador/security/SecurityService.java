package com.mmhfgroup.proyectointegrador.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    /**
     * Obtiene el objeto de autenticación del usuario actual.
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Comprueba si el usuario autenticado actualmente tiene un rol específico.
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si el usuario actual es ROLE_ADMIN.
     */
    public boolean isUserAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * Comprueba si el usuario actual es ROLE_CATEDRA.
     */
    public boolean isUserCatedra() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_CATEDRA")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si el usuario actual es ROLE_ESTUDIANTE.
     */
    public boolean isUserEstudiante() {
        return hasRole("ROLE_ESTUDIANTE");
    }
}