package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.repository.EstudianteRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
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

import java.util.List;

@Route(value = "catedra", layout = CatedraLayout.class)
@PageTitle("Inicio Cátedra | Proyecto Integrador")
@RolesAllowed({"ROLE_CATEDRA","ROLE_ADMIN"})
public class CatedraHomeView extends VerticalLayout {

    private final EstudianteRepository estudianteRepository;
    private final Grid<Estudiante> grid = new Grid<>(Estudiante.class, false);

    public CatedraHomeView(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        // --- Título principal ---
        H1 titulo = new H1("Panel de Cátedra");
        titulo.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "700")
                .set("margin-bottom", "30px");

        // --- Contenedor principal de accesos rápidos (igual estilo que MainView) ---
        HorizontalLayout accesos = new HorizontalLayout();
        accesos.setSpacing(true);
        accesos.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        accesos.setWidth("100%");

        accesos.add(
                crearOpcion(VaadinIcon.GROUP, "Equipos", "#2196F3", "equipos"),
                crearOpcion(VaadinIcon.UPLOAD, "Entregas", "#9C27B0", "entregas"),
                crearOpcion(VaadinIcon.BELL, "Notificaciones", "#4CAF50", "notificaciones"),
                crearOpcion(VaadinIcon.CALENDAR, "Calendario", "#FFC107", "calendario"),
                crearOpcion(VaadinIcon.MAILBOX, "Mensajería", "#E91E63", "foro")
        );

        // --- Sección Estudiantes ---
        VerticalLayout bloqueEstudiantes = new VerticalLayout();
        bloqueEstudiantes.setWidthFull();
        bloqueEstudiantes.setMaxWidth("1200px");
        bloqueEstudiantes.setPadding(false);
        bloqueEstudiantes.setSpacing(false);
        bloqueEstudiantes.getStyle().set("margin-top", "24px");

        Paragraph subtitulo = new Paragraph("Estudiantes");
        subtitulo.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "700")
                .set("margin", "8px 0 8px 0")
                .set("align-self", "flex-start");

        // Grid de estudiantes
        grid.addColumn(Estudiante::getApellido).setHeader("Apellido").setAutoWidth(true).setSortable(true);
        grid.addColumn(Estudiante::getNombre).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        grid.addColumn(Estudiante::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Estudiante::getLegajo).setHeader("Legajo").setAutoWidth(true);
        grid.addColumn(e -> e.getEquipo() == null ? "Sin asignar" : String.valueOf(e.getEquipo().getNombre()))
                .setHeader("Equipo")
                .setAutoWidth(true);

        grid.setWidthFull();
        grid.getStyle().set("background", "white");

        // Datos
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        grid.setItems(estudiantes);

        bloqueEstudiantes.add(subtitulo, grid);

        add(titulo, accesos, bloqueEstudiantes);
    }

    // --- helper para accesos rápidos (copiado/compat. con MainView) ---
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

        fondoIcono.getElement().addEventListener("mouseenter", e -> fondoIcono.getStyle().set("transform", "scale(1.05)"));
        fondoIcono.getElement().addEventListener("mouseleave", e -> fondoIcono.getStyle().set("transform", "scale(1)"));

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
