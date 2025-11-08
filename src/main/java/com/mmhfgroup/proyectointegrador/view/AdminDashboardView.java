package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/dashboard", layout = AdminLayout.class) // <-- Usa AdminLayout
@PageTitle("Admin Dashboard")
@RolesAllowed("ROLE_ADMIN")
public class AdminDashboardView extends VerticalLayout {

    public AdminDashboardView() {
        add(new H1("Bienvenido, Administrador"));
        add("Panel de gestión del sistema.");
    }
}