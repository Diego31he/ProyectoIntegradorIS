package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ROLE_ADMIN") // <-- SOLO Admin
public class AdminLayout extends AppLayout {

    public AdminLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        H2 titulo = new H2("Panel de Administración");
        titulo.getStyle().set("margin", "0").set("font-weight", "700").set("color", "white");
        Span subtitulo = new Span("Gestión del Sistema");
        subtitulo.getStyle().set("font-size", "13px").set("color", "rgba(255,255,255,0.85)");

        VerticalLayout textos = new VerticalLayout(titulo, subtitulo);
        textos.setPadding(false);
        textos.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(toggle, textos);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(textos);
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);

        // Un color diferente (ej. gris oscuro)
        header.getStyle()
                .set("background", "#343a40")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)");

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();
        menu.addClassNames(LumoUtility.Padding.MEDIUM);

        // --- Menú Específico de Admin ---
        menu.add(
                new RouterLink("Dashboard Admin", AdminDashboardView.class),
                new RouterLink("Importar Datos", AdminImportView.class)
                // (Aquí irán "Gestionar Usuarios", etc.)
        );

        // --- Link para volver al sitio (vista de Cátedra) ---
        menu.add(LumoUtility.Margin.Top.LARGE);
        menu.add(new RouterLink("← Volver a Vista Cátedra", EquiposView.class));

        addToDrawer(menu);
    }
}