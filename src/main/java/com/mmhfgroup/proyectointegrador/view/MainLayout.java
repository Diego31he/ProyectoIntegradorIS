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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        // --- Logo del grupo ---
        Image logo = new Image("images/mmhf_logo.png", "MMHF Logo");

// Ajustamos el tamaño (más chico que el actual)
//        logo.setWidth("120px");
        logo.setHeight("120px");

// Bordes redondeados suaves
        logo.getStyle()
                .set("border-radius", "5px") // bordes redondeados sin llegar a ser círculo
                .set("object-fit", "cover")   // mantiene el contenido centrado y bien recortado
                //.set("margin-right", "30px")  // ajusta el espacio a la derecha
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)"); // opcional: sombra suave



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
        HorizontalLayout header = new HorizontalLayout(toggle, logo, textos, logout);
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


    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();
        menu.addClassNames(LumoUtility.Padding.MEDIUM);

        menu.add(
                new RouterLink("Inicio", MainView.class),
                new RouterLink("Equipos", EquiposView.class),
                new RouterLink("Entregas", EntregasView.class),
                new RouterLink("Notificaciones", NotificacionesView.class),
                new RouterLink("Calendario", CalendarioView.class),
                new RouterLink("Mensajeria", ForoView.class)
        );

        addToDrawer(menu);
    }

}
