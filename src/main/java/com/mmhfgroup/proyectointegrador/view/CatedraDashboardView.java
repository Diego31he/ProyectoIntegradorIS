package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "catedra/dashboard", layout = CatedraLayout.class) // <-- Usa AdminLayout
@PageTitle("Catedra Dashboard")
@RolesAllowed({"ROLE_CATEDRA","ROLE_ADMIN"})
public class CatedraDashboardView extends VerticalLayout {

    public CatedraDashboardView() {
        add(new H1("Bienvenido, Profesor"));
        add("Panel de gestión de equipos.");
    }
}