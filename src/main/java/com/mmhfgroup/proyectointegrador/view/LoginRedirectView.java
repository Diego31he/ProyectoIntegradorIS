package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("post-login") // URL de redirección
@PermitAll
public class LoginRedirectView extends VerticalLayout implements BeforeEnterObserver {

    private final SecurityService securityService;

    public LoginRedirectView(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Redirige al usuario basado en su rol

        if (securityService.isUserAdmin()) {
            // 1. Si es Admin, va al dashboard de admin
            event.rerouteTo(AdminDashboardView.class);

        } else if (securityService.isUserCatedra()) {
            // 2. Si es Cátedra (pero no admin), va a la vista de Equipos
            event.rerouteTo(EquiposView.class);

        } else if (securityService.isUserEstudiante()) {
            // 3. Si es Estudiante, va a la vista principal
            event.rerouteTo(MainView.class);

        } else {
            // Caso por defecto (no debería pasar si está logueado)
            event.rerouteTo(LoginView.class);
        }
    }
}