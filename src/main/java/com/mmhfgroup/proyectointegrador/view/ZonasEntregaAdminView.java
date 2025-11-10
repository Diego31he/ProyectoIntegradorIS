package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Seccion;
import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import com.mmhfgroup.proyectointegrador.repository.SeccionRepository;
import com.mmhfgroup.proyectointegrador.repository.ZonaEntregaRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;

@Route(value = "catedra/entregas-config", layout = CatedraLayout.class)
@PageTitle("Configurar Entregas")
@RolesAllowed({"ROLE_CATEDRA","ROLE_ADMIN"})
public class ZonasEntregaAdminView extends VerticalLayout {

    private final SeccionRepository seccionRepo;
    private final ZonaEntregaRepository zonaRepo;

    private final Grid<Seccion> gridSecciones = new Grid<>(Seccion.class, false);
    private final Grid<ZonaEntrega> gridZonas = new Grid<>(ZonaEntrega.class, false);

    // Form seccion
    private final TextField sTitulo = new TextField("Título de Sección");
    private final TextArea sDescripcion = new TextArea("Descripción");
    private final Button sGuardar = new Button("Crear sección");
    private final Button sEliminar = new Button("Eliminar sección seleccionada");

    // Form zona
    private final TextField zTitulo = new TextField("Título de Zona");
    private final DatePicker zFechaCierre = new DatePicker("Fecha de cierre");
    private final Button zGuardar = new Button("Agregar zona a sección seleccionada");
    private final Button zEliminar = new Button("Eliminar zona seleccionada");

    private Seccion seccionSeleccionada;
    private ZonaEntrega zonaSeleccionada;

    public ZonasEntregaAdminView(SeccionRepository seccionRepo, ZonaEntregaRepository zonaRepo) {
        this.seccionRepo = seccionRepo;
        this.zonaRepo = zonaRepo;

        setSpacing(true);
        setPadding(true);
        add(new H2("Configurar Secciones y Zonas de Entrega (generales)"));

        // Grid de Secciones
        gridSecciones.addColumn(Seccion::getTitulo).setHeader("Sección").setAutoWidth(true);
        gridSecciones.addColumn(Seccion::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        gridSecciones.setItems(seccionRepo.findAll());
        gridSecciones.asSingleSelect().addValueChangeListener(ev -> {
            seccionSeleccionada = ev.getValue();
            refreshZonas();
        });

        // Form seccion
        sDescripcion.setWidth("400px");
        HorizontalLayout seccionForm = new HorizontalLayout(sTitulo, sDescripcion, sGuardar, sEliminar);
        seccionForm.setDefaultVerticalComponentAlignment(Alignment.END);

        sGuardar.addClickListener(e -> {
            if (sTitulo.isEmpty()) {
                Notification.show("Ingresá el título de la sección");
                return;
            }
            Seccion s = new Seccion();
            s.setTitulo(sTitulo.getValue().trim());
            s.setDescripcion(sDescripcion.getValue() == null ? "" : sDescripcion.getValue().trim());
            seccionRepo.save(s);
            sTitulo.clear();
            sDescripcion.clear();
            refreshSecciones();
            Notification.show("Sección creada");
        });

        sEliminar.addClickListener(e -> {
            if (seccionSeleccionada == null) {
                Notification.show("Seleccioná una sección para eliminar");
                return;
            }
            zonaRepo.deleteAll(zonaRepo.findBySeccionId(seccionSeleccionada.getId()));
            seccionRepo.delete(seccionSeleccionada);
            seccionSeleccionada = null;
            refreshSecciones();
            gridZonas.setItems();
            Notification.show("Sección eliminada");
        });

        // Grid de Zonas
        gridZonas.addColumn(ZonaEntrega::getTitulo).setHeader("Zona").setAutoWidth(true);
        gridZonas.addColumn(ZonaEntrega::getFechaCierre).setHeader("Cierra").setAutoWidth(true);
        gridZonas.asSingleSelect().addValueChangeListener(ev -> zonaSeleccionada = ev.getValue());

        // Form zona
        zFechaCierre.setClearButtonVisible(true);
        zFechaCierre.setMin(LocalDate.now().minusYears(1));
        HorizontalLayout zonaForm = new HorizontalLayout(zTitulo, zFechaCierre, zGuardar, zEliminar);
        zonaForm.setDefaultVerticalComponentAlignment(Alignment.END);

        zGuardar.addClickListener(e -> {
            if (seccionSeleccionada == null) {
                Notification.show("Seleccioná una sección primero");
                return;
            }
            if (zTitulo.isEmpty()) {
                Notification.show("Ingresá el título de la zona");
                return;
            }
            ZonaEntrega z = new ZonaEntrega();
            z.setTitulo(zTitulo.getValue().trim());
            z.setFechaCierre(zFechaCierre.getValue());
            z.setSeccion(seccionSeleccionada);
            zonaRepo.save(z);
            zTitulo.clear();
            zFechaCierre.clear();
            refreshZonas();
            Notification.show("Zona creada");
        });

        zEliminar.addClickListener(e -> {
            if (zonaSeleccionada == null) {
                Notification.show("Seleccioná una zona");
                return;
            }
            zonaRepo.delete(zonaSeleccionada);
            zonaSeleccionada = null;
            refreshZonas();
            Notification.show("Zona eliminada");
        });

        add(
                new H2("Secciones"),
                gridSecciones,
                seccionForm,
                new H2("Zonas de la sección seleccionada"),
                gridZonas,
                zonaForm
        );
    }

    private void refreshSecciones() {
        gridSecciones.setItems(seccionRepo.findAll());
    }

    private void refreshZonas() {
        if (seccionSeleccionada == null) {
            gridZonas.setItems();
        } else {
            gridZonas.setItems(zonaRepo.findBySeccionId(seccionSeleccionada.getId()));
        }
    }
}
