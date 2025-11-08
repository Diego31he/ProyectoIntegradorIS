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
import com.mmhfgroup.proyectointegrador.view.MainView; // <-- Importar MainView
import com.mmhfgroup.proyectointegrador.view.EquiposView; // <-- Importar EquiposView
import com.vaadin.flow.component.icon.Icon; // <-- Importar Icon
import com.vaadin.flow.component.icon.VaadinIcon;
import com.mmhfgroup.proyectointegrador.model.Catedra;

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
                new RouterLink("Dashboard", AdminDashboardView.class),
                // Esta es la línea correcta (la que tú tenías):
                new RouterLink("Importar Datos", AdminImportView.class)
        );

        // --- AÑADIR ESTO (Los botones de "Ver como") ---
        menu.add(LumoUtility.Margin.Top.LARGE);

        // Link para "Ver como Cátedra"
        RouterLink vistaCatedra = new RouterLink();
        vistaCatedra.add(new Icon(VaadinIcon.EYE), new Span("Ver como Cátedra"));
        vistaCatedra.setRoute(EquiposView.class); // (Página principal de Cátedra)
        menu.add(vistaCatedra);

        // Link para "Ver como Estudiante"
        RouterLink vistaEstudiante = new RouterLink();
        vistaEstudiante.add(new Icon(VaadinIcon.EYE), new Span("Ver como Estudiante"));
        vistaEstudiante.setRoute(MainView.class); // (Página principal de Estudiante)
        menu.add(vistaEstudiante);

        menu.add(LumoUtility.Margin.Top.AUTO); // Empuja el link de "Volver"
        // --- FIN DE LO AÑADIDO ---

        menu.add(new RouterLink("← Volver al Sitio", MainView.class));
        addToDrawer(menu);
    }
}