package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.EventoCalendario;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.service.CalendarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed; // <-- Seguridad
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Calendario de Cátedra")
@Route(value = "catedra/calendario", layout = CatedraLayout.class) // <-- Layout de Cátedra
@RolesAllowed({"CATEDRA", "ADMIN"}) // <-- Seguridad
public class CalendarioCatedraView extends VerticalLayout {

    private final CalendarioService calendarioService;
    private final Usuario usuarioActual;
    private final Grid<EventoCalendario> grid = new Grid<>(EventoCalendario.class);

    @Autowired
    public CalendarioCatedraView(CalendarioService calendarioService, SecurityService securityService) {
        this.calendarioService = calendarioService;
        this.usuarioActual = securityService.getAuthenticatedUser();

        setPadding(true);
        setSpacing(true);
        add(new H2("Calendario Interactivo (Cátedra)"));

        DatePicker fecha = new DatePicker("Fecha del evento");
        TextField titulo = new TextField("Título del evento");
        TextArea descripcion = new TextArea("Descripción");
        descripcion.setWidth("300px");

        Button agregar = new Button("Agregar evento");
        Button eliminar = new Button("Eliminar seleccionado");

        agregar.addClickListener(e -> {
            if (fecha.isEmpty() || titulo.isEmpty()) {
                Notification.show("Completá al menos la fecha y el título", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            EventoCalendario nuevo = new EventoCalendario(
                    fecha.getValue(),
                    titulo.getValue(),
                    descripcion.getValue()
            );

            // Los eventos de cátedra no se asignan a un equipo,
            // pero sí al creador.
            calendarioService.agregarEvento(nuevo, usuarioActual);
            grid.setItems(calendarioService.listarEventos());

            Notification.show("Evento agregado: " + titulo.getValue(), 4000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            fecha.clear();
            titulo.clear();
            descripcion.clear();
        });

        eliminar.addClickListener(e -> {
            EventoCalendario seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                calendarioService.eliminarEvento(seleccionado);
                grid.setItems(calendarioService.listarEventos());
                Notification.show("Evento eliminado", 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        HorizontalLayout formulario = new HorizontalLayout(fecha, titulo, descripcion, agregar, eliminar);
        formulario.setDefaultVerticalComponentAlignment(Alignment.END);

        grid.setColumns("fecha", "titulo", "descripcion", "creador.nombre");
        grid.getColumnByKey("fecha").setHeader("Fecha");
        grid.getColumnByKey("titulo").setHeader("Título");
        grid.getColumnByKey("descripcion").setHeader("Descripción");
        grid.getColumnByKey("creador.nombre").setHeader("Creado por");
        grid.setItems(calendarioService.listarEventos());

        add(formulario, grid);
    }
}