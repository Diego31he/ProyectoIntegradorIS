package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.service.EquipoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Gestión de Equipos")
@Route(value = "catedra/equipos", layout = CatedraLayout.class)
@RolesAllowed({"CATEDRA", "ADMIN"})
public class EquiposCatedraView extends VerticalLayout {

    // ... (El constructor y los botones se mantienen igual) ...

    private final EquipoService equipoService;
    private final Grid<Equipo> grid = new Grid<>(Equipo.class);
    private Equipo equipoSeleccionado;

    @Autowired
    public EquiposCatedraView(EquipoService equipoService) {
        this.equipoService = equipoService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        add(new H2("Gestión de Equipos de la Cursada"));
        Button btnCrear = new Button("Crear Equipo", VaadinIcon.PLUS.create(), e -> crearEquipo());
        Button btnEditar = new Button("Editar Equipo", VaadinIcon.PENCIL.create(), e -> editarEquipo(equipoSeleccionado));
        btnEditar.setEnabled(false);
        Button btnEliminar = new Button("Eliminar Equipo", VaadinIcon.TRASH.create(), e -> confirmarEliminacion(equipoSeleccionado));
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnEliminar.setEnabled(false);
        Button btnVer = new Button("Ver/Refrescar Equipos", VaadinIcon.REFRESH.create(), e -> actualizarGrid());
        HorizontalLayout toolbar = new HorizontalLayout(btnCrear, btnEditar, btnEliminar, btnVer);
        add(toolbar);
        configurarGrid(btnEditar, btnEliminar);
        actualizarGrid();
        add(grid);
    }

    // --- INICIO DE CORRECCIÓN (Error 1) ---
    // Función helper para obtener el nombre completo de forma segura
    private String getNombreCompletoEstudiante(Estudiante est) {
        String nombre = (est.getNombre() == null) ? "" : est.getNombre();
        String apellido = (est.getApellido() == null) ? "" : est.getApellido();
        return (nombre + " " + apellido).trim();
    }
    // --- FIN DE CORRECCIÓN ---

    private void configurarGrid(Button btnEditar, Button btnEliminar) {
        grid.removeAllColumns();
        grid.setSizeFull();

        grid.addColumn(Equipo::getNumero).setHeader("Nro").setFlexGrow(0).setWidth("80px");
        grid.addColumn(Equipo::getNombre).setHeader("Nombre del Equipo").setFlexGrow(1);
        grid.addColumn(Equipo::getAuditor).setHeader("Auditor Asignado").setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(equipo -> {
            Set<Estudiante> integrantes = equipo.getIntegrantes();
            if (integrantes == null || integrantes.isEmpty()) {
                return new Span("Sin integrantes");
            }
            // --- INICIO DE CORRECCIÓN (Error 2) ---
            // Usamos la función helper segura
            String integrantesStr = integrantes.stream()
                    .map(this::getNombreCompletoEstudiante)
                    .collect(Collectors.joining(", "));
            // --- FIN DE CORRECCIÓN ---
            return new Span(integrantesStr);
        })).setHeader("Integrantes").setFlexGrow(2);

        grid.asSingleSelect().addValueChangeListener(event -> {
            equipoSeleccionado = event.getValue();
            boolean seleccionado = (equipoSeleccionado != null);
            btnEditar.setEnabled(seleccionado);
            btnEliminar.setEnabled(seleccionado);
        });
    }

    // ... (El resto de la clase principal se mantiene igual) ...
    private void actualizarGrid() {
        grid.setItems(equipoService.findAll());
        grid.asSingleSelect().clear();
        equipoSeleccionado = null;
    }

    private void crearEquipo() {
        try {
            Equipo nuevoEquipo = equipoService.crearEquipoSiguiente();
            Notification.show("Equipo " + nuevoEquipo.getNumero() + " creado.", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            actualizarGrid();
            editarEquipo(nuevoEquipo);
        } catch (Exception e) {
            Notification.show("Error al crear equipo: " + e.getMessage(), 4000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editarEquipo(Equipo equipo) {
        if (equipo == null) {
            return;
        }
        // Pasamos la lógica para obtener nombres al diálogo
        EquipoEditDialog dialog = new EquipoEditDialog(equipo, equipoService, this::getNombreCompletoEstudiante);
        dialog.open();

        dialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                actualizarGrid();
            }
        });
    }

    private void confirmarEliminacion(Equipo equipo) {
        if (equipo == null) return;
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar eliminación");
        dialog.setText("¿Estás seguro de que quieres eliminar el '" + equipo.getNombre() +
                "'? Los estudiantes asignados quedarán sin equipo.");
        dialog.setConfirmText("Eliminar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> eliminarEquipo(equipo));
        dialog.setCancelText("Cancelar");
        dialog.open();
    }

    private void eliminarEquipo(Equipo equipo) {
        try {
            equipoService.deleteEquipo(equipo.getId());
            Notification.show("Equipo eliminado.", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            actualizarGrid();
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 4000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}


// --- CLASE INTERNA DEL DIÁLOGO DE EDICIÓN (CORREGIDA) ---

class EquipoEditDialog extends Dialog {

    private final EquipoService equipoService;
    private Equipo equipo;
    private final Binder<Equipo> binder = new Binder<>(Equipo.class);

    private final TextField nombre = new TextField("Nombre del Equipo");
    private final TextField auditor = new TextField("Auditor");
    private final MultiSelectComboBox<Estudiante> integrantes = new MultiSelectComboBox<>("Integrantes Actuales (desmarca para eliminar)");
    private final MultiSelectComboBox<Estudiante> estudiantesSinEquipo = new MultiSelectComboBox<>("Estudiantes sin equipo (marca para agregar)");

    // Interfaz funcional para pasar la lógica de 'getNombreCompleto'
    @FunctionalInterface
    interface EstudianteLabelGenerator {
        String apply(Estudiante estudiante);
    }

    public EquipoEditDialog(Equipo equipo, EquipoService equipoService, EstudianteLabelGenerator labelGenerator) {
        this.equipo = equipo;
        this.equipoService = equipoService;

        setHeaderTitle("Editando Equipo " + equipo.getNumero());
        setWidth("600px");

        VerticalLayout layout = new VerticalLayout();
        binder.setBean(equipo);

        binder.bind(nombre, Equipo::getNombre, Equipo::setNombre);
        nombre.setWidth("100%");

        binder.bind(auditor, Equipo::getAuditor, Equipo::setAuditor);
        auditor.setWidth("100%");

        // --- INICIO DE CORRECCIÓN (Error 3) ---
        // Pasamos el 'labelGenerator' a la configuración de los ComboBox
        configurarSeleccionIntegrantes(labelGenerator);
        // --- FIN DE CORRECCIÓN ---

        layout.add(nombre, auditor, integrantes, new H3("Agregar Estudiantes"), estudiantesSinEquipo);

        Button btnGuardar = new Button("Guardar", e -> guardarCambios());
        Button btnCancelar = new Button("Cancelar", e -> close());
        getFooter().add(btnCancelar, btnGuardar);

        add(layout);
    }

    private void configurarSeleccionIntegrantes(EstudianteLabelGenerator labelGenerator) {
        Set<Estudiante> integrantesActuales = equipo.getIntegrantes() != null ?
                new HashSet<>(equipo.getIntegrantes()) :
                Collections.emptySet();

        integrantes.setItems(integrantesActuales);
        integrantes.setValue(integrantesActuales);
        // Usamos el 'labelGenerator' seguro
        integrantes.setItemLabelGenerator(labelGenerator::apply);
        integrantes.setWidth("100%");

        List<Estudiante> sinEquipo = equipoService.findEstudiantesSinEquipo();
        estudiantesSinEquipo.setItems(sinEquipo);
        // Usamos el 'labelGenerator' seguro
        estudiantesSinEquipo.setItemLabelGenerator(labelGenerator::apply);
        estudiantesSinEquipo.setWidth("100%");
    }

    private void guardarCambios() {
        try {
            // 1. Guardar Nombre y Auditor
            binder.writeBean(equipo);
            equipoService.guardar(equipo); // <-- Esto funciona

            // 2. Actualizar integrantes
            Set<Estudiante> integrantesDeseados = integrantes.getValue();
            Set<Estudiante> integrantesParaAgregar = estudiantesSinEquipo.getValue();
            integrantesDeseados.addAll(integrantesParaAgregar);

            Set<Estudiante> integrantesOriginales = equipo.getIntegrantes() != null ?
                    new HashSet<>(equipo.getIntegrantes()) :
                    Collections.emptySet();

            // (A) Desasignar
            for (Estudiante estOriginal : integrantesOriginales) {
                if (!integrantesDeseados.contains(estOriginal)) {
                    equipoService.quitarEquipoDeEstudiante(estOriginal.getId());
                }
            }

            // (B) Asignar
            for (Estudiante estNuevo : integrantesDeseados) {
                if (estNuevo.getEquipo() == null || !estNuevo.getEquipo().getId().equals(equipo.getId())) {
                    equipoService.asignarEstudianteAEquipo(estNuevo.getId(), equipo.getId());
                }
            }

            // Si todo sale bien, esta es la notificación que verás:
            Notification.show("Equipo guardado", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            close();

        } catch (Exception e) {
            // Si algo falla (incluso el NPE que corregimos), verás esto
            Notification.show("Error al guardar: " + e.getMessage(), 4000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }
}