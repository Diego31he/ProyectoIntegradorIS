package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Notificacion;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed; // <-- Seguridad
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

@PageTitle("Notificaciones de Cátedra")
@Route(value = "catedra/notificaciones", layout = CatedraLayout.class) // <-- Layout de Cátedra
@RolesAllowed({"CATEDRA", "ADMIN"}) // <-- Seguridad
public class NotificacionesCatedraView extends VerticalLayout {

    private final NotificacionService notificacionService;
    private final Usuario usuarioActual;
    private final Grid<Notificacion> grid = new Grid<>(Notificacion.class);

    @Autowired
    public NotificacionesCatedraView(NotificacionService notificacionService, SecurityService securityService) {
        this.notificacionService = notificacionService;
        this.usuarioActual = securityService.getAuthenticatedUser();

        setPadding(true);
        setSpacing(true);
        add(new H2("Centro de Notificaciones (Cátedra)"));

        configurarGrid();
        cargarNotificaciones();

        add(grid);

        // Al entrar a la vista, marcamos todas como leídas
        marcarLeidas();
    }

    private void configurarGrid() {
        grid.setColumns(); // Limpiamos columnas automáticas

        grid.addComponentColumn(notif -> {
            Icon icon;
            if (notif.isVista()) {
                icon = VaadinIcon.ENVELOPE_OPEN_O.create();
                icon.setColor("gray");
            } else {
                icon = VaadinIcon.ENVELOPE.create();
                icon.setColor("var(--lumo-primary-color)");
            }
            return icon;
        }).setHeader("").setFlexGrow(0).setWidth("60px");

        grid.addColumn(Notificacion::getMensaje)
                .setHeader("Mensaje")
                .setFlexGrow(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        grid.addColumn(new TextRenderer<>(notif ->
                notif.getFechaHora().format(formatter)
        )).setHeader("Fecha y Hora").setFlexGrow(0).setWidth("180px");
    }

    private void cargarNotificaciones() {
        List<Notificacion> misNotificaciones = notificacionService.listarNotificacionesPorUsuario(usuarioActual);
        grid.setItems(misNotificaciones);
    }

    private void marcarLeidas() {
        List<Notificacion> noVistas = notificacionService.listarNotificacionesPorUsuario(usuarioActual).stream()
                .filter(n -> !n.isVista())
                .toList();

        if (!noVistas.isEmpty()) {
            notificacionService.marcarComoVistas(noVistas);
            grid.setItems(notificacionService.listarNotificacionesPorUsuario(usuarioActual));
        }
    }
}