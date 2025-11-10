package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * Vista legacy. Ya no se usa la ruta /entregas.
 * La dejamos solo para no romper enlaces viejos y redirigir a /mi-equipo.
 */
@PageTitle("Entregas")
@Route(value = "entregas", layout = EstudianteLayout.class)
@PermitAll
public class EntregasView extends VerticalLayout {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        getUI().ifPresent(ui -> ui.navigate("mi-equipo"));
    }
}
