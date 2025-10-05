package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.mmhfgroup.proyectointegrador.model.EventoCalendario;
import com.mmhfgroup.proyectointegrador.service.CalendarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Calendario")
@Route(value = "calendario", layout = MainLayout.class)
public class CalendarioView extends VerticalLayout {

    private final CalendarioService servicio = new CalendarioService();
    private final NotificacionService notificacionService = new NotificacionService();
    private final Grid<EventoCalendario> grid = new Grid<>(EventoCalendario.class);

    public CalendarioView() {
        setPadding(true);
        setSpacing(true);
        add(new H2("Calendario Interactivo"));

        DatePicker fecha = new DatePicker("Fecha del evento");
        TextField titulo = new TextField("Título del evento");
        TextArea descripcion = new TextArea("Descripción");
        descripcion.setWidth("300px");

        Button agregar = new Button("Agregar evento");
        Button eliminar = new Button("Eliminar seleccionado");

        // --- Acción: agregar evento ---
        agregar.addClickListener(e -> {
            if (fecha.isEmpty() || titulo.isEmpty()) {
                Notification notif = Notification.show("⚠️ Completá al menos la fecha y el título", 3000, Notification.Position.MIDDLE);
                notif.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            EventoCalendario nuevo = new EventoCalendario(
                    fecha.getValue(),
                    titulo.getValue(),
                    descripcion.getValue()
            );

            servicio.agregarEvento(nuevo);
            grid.setItems(servicio.listarEventos());

            // ✅ Registrar en historial de notificaciones
            String mensaje = "📅 Nuevo evento agregado: " + titulo.getValue() + " (" + fecha.getValue() + ")";
            notificacionService.agregarNotificacion(mensaje);

            // Mostrar notificación visual
            Notification notif = Notification.show(mensaje, 4000, Notification.Position.BOTTOM_CENTER);
            notif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            fecha.clear();
            titulo.clear();
            descripcion.clear();
        });

        // --- Acción: eliminar evento ---
        eliminar.addClickListener(e -> {
            EventoCalendario seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                servicio.eliminarEvento(seleccionado);
                grid.setItems(servicio.listarEventos());

                // ✅ Registrar en historial de notificaciones
                String mensaje = "🗑️ Evento eliminado: " + seleccionado.getTitulo();
                notificacionService.agregarNotificacion(mensaje);

                Notification notif = Notification.show("Evento eliminado", 3000, Notification.Position.BOTTOM_CENTER);
                notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        HorizontalLayout formulario = new HorizontalLayout(fecha, titulo, descripcion, agregar, eliminar);
        formulario.setDefaultVerticalComponentAlignment(Alignment.END);

        grid.setColumns("fecha", "titulo", "descripcion");
        grid.getColumnByKey("fecha").setHeader("Fecha");
        grid.getColumnByKey("titulo").setHeader("Título");
        grid.getColumnByKey("descripcion").setHeader("Descripción");
        grid.setItems(servicio.listarEventos());

        add(formulario, grid);
    }
}
