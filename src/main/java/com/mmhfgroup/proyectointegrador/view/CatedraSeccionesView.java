package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Seccion;
import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import com.mmhfgroup.proyectointegrador.repository.SeccionRepository;
import com.mmhfgroup.proyectointegrador.repository.ZonaEntregaRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification; // <-- IMPORT AÑADIDO
import com.vaadin.flow.component.notification.Notification.Position; // <-- IMPORT AÑADIDO
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Gestionar Secciones")
@Route(value = "catedra/secciones", layout = CatedraLayout.class)
@RolesAllowed({"ROLE_CATEDRA", "ROLE_ADMIN"})
public class CatedraSeccionesView extends HorizontalLayout {

    private final SeccionRepository seccionRepo;
    private final ZonaEntregaRepository zonaRepo;

    private Grid<Seccion> gridSecciones = new Grid<>(Seccion.class);
    private Grid<ZonaEntrega> gridZonas = new Grid<>(ZonaEntrega.class);

    private TextField tituloSeccion = new TextField("Título de la Sección");
    private TextField descripcionSeccion = new TextField("Descripción");

    private TextField tituloZona = new TextField("Título de la Zona");
    private DatePicker fechaCierreZona = new DatePicker("Fecha de Cierre");

    private Seccion seccionSeleccionada;

    public CatedraSeccionesView(SeccionRepository seccionRepo, ZonaEntregaRepository zonaRepo) {
        this.seccionRepo = seccionRepo;
        this.zonaRepo = zonaRepo;

        setSizeFull();

        // --- Panel Izquierdo: Secciones ---
        VerticalLayout panelSecciones = new VerticalLayout();
        panelSecciones.setWidth("50%");
        panelSecciones.add(new H2("Secciones de Entregas"));

        HorizontalLayout formSeccion = new HorizontalLayout(
                tituloSeccion,
                descripcionSeccion,
                new Button("Crear Sección", e -> crearSeccion())
        );
        formSeccion.setDefaultVerticalComponentAlignment(Alignment.END);

        gridSecciones.setColumns("titulo", "descripcion");
        gridSecciones.setSelectionMode(SelectionMode.SINGLE);
        gridSecciones.asSingleSelect().addValueChangeListener(e -> {
            this.seccionSeleccionada = e.getValue();
            refrescarGridZonas();
        });

        panelSecciones.add(formSeccion, gridSecciones);

        // --- Panel Derecho: Zonas de Entrega ---
        VerticalLayout panelZonas = new VerticalLayout();
        panelZonas.setWidth("50%");
        panelZonas.add(new H2("Zonas de Entrega"));

        HorizontalLayout formZona = new HorizontalLayout(tituloZona, fechaCierreZona, new Button("Crear Zona", e -> crearZona()));
        formZona.setDefaultVerticalComponentAlignment(Alignment.END);

        gridZonas.setColumns("titulo", "fechaCierre");
        panelZonas.add(formZona, gridZonas);

        add(panelSecciones, panelZonas);
        refrescarGridSecciones();
        refrescarGridZonas();
    }

    private void crearSeccion() {
        if (!tituloSeccion.isEmpty()) {
            Seccion s = new Seccion();
            s.setTitulo(tituloSeccion.getValue());
            s.setDescripcion(descripcionSeccion.getValue());
            seccionRepo.save(s);
            refrescarGridSecciones();
            tituloSeccion.clear();
            descripcionSeccion.clear();
        }
    }

    // --- MÉTODO CORREGIDO ---
    private void crearZona() {

        // 1. Comprobar que haya una sección seleccionada
        if (seccionSeleccionada == null) {
            Notification.show("Error: Debe seleccionar una sección primero.", 3000, Position.MIDDLE);
            return; // Detiene la ejecución
        }

        // 2. Comprobar que el título de la zona no esté vacío
        if (tituloZona.isEmpty()) {
            Notification.show("Error: La zona debe tener un título.", 3000, Position.MIDDLE);
            return; // Detiene la ejecución
        }

        // Si ambas comprobaciones pasan, crea la zona
        ZonaEntrega z = new ZonaEntrega();
        z.setTitulo(tituloZona.getValue());
        if (!fechaCierreZona.isEmpty()) {
            z.setFechaCierre(fechaCierreZona.getValue());
        }
        z.setSeccion(seccionSeleccionada);
        zonaRepo.save(z);

        refrescarGridZonas(); // Actualiza la grilla de zonas
        tituloZona.clear();
        fechaCierreZona.clear();
    }

    private void refrescarGridSecciones() {
        gridSecciones.setItems(seccionRepo.findAll());
    }

    private void refrescarGridZonas() {
        if (seccionSeleccionada != null) {
            gridZonas.setItems(zonaRepo.findBySeccionId(seccionSeleccionada.getId()));
        } else {
            gridZonas.setItems();
        }
    }
}
