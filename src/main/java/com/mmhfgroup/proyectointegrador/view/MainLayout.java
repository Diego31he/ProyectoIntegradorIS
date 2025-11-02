package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode; // <-- NUEVA
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();



        // --- Título principal y subtítulo ---
        H2 titulo = new H2("Proyecto Integrador");
        titulo.getStyle()
                .set("margin", "0")
                .set("font-weight", "700")
                .set("color", "white");

        Span subtitulo = new Span("MMHF Group — Ingeniería de Software");
        subtitulo.getStyle()
                .set("font-size", "13px")
                .set("color", "rgba(255,255,255,0.85)");

        VerticalLayout textos = new VerticalLayout(titulo, subtitulo);
        textos.setPadding(false);
        textos.setSpacing(false);
        textos.setAlignItems(Alignment.START);

        // --- Botón de salida (ahora queda discreto en la esquina) ---
        Button logout = new Button("Salir");
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        logout.getStyle().set("color", "white");
        logout.addClickListener(e -> UI.getCurrent().navigate(""));

        // --- Header completo ---
        HorizontalLayout header = new HorizontalLayout(toggle, textos, logout);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(textos);
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);

        // Fondo con gradiente azul
        header.getStyle()
                .set("background", "linear-gradient(90deg, #1E88E5, #42A5F5)")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)");

        addToNavbar(header);
    }

    private VerticalLayout createSocialLink(String label, Icon icon, String url) {
        Anchor link = new Anchor(url, icon);
        link.setTarget("_blank");
        link.setTitle(label); // Tooltip al pasar el mouse

        // Etiqueta de texto ("Instagram", "X", "Web")
        Span text = new Span(label);
        text.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)");

        // Columna vertical que agrupa el texto y el icono
        VerticalLayout column = new VerticalLayout(text, link);
        column.setAlignItems(Alignment.CENTER);
        column.setPadding(false);
        column.setSpacing(false);
        return column;
    }

    // --- REEMPLAZA TU MÉTODO createDrawer() POR ESTE ---
    private void createDrawer() {
        // El VerticalLayout principal del drawer
        VerticalLayout menu = new VerticalLayout();
        menu.addClassNames(LumoUtility.Padding.MEDIUM);
        menu.setSizeFull(); // Importante: para que el spacer funcione

        // --- Links de Navegación (los que ya tenías) ---
        VerticalLayout navLinks = new VerticalLayout(
                new RouterLink("Inicio", MainView.class),
                new RouterLink("Equipos", EquiposView.class),
                new RouterLink("Entregas", EntregasView.class),
                new RouterLink("Notificaciones", NotificacionesView.class),
                new RouterLink("Calendario", CalendarioView.class),
                new RouterLink("Mensajeria", ForoView.class)
        );
        navLinks.setPadding(false);
        navLinks.setSpacing(false);

        // --- Spacer: un div vacío que ocupa el espacio restante ---
        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");


        // --- 1. Contenido de los iconos (para el colapsable) ---
        HorizontalLayout socialIconsLayout = new HorizontalLayout();
        socialIconsLayout.setWidthFull();
        socialIconsLayout.setJustifyContentMode(JustifyContentMode.EVENLY); // Espacio equilibrado
        socialIconsLayout.setPadding(false);

        // Usamos el método auxiliar
        socialIconsLayout.add(
                createSocialLink("Instagram", new Icon(VaadinIcon.CAMERA), "https://www.instagram.com/"),
                createSocialLink("X", new Icon(VaadinIcon.TWITTER), "https://www.x.com/"),
                createSocialLink("Web", new Icon(VaadinIcon.GLOBE), "http://mmhfgroup.com.ar")
        );

        // --- 3. Layout completo del colapsable (iconos + link de texto) ---
        VerticalLayout detailsContent = new VerticalLayout(socialIconsLayout);
        detailsContent.setAlignItems(Alignment.CENTER);
        detailsContent.setSpacing(true);
        detailsContent.setPadding(false);
        detailsContent.getStyle().set("padding-top", "var(--lumo-space-s)"); // Espacio arriba


        // --- Título "Nosotros" con el estilo de los otros links ---
        Span nosotrosSummary = new Span("Nosotros");
        nosotrosSummary.getStyle()
                .set("font-size", "var(--lumo-font-size-m)") // Mismo tamaño de fuente
                .set("font-weight", "500")                  // Mismo peso
                .set("color", "var(--lumo-body-text-color)"); // Mismo color


        // --- Componente colapsable "Nosotros" ---
        Details nosotrosDetails = new Details(nosotrosSummary, detailsContent);
        nosotrosDetails.setWidthFull(); // Que ocupe todo el ancho


        // Añadir todo al menú del drawer en orden
        menu.add(navLinks, spacer, nosotrosDetails);

        addToDrawer(menu);
    }
    // --- FIN DEL MÉTODO REEMPLAZADO ---

}

