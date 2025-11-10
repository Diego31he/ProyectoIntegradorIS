package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Inicio - Cátedra")
@Route(value = "catedra", layout = CatedraLayout.class)
@RolesAllowed({"CATEDRA", "ADMIN"})
public class CatedraHomeView extends VerticalLayout {

    public CatedraHomeView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        add(new H1("Panel de Control de Cátedra"));

        HorizontalLayout row1 = new HorizontalLayout();
        row1.add(
                createNavButton("Gestión de Equipos", VaadinIcon.GROUP, EquiposCatedraView.class),
                createNavButton("Zonas de Entrega", VaadinIcon.TASKS, ZonasEntregaAdminView.class),
                createNavButton("Ver Entregas", VaadinIcon.ARCHIVE, CatedraEntregasView.class),
                createNavButton("Participantes", VaadinIcon.USER, ParticipantesCatedraView.class)

        );

        HorizontalLayout row2 = new HorizontalLayout();
        row2.add(
                // --- INICIO DE CORRECCIÓN ---
                createNavButton("Gestión Auditorías", VaadinIcon.CLIPBOARD_CHECK, AuditoriasView.class), // <-- AÑADIDO
                // --- FIN DE CORRECCIÓN ---
                createNavButton("Calendario Cátedra", VaadinIcon.CALENDAR, CalendarioCatedraView.class),
                createNavButton("Mensajería", VaadinIcon.COMMENTS, MensajeriaCatedraView.class),
                createNavButton("Notificaciones", VaadinIcon.BELL, NotificacionesCatedraView.class)
        );

        add(row1, row2);
    }

    private Button createNavButton(String text, VaadinIcon icon, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        Button button = new Button(text, icon.create());
        button.setHeight("100px");
        button.setWidth("200px");
        button.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        return button;
    }
}