package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class AdminLayout extends AppLayout {

    private final AuthenticationContext auth;

    // Inyectamos AuthenticationContext para poder hacer logout correcto (POST + CSRF)
    public AdminLayout(AuthenticationContext auth) {
        this.auth = auth;
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

        // === Botón Salir ===
        Button logout = new Button("Salir", e -> auth.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        logout.getStyle().set("color", "white");
        logout.setPrefixComponent(new Icon(VaadinIcon.SIGN_OUT));

        HorizontalLayout header = new HorizontalLayout(toggle, textos, logout);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(textos);
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);

        // Fondo oscuro del header
        header.getStyle()
                .set("background", "#343a40")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)");

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();
        menu.addClassNames(LumoUtility.Padding.MEDIUM);

        // Menú Admin
        menu.add(
                new RouterLink("Dashboard", AdminDashboardView.class),
                new RouterLink("Importar Datos", AdminImportView.class)
        );

        // Ver como Cátedra / Estudiante
        Button verComoCatedra = new Button("Ver como Cátedra", e -> UI.getCurrent().navigate("catedra"));
        verComoCatedra.setPrefixComponent(new Icon(VaadinIcon.EYE));
        verComoCatedra.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_CONTRAST);

        Button verComoEstudiante = new Button("Ver como Estudiante", e -> {
            com.mmhfgroup.proyectointegrador.view.util.ViewModeUtil.enableViewAsStudent();
            UI.getCurrent().navigate(""); // Home Estudiante
        });
        verComoEstudiante.setPrefixComponent(new Icon(VaadinIcon.EYE));
        verComoEstudiante.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_CONTRAST);

        menu.add(verComoCatedra, verComoEstudiante);

        addToDrawer(menu);
    }
}
