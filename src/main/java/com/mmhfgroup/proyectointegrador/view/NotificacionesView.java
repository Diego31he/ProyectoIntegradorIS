package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Notificacion;
import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Notificaciones")
@Route(value = "notificaciones", layout = EstudianteLayout.class)
public class NotificacionesView extends VerticalLayout {

    private final NotificacionService servicio = new NotificacionService();

    public NotificacionesView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Centro de Notificaciones"));

        Grid<Notificacion> grid = new Grid<>(Notificacion.class);
        grid.setColumns("mensaje", "fechaHora");
        grid.getColumnByKey("mensaje").setHeader("Mensaje");
        grid.getColumnByKey("fechaHora").setHeader("Fecha y Hora");

        grid.setItems(servicio.listarNotificaciones());
        add(grid);
    }
}
