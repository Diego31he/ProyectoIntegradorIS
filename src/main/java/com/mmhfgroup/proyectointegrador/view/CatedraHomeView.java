package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "catedra", layout = CatedraLayout.class)
@PageTitle("Cátedra")
@RolesAllowed({"ROLE_CATEDRA","ROLE_ADMIN"})
public class CatedraHomeView extends VerticalLayout {
    public CatedraHomeView() {
        setPadding(true);
        setSpacing(true);
        add(new H2("Inicio Cátedra"));
        // contenido inicial o redirección a lo que quieras
    }
}
