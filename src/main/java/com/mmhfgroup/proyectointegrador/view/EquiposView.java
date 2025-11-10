package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.service.EquipoService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Equipos")
@Route(value = "equipos", layout = EstudianteLayout.class)
@PermitAll
public class EquiposView extends VerticalLayout {

    private final EquipoService servicio;
    private final Grid<Equipo> grid = new Grid<>(Equipo.class, false);

    // Controles de alta/baja (solo roles Cátedra/Admin)
    private final IntegerField numero = new IntegerField("Número");
    private final TextField nombre = new TextField("Nombre");
    private final TextField auditor = new TextField("Auditor");
    private final Button agregar = new Button("Agregar equipo");
    private final Button eliminar = new Button("Eliminar seleccionado");

    public EquiposView(EquipoService servicio) {
        this.servicio = servicio;
        setPadding(true);
        setSpacing(true);

        add(new H2("Gestión de Equipos"));

        // --- Grid ---
        grid.addColumn(Equipo::getNumero).setHeader("N°").setAutoWidth(true).setSortable(true);
        grid.addColumn(Equipo::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Equipo::getAuditor).setHeader("Auditor").setAutoWidth(true);

        // Columna de acción: ver integrantes
        grid.addComponentColumn(eq -> {
            Button ver = new Button("Integrantes", new Icon(VaadinIcon.USERS));
            ver.addClickListener(e -> abrirDialogoIntegrantes(eq));
            ver.addThemeName("tertiary");
            return ver;
        }).setHeader("Integrantes").setAutoWidth(true);

        grid.setItems(servicio.listarEquipos());
        add(grid);

        // --- Formulario (solo visible si es Cátedra/Admin) ---
        boolean esEditor = tieneRol("ROLE_ADMIN") || tieneRol("ROLE_CATEDRA");

        numero.setMin(1);
        numero.setStepButtonsVisible(true);
        numero.setHelperText("Dejar vacío para usar el siguiente libre");
        nombre.setPlaceholder("Equipo X");
        auditor.setPlaceholder("Nombre del auditor");

        agregar.addClickListener(e -> {
            Integer n = numero.getValue();
            if (n == null) n = servicio.proximoNumero();
            String nom = nombre.getValue();
            if (nom == null || nom.isBlank()) nom = "Equipo " + n;
            servicio.crearEquipo(n, nom, auditor.getValue());
            refrescar();
            Notification.show("Equipo agregado");
            numero.clear(); nombre.clear(); auditor.clear();
        });

        eliminar.addClickListener(e -> {
            Equipo sel = grid.asSingleSelect().getValue();
            if (sel != null) {
                servicio.eliminarEquipo(sel.getId());
                refrescar();
                Notification.show("Equipo eliminado");
            }
        });

        HorizontalLayout formulario = new HorizontalLayout(numero, nombre, auditor, agregar, eliminar);
        formulario.setDefaultVerticalComponentAlignment(Alignment.END);
        formulario.setVisible(esEditor);
        add(formulario);

        // Botón para recargar (opcional)
        Button recargar = new Button("Recargar", ev -> refrescar());
        add(recargar);
    }

    private void refrescar() {
        grid.setItems(servicio.listarEquipos());
    }

    private boolean tieneRol(String rol) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> rol.equals(a.getAuthority()));
    }

    private void abrirDialogoIntegrantes(Equipo equipo) {
        Dialog d = new Dialog();
        d.setHeaderTitle("Integrantes - Equipo " + equipo.getNumero());

        // --- TAMAÑO DEL DIALOG ---
        // Ancho y alto por defecto (responsive); usa viewport units
        d.setWidth("900px");       // o "70vw"
        d.setHeight("70vh");       // o "80vh"
        d.setResizable(true);      // permitir que el usuario lo redimensione
        d.setDraggable(true);      // opcional: se puede arrastrar

        // Contenedor para que el grid ocupe el alto disponible
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(false);

        Grid<Estudiante> g = new Grid<>(Estudiante.class, false);
        g.addColumn(Estudiante::getApellido).setHeader("Apellido").setAutoWidth(true).setSortable(true);
        g.addColumn(Estudiante::getNombre).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        g.addColumn(Estudiante::getEmail).setHeader("Email").setAutoWidth(true);
        g.addColumn(Estudiante::getLegajo).setHeader("Legajo").setAutoWidth(true);

        // El grid ocupa casi todo el alto del diálogo
        g.setSizeFull();           // ancho/alto 100% del contenedor
        g.getStyle().set("min-height", "50vh"); // para que no quede chiquito

        var integrantes = servicio.integrantesDe(equipo.getId());
        g.setItems(integrantes);

        if (integrantes.isEmpty()) {
            g.setVisible(false);
            Span vacio = new Span("Sin integrantes asignados.");
            vacio.getStyle().set("color", "var(--lumo-secondary-text-color)");
            content.add(vacio);
        } else {
            content.add(g);
        }

        d.add(content);

        Button cerrar = new Button("Cerrar", e -> d.close());
        d.getFooter().add(cerrar);

        d.open();
    }

}
