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

        // 1. El Admin va a su Dashboard
        if (securityService.isUserAdmin()) {
            event.rerouteTo(AdminDashboardView.class);

            // 2. La Cátedra (que NO es Admin) va a su vista
        } else if (securityService.isUserCatedra()) {
            // (Usamos EquiposView como "home" de Catedra)
            event.rerouteTo(EquiposView.class);

            // 3. Por defecto, el resto (Estudiantes) va a la vista principal
        } else {
            event.rerouteTo(MainView.class);
        }
    }
}