package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.service.EquipoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Equipos")
@Route(value = "equipos", layout = MainLayout.class)
public class EquiposView extends VerticalLayout {

    private final EquipoService servicio = new EquipoService();
    private final Grid<Equipo> grid = new Grid<>(Equipo.class);
    private final TextField nombre = new TextField("Nombre del equipo");
    private final TextField auditor = new TextField("Auditor asignado");

    public EquiposView() {
        setPadding(true);
        setSpacing(true);
        add(new H2("Gestión de Equipos"));

        grid.setColumns("numero", "nombre", "auditor");
        grid.setItems(servicio.listarEquipos());

        Button agregar = new Button("Agregar equipo", e -> {
            if (!nombre.isEmpty() && !auditor.isEmpty()) {
                servicio.agregarEquipo(nombre.getValue(), auditor.getValue());
                grid.getDataProvider().refreshAll();
                nombre.clear();
                auditor.clear();
            }
        });

        Button eliminar = new Button("Eliminar seleccionado", e -> {
            Equipo seleccionado = grid.asSingleSelect().getValue();
            if (seleccionado != null) {
                servicio.eliminarEquipo(seleccionado);
                grid.getDataProvider().refreshAll();
            }
        });

        HorizontalLayout formulario = new HorizontalLayout(nombre, auditor, agregar, eliminar);
        formulario.setDefaultVerticalComponentAlignment(Alignment.END);

        add(formulario, grid);
    }
}
