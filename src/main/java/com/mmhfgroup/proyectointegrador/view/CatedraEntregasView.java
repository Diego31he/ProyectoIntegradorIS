package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Seccion;
import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import com.mmhfgroup.proyectointegrador.repository.EntregaRepository;
import com.mmhfgroup.proyectointegrador.repository.SeccionRepository;
import com.mmhfgroup.proyectointegrador.repository.ZonaEntregaRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import java.util.function.Function;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vista Cátedra simplificada:
 * - Panel izquierdo: Sección + Zonas (crear rápido)
 * - Panel derecho: Entregas de la zona seleccionada
 */
@PageTitle("Entregas (Cátedra)")
@Route(value = "catedra/entregas", layout = CatedraLayout.class)
@RolesAllowed({"ROLE_CATEDRA", "ROLE_ADMIN"})
public class CatedraEntregasView extends VerticalLayout {

    private final SeccionRepository seccionRepo;
    private final ZonaEntregaRepository zonaRepo;
    private final EntregaRepository entregaRepo;

    // Selecciones
    private final Select<Seccion> selSeccion = new Select<>();
    private final Select<ZonaEntrega> selZona = new Select<>();

    // Grid plano (DTO) para evitar LAZY en UI
    private final Grid<EntregaRow> grid = new Grid<>(EntregaRow.class, false);

    public CatedraEntregasView(SeccionRepository seccionRepo,
                               ZonaEntregaRepository zonaRepo,
                               EntregaRepository entregaRepo) {
        this.seccionRepo = seccionRepo;
        this.zonaRepo = zonaRepo;
        this.entregaRepo = entregaRepo;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Gestión de Entregas · Cátedra"));

        // Split master-detail (izq: selección/creación; der: entregas)
        SplitLayout split = new SplitLayout(buildLeftPanel(), buildRightPanel());
        split.setSizeFull();
        split.setSplitterPosition(40);
        add(split);

        // Carga inicial
        safeRefreshAll();
    }

    // ===================== Panels =====================

    private Component buildLeftPanel() {
        VerticalLayout left = new VerticalLayout();
        left.setPadding(true);
        left.setSpacing(true);
        left.setSizeFull();

        // Selectores
        HorizontalLayout selectors = buildSelectors();
        selectors.setWidthFull();

        // Creación rápida
        Component quickCreate = buildQuickCreateForms();

        left.add(new H3("Seleccionar contexto"), selectors, quickCreate);
        return left;
    }

    private Component buildRightPanel() {
        VerticalLayout right = new VerticalLayout();
        right.setPadding(true);
        right.setSpacing(true);
        right.setSizeFull();

        // Grid columnas
        grid.addColumn(EntregaRow::archivo).setHeader("Archivo").setAutoWidth(true).setResizable(true);
        grid.addColumn(EntregaRow::fecha).setHeader("Fecha").setAutoWidth(true).setResizable(true);
        grid.addColumn(EntregaRow::autor).setHeader("Autor").setAutoWidth(true).setResizable(true);
        grid.addColumn(EntregaRow::equipo).setHeader("Equipo").setAutoWidth(true).setResizable(true);
        grid.setHeight("550px");

        Button btnRefrescar = new Button("Refrescar entregas", e -> safeRefreshEntregas());
        btnRefrescar.setWidthFull();

        right.add(new H3("Entregas de la zona seleccionada"), grid, btnRefrescar);
        right.setFlexGrow(1, grid);
        return right;
    }

    // ===================== Builders =====================

    private HorizontalLayout buildSelectors() {
        selSeccion.setLabel("Sección");
        selSeccion.setItemLabelGenerator(s -> {
            if (s == null) return "—";
            String t = Optional.ofNullable(s.getTitulo()).orElse("").trim();
            return t.isBlank() ? ("Sección #" + s.getId()) : t;
        });
        selSeccion.addValueChangeListener(e -> {
            try {
                refreshZonas(e.getValue(), /*preserve*/ false);
                grid.setItems(Collections.emptyList());
            } catch (Exception ex) {
                notifyError("No se pudieron cargar las zonas", ex);
            }
        });

        selZona.setLabel("Zona de entrega");
        selZona.setItemLabelGenerator(z -> {
            if (z == null) return "—";
            String titulo = Optional.ofNullable(z.getTitulo()).orElse("").trim();
            String fecha = Optional.ofNullable(z.getFechaCierre())
                    .map(f -> " (vence: " + f.format(DateTimeFormatter.ISO_DATE) + ")")
                    .orElse("");
            return (titulo.isBlank() ? ("Zona #" + z.getId()) : titulo) + fecha;
        });
        selZona.addValueChangeListener(e -> safeRefreshEntregas());

        Button btnRefrescarTodo = new Button("Refrescar todo", click -> safeRefreshAll());

        HorizontalLayout hl = new HorizontalLayout(selSeccion, selZona, btnRefrescarTodo);
        hl.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        hl.setSpacing(true);
        return hl;
    }

    private Component buildQuickCreateForms() {
        HorizontalLayout container = new HorizontalLayout();
        container.setWidthFull();
        container.setSpacing(true);

        // Crear Sección
        FormLayout formSeccion = new FormLayout();
        formSeccion.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        TextField tituloSec = new TextField("Título");
        TextArea descSec = new TextArea("Descripción");
        descSec.setHeight("100px");
        Button crearSeccion = new Button("Crear sección", e -> {
            String t = Optional.ofNullable(tituloSec.getValue()).orElse("").trim();
            if (t.isBlank()) { Notification.show("Ingrese un título"); return; }
            try {
                Seccion s = new Seccion();
                s.setTitulo(t);
                s.setDescripcion(descSec.getValue());
                seccionRepo.save(s);
                tituloSec.clear(); descSec.clear();
                safeRefreshSecciones(/*preserve*/ true);
                Notification.show("Sección creada");
            } catch (Exception ex) {
                notifyError("No se pudo crear la sección", ex);
            }
        });
        formSeccion.add(tituloSec, descSec, crearSeccion);
        VerticalLayout cardSeccion = new VerticalLayout(new H3("Nueva Sección"), formSeccion);
        cardSeccion.setWidthFull();

        // Crear Zona
        FormLayout formZona = new FormLayout();
        formZona.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        TextField tituloZona = new TextField("Título");
        DatePicker fechaCierre = new DatePicker("Fecha límite (opcional)");
        Button crearZona = new Button("Crear zona", e -> {
            Seccion sec = selSeccion.getValue();
            if (sec == null) { Notification.show("Seleccione primero una sección"); return; }
            String tz = Optional.ofNullable(tituloZona.getValue()).orElse("").trim();
            if (tz.isBlank()) { Notification.show("Ingrese un título para la zona"); return; }
            try {
                ZonaEntrega z = new ZonaEntrega();
                z.setTitulo(tz);
                LocalDate f = fechaCierre.getValue();
                if (f != null) z.setFechaCierre(f);
                z.setSeccion(sec);
                zonaRepo.save(z);
                tituloZona.clear(); fechaCierre.clear();
                refreshZonas(sec, /*preserve*/ true);
                Notification.show("Zona creada");
            } catch (Exception ex) {
                notifyError("No se pudo crear la zona", ex);
            }
        });
        formZona.add(tituloZona, fechaCierre, crearZona);
        VerticalLayout cardZona = new VerticalLayout(new H3("Nueva Zona (en la sección seleccionada)"), formZona);
        cardZona.setWidthFull();

        container.add(cardSeccion, cardZona);
        container.setFlexGrow(1, cardSeccion, cardZona);
        return container;
    }

    // ===================== Data loading (seguros) =====================

    private void safeRefreshAll() {
        try {
            safeRefreshSecciones(/*preserve*/ true);
            safeRefreshEntregas(); // si hay zona seleccionada
            Notification.show("Datos actualizados");
        } catch (Exception ex) {
            notifyError("No se pudieron actualizar los datos", ex);
        }
    }

    private void safeRefreshSecciones(boolean preserve) {
        List<Seccion> secciones = seccionRepo.findAll();
        Seccion prev = preserve ? selSeccion.getValue() : null;

        selSeccion.setItems(secciones);

        if (preserve && prev != null && containsById(secciones, prev.getId(), Seccion::getId)) {
            selSeccion.setValue(findById(secciones, prev.getId(), Seccion::getId));
            refreshZonas(selSeccion.getValue(), /*preserve*/ true);
        } else if (!secciones.isEmpty()) {
            selSeccion.setValue(secciones.get(0));
            refreshZonas(selSeccion.getValue(), /*preserve*/ false);
        } else {
            selSeccion.clear();
            selZona.clear();
            selZona.setItems(Collections.emptyList());
            grid.setItems(Collections.emptyList());
        }
    }

    private void refreshZonas(Seccion seccion, boolean preserve) {
        if (seccion == null) {
            selZona.clear();
            selZona.setItems(Collections.emptyList());
            grid.setItems(Collections.emptyList());
            return;
        }
        List<ZonaEntrega> zonas = zonaRepo.findBySeccionId(seccion.getId());
        ZonaEntrega prev = preserve ? selZona.getValue() : null;

        selZona.setItems(zonas);

        if (preserve && prev != null && containsById(zonas, prev.getId(), ZonaEntrega::getId)) {
            selZona.setValue(findById(zonas, prev.getId(), ZonaEntrega::getId));
        } else if (!zonas.isEmpty()) {
            selZona.setValue(zonas.get(0));
        } else {
            selZona.clear();
            grid.setItems(Collections.emptyList());
        }
    }

    private void safeRefreshEntregas() {
        try {
            ZonaEntrega z = selZona.getValue();
            if (z == null) { grid.setItems(Collections.emptyList()); return; }
            grid.setItems(loadEntregaRows(z.getId()));
        } catch (Exception ex) {
            notifyError("No se pudieron cargar las entregas", ex);
        }
    }

    // ===================== Core: evitar LAZY en UI =====================

    /**
     * Carga Entregas y las mapea a un DTO plano con @Transactional (sesión abierta).
     * Así evitamos LazyInitializationException al renderizar el Grid.
     */
    @Transactional(readOnly = true)
    protected List<EntregaRow> loadEntregaRows(Long zonaId) {
        // Si querés, podés cambiar a un método con fetch-join (ver nota al final)
        List<Entrega> entregas = entregaRepo.findByZonaEntregaId(zonaId);

        return entregas.stream().map(e -> {
            String archivo = Optional.ofNullable(e.getNombreArchivo()).orElse("—");
            String fecha = Optional.ofNullable(e.getFechaHora()).map(Object::toString).orElse("—");

            String autor = "—";
            String equipo = "—";
            try {
                if (e.getAutor() != null) {
                    String nombre = Optional.ofNullable(e.getAutor().getNombre()).orElse("").trim();
                    String apellido = Optional.ofNullable(e.getAutor().getApellido()).orElse("").trim();
                    String full = (nombre + " " + apellido).trim();
                    autor = full.isBlank()
                            ? Optional.ofNullable(e.getAutor().getEmail()).orElse("—")
                            : full;

                    if (e.getAutor() instanceof Estudiante est) {
                        Equipo eq = est.getEquipo(); // inicializado dentro de la transacción
                        if (eq != null && eq.getNumero() != null) {
                            equipo = "Eq " + eq.getNumero();
                        }
                    }
                }
            } catch (Exception ignored) {
                // a prueba de pereza extrema :)
            }

            return new EntregaRow(archivo, fecha, autor, equipo);
        }).collect(Collectors.toList());
    }

    // ===================== Helpers =====================

    private void notifyError(String msg, Exception ex) {
        Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        ex.printStackTrace();
    }

    private static <E, T> boolean containsById(List<E> list, T id, Function<E, T> idGetter) {
        if (list == null || id == null) return false;
        for (E e : list) {
            if (Objects.equals(idGetter.apply(e), id)) return true;
        }
        return false;
    }

    private static <E, T> E findById(List<E> list, T id, Function<E, T> idGetter) {
        if (list == null || id == null) return null;
        for (E e : list) {
            if (Objects.equals(idGetter.apply(e), id)) return e;
        }
        return null;
    }

    // DTO plano para el Grid
    public record EntregaRow(String archivo, String fecha, String autor, String equipo) {}

    // Extensiones para tratar Seccion/Zona como HasId
    static {
        // Nada que ejecutar; sólo indicativo de que Seccion y ZonaEntrega ya tienen getId()
    }
}
