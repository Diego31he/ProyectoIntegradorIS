package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "", layout = EstudianteLayout.class)
@PageTitle("Inicio | Proyecto Integrador")
@RolesAllowed("ROLE_ESTUDIANTE")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        // --- Título principal ---
        H1 titulo = new H1("Sistema del Proyecto Integrador");
        titulo.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "700")
                .set("margin-bottom", "30px");

        // --- Contenedor principal de iconos ---
        HorizontalLayout contenedor = new HorizontalLayout();
        contenedor.setSpacing(true);
        contenedor.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        contenedor.setWidth("100%");

        // --- Íconos tipo “tarjeta cuadrada” ---
        contenedor.add(
                crearOpcion(VaadinIcon.GROUP, "Equipos", "#2196F3", "equipos"),
                crearOpcion(VaadinIcon.UPLOAD, "Entregas", "#9C27B0", "entregas"),
                crearOpcion(VaadinIcon.BELL, "Notificaciones", "#4CAF50", "notificaciones"),
                crearOpcion(VaadinIcon.CALENDAR, "Calendario", "#FFC107", "calendario"),
                crearOpcion(VaadinIcon.MAILBOX, "Mensajería", "#E91E63", "foro")
        );

        add(titulo, contenedor);
    }

    // --- Método auxiliar: crea un “botón cuadrado” tipo app launcher ---
    private VerticalLayout crearOpcion(VaadinIcon icono, String texto, String colorFondo, String ruta) {
        Icon icon = icono.create();
        icon.setColor("white");
        icon.setSize("40px");

        Div fondoIcono = new Div(icon);
        fondoIcono.getStyle()
                .set("background-color", colorFondo)
                .set("width", "80px")
                .set("height", "80px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("border-radius", "10px")
                .set("box-shadow", "2px 2px 6px rgba(0,0,0,0.2)")
                .set("cursor", "pointer")
                .set("transition", "transform 0.2s ease-in-out");

        // Efecto hover
        fondoIcono.getElement().addEventListener("mouseenter", e -> fondoIcono.getStyle().set("transform", "scale(1.05)"));
        fondoIcono.getElement().addEventListener("mouseleave", e -> fondoIcono.getStyle().set("transform", "scale(1)"));

        // Acción al hacer clic
        fondoIcono.addClickListener(e -> {
            if (ruta == null || ruta.isEmpty()) {
                UI.getCurrent().navigate("");
            } else {
                UI.getCurrent().navigate(ruta);
            }
        });

        Paragraph etiqueta = new Paragraph(texto);
        etiqueta.getStyle()
                .set("margin", "8px 0 0 0")
                .set("font-weight", "600")
                .set("color", "#666")
                .set("font-size", "14px")
                .set("text-align", "center");

        VerticalLayout card = new VerticalLayout(fondoIcono, etiqueta);
        card.setAlignItems(Alignment.CENTER);
        card.setPadding(false);
        card.setSpacing(false);
        card.getStyle().set("margin", "0 20px");

        return card;
    }
}
