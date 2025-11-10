package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Catedra; // <-- AÑADIDO
import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.CatedraRepository; // <-- AÑADIDO
import com.mmhfgroup.proyectointegrador.service.EquipoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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

    private final EquipoService equipoService;
    // --- INICIO DE CORRECCIÓN ---
    // Cambiamos UsuarioRepository por CatedraRepository
    private final CatedraRepository catedraRepository;
    // --- FIN DE CORRECCIÓN ---
    private final Grid<Equipo> grid = new Grid<>(Equipo.class);
    private Equipo equipoSeleccionado;

    @Autowired
    public EquiposCatedraView(EquipoService equipoService, CatedraRepository catedraRepository) { // <-- Corregido
        this.equipoService = equipoService;
        this.catedraRepository = catedraRepository; // <-- Corregido

        // ... (El resto del constructor se mantiene igual) ...
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

    private String getNombreCompletoUsuario(Usuario u) {
        // ... (Se mantiene igual) ...
        if (u == null) return "";
        String nombre = (u.getNombre() == null) ? "" : u.getNombre();
        String apellido = (u.getApellido() == null) ? "" : u.getApellido();
        String nombreCompleto = (nombre + " " + apellido).trim();
        return nombreCompleto.isEmpty() ? u.getEmail() : nombreCompleto;
    }

    private void configurarGrid(Button btnEditar, Button btnEliminar) {
        // ... (Se mantiene igual) ...
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
            String integrantesStr = integrantes.stream()
                    .map(this::getNombreCompletoUsuario)
                    .collect(Collectors.joining(", "));
            return new Span(integrantesStr);
        })).setHeader("Integrantes").setFlexGrow(2);
        grid.asSingleSelect().addValueChangeListener(event -> {
            equipoSeleccionado = event.getValue();
            boolean seleccionado = (equipoSeleccionado != null);
            btnEditar.setEnabled(seleccionado);
            btnEliminar.setEnabled(seleccionado);
        });
    }

    private void actualizarGrid() {
        // ... (Se mantiene igual) ...
        grid.setItems(equipoService.findAll());
        grid.asSingleSelect().clear();
        equipoSeleccionado = null;
    }

    private void crearEquipo() {
        // ... (Se mantiene igual) ...
        try {
            Equipo nuevoEquipo = equipoService.crearEquipoSiguiente();
            Notification.show("Equipo " + nuevoEquipo.getNumero() + " creado.", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            actualizarGrid();
            editarEquipo(nuevoEquipo);
        } catch (Exception e) {
            Notification.show("Error al crear equipo: " + e.getMessage(), 4000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace(); // Imprime el error real en la consola
        }
    }

    private void editarEquipo(Equipo equipo) {
        if (equipo == null) {
            return;
        }
        // --- INICIO DE CORRECCIÓN ---
        // Pasamos el repositorio de CATEDRA
        EquipoEditDialog dialog = new EquipoEditDialog(equipo, equipoService, catedraRepository, this::getNombreCompletoUsuario);
        // --- FIN DE CORRECCIÓN ---

        dialog.open();

        dialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                actualizarGrid();
            }
        });
    }

    private void confirmarEliminacion(Equipo equipo) {
        // ... (Se mantiene igual) ...
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
        // ... (Se mantiene igual) ...
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
    // --- INICIO DE CORRECCIÓN ---
    private final CatedraRepository catedraRepository;
    // --- FIN DE CORRECCIÓN ---
    private Equipo equipo;
    private final Binder<Equipo> binder = new Binder<>(Equipo.class);

    private final TextField nombre = new TextField("Nombre del Equipo");

    // --- INICIO DE CORRECCIÓN ---
    // El ComboBox ahora es de tipo Catedra, no Usuario
    private final ComboBox<Catedra> auditor = new ComboBox<>("Auditor (Docente)");
    // --- FIN DE CORRECCIÓN ---

    private final MultiSelectComboBox<Estudiante> integrantes = new MultiSelectComboBox<>("Integrantes Actuales (desmarca para eliminar)");
    private final MultiSelectComboBox<Estudiante> estudiantesSinEquipo = new MultiSelectComboBox<>("Estudiantes sin equipo (marca para agregar)");

    @FunctionalInterface
    interface UsuarioLabelGenerator {
        String apply(Usuario usuario);
    }

    public EquipoEditDialog(Equipo equipo,
                            EquipoService equipoService,
                            CatedraRepository catedraRepository, // <-- Corregido
                            UsuarioLabelGenerator labelGenerator) {
        this.equipo = equipo;
        this.equipoService = equipoService;
        this.catedraRepository = catedraRepository; // <-- Corregido

        setHeaderTitle("Editando Equipo " + equipo.getNumero());
        setWidth("600px");

        VerticalLayout layout = new VerticalLayout();
        binder.setBean(equipo);

        binder.bind(nombre, Equipo::getNombre, Equipo::setNombre);
        nombre.setWidth("100%");

        // --- INICIO DE CORRECCIÓN ---

        auditor.setWidth("100%");
        // Usamos el CatedraRepository para buscar TODOS los Catedra
        List<Catedra> docentes = catedraRepository.findAll();
        auditor.setItems(docentes);
        auditor.setItemLabelGenerator(labelGenerator::apply);

        // Pre-seleccionar el auditor actual
        String nombreAuditorActual = equipo.getAuditor();
        if (nombreAuditorActual != null && !nombreAuditorActual.isBlank()) {
            for (Catedra docente : docentes) {
                if (nombreAuditorActual.equals(labelGenerator.apply(docente))) {
                    auditor.setValue(docente);
                    break;
                }
            }
        }

        // --- FIN DE CORRECCIÓN ---

        configurarSeleccionIntegrantes(labelGenerator);

        layout.add(nombre, auditor, integrantes, new H3("Agregar Estudiantes"), estudiantesSinEquipo);

        Button btnGuardar = new Button("Guardar", e -> guardarCambios());
        Button btnCancelar = new Button("Cancelar", e -> close());
        getFooter().add(btnCancelar, btnGuardar);

        add(layout);
    }

    private void configurarSeleccionIntegrantes(UsuarioLabelGenerator labelGenerator) {
        // ... (Se mantiene igual) ...
        Set<Estudiante> integrantesActuales = equipo.getIntegrantes() != null ?
                new HashSet<>(equipo.getIntegrantes()) :
                Collections.emptySet();
        integrantes.setItems(integrantesActuales);
        integrantes.setValue(integrantesActuales);
        integrantes.setItemLabelGenerator(labelGenerator::apply);
        integrantes.setWidth("100%");
        List<Estudiante> sinEquipo = equipoService.findEstudiantesSinEquipo();
        estudiantesSinEquipo.setItems(sinEquipo);
        estudiantesSinEquipo.setItemLabelGenerator(labelGenerator::apply);
        estudiantesSinEquipo.setWidth("100%");
    }

    private void guardarCambios() {
        try {
            binder.writeBean(equipo);

            // --- INICIO DE CORRECCIÓN ---
            // Guardar Auditor (manualmente)
            Catedra docenteSeleccionado = auditor.getValue(); // <-- Es de tipo Catedra
            if (docenteSeleccionado != null) {
                equipo.setAuditor(getNombreCompletoUsuario(docenteSeleccionado));
            } else {
                equipo.setAuditor(null);
            }
            // --- FIN DE CORRECCIÓN ---

            equipoService.guardar(equipo);

            // ... (Lógica de integrantes se mantiene igual) ...
            Set<Estudiante> integrantesDeseados = integrantes.getValue();
            Set<Estudiante> integrantesParaAgregar = estudiantesSinEquipo.getValue();
            integrantesDeseados.addAll(integrantesParaAgregar);
            Set<Estudiante> integrantesOriginales = equipo.getIntegrantes() != null ?
                    new HashSet<>(equipo.getIntegrantes()) :
                    Collections.emptySet();
            for (Estudiante estOriginal : integrantesOriginales) {
                if (!integrantesDeseados.contains(estOriginal)) {
                    equipoService.quitarEquipoDeEstudiante(estOriginal.getId());
                }
            }
            for (Estudiante estNuevo : integrantesDeseados) {
                if (estNuevo.getEquipo() == null || !estNuevo.getEquipo().getId().equals(equipo.getId())) {
                    equipoService.asignarEstudianteAEquipo(estNuevo.getId(), equipo.getId());
                }
            }

            Notification.show("Equipo guardado", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            close();

        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 4000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private String getNombreCompletoUsuario(Usuario u) {
        // ... (Se mantiene igual) ...
        if (u == null) return "";
        String nombre = (u.getNombre() == null) ? "" : u.getNombre();
        String apellido = (u.getApellido() == null) ? "" : u.getApellido();
        String nombreCompleto = (nombre + " " + apellido).trim();
        return nombreCompleto.isEmpty() ? u.getEmail() : nombreCompleto;
    }
}