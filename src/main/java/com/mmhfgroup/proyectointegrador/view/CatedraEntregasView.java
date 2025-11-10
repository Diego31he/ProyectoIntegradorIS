package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Seccion;
import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import com.mmhfgroup.proyectointegrador.repository.EntregaRepository;
import com.mmhfgroup.proyectointegrador.repository.SeccionRepository;
import com.mmhfgroup.proyectointegrador.repository.ZonaEntregaRepository;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@PageTitle("Entregas (Cátedra)")
@Route(value = "catedra/entregas", layout = CatedraLayout.class)
@RolesAllowed({"ROLE_CATEDRA", "ROLE_ADMIN"})
public class CatedraEntregasView extends VerticalLayout {

    private final SeccionRepository seccionRepo;
    private final ZonaEntregaRepository zonaRepo;
    private final EntregaRepository entregaRepo;

    // Filtros
    private final Select<Seccion> selSeccion = new Select<>();
    private final Select<ZonaEntrega> selZona = new Select<>();

    // Grid (usamos DTO plano para evitar problemas de LAZY al render)
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

        add(new H2("Entregas – Cátedra"));

        HorizontalLayout filtros = buildFilterBar();
        filtros.setWidthFull();
        add(filtros);

        configGrid();
        add(grid);
        expand(grid);

        // Carga inicial
        safeRefreshAll();
    }

    // ===================== UI =====================

    private HorizontalLayout buildFilterBar() {
        // Sección
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

        // Zona
        selZona.setLabel("Zona");
        selZona.setItemLabelGenerator(z -> {
            if (z == null) return "—";
            String titulo = Optional.ofNullable(z.getTitulo()).orElse("").trim();
            String fecha = Optional.ofNullable(z.getFechaCierre())
                    .map(f -> " (vence: " + f.format(DateTimeFormatter.ISO_DATE) + ")")
                    .orElse("");
            return (titulo.isBlank() ? ("Zona #" + z.getId()) : titulo) + fecha;
        });
        selZona.addValueChangeListener(e -> safeRefreshEntregas());

        // Acciones
        Button btnRefrescar = new Button("Refrescar", e -> safeRefreshAll());
        Button btnNuevaSeccion = new Button("Nueva sección", e -> openNuevaSeccionDialog());
        Button btnNuevaZona = new Button("Nueva zona", e -> openNuevaZonaDialog());

        HorizontalLayout hl = new HorizontalLayout(selSeccion, selZona, btnRefrescar, btnNuevaSeccion, btnNuevaZona);
        hl.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        hl.setSpacing(true);
        // Si tu versión de Vaadin lo soporta, habilita wrap para evitar superposición:
        try { hl.setWrap(true); } catch (NoSuchMethodError ignored) {}
        return hl;
    }

    private void configGrid() {
        grid.removeAllColumns();
        grid.addColumn(EntregaRow::archivo).setHeader("Archivo").setAutoWidth(true).setResizable(true);
        grid.addColumn(EntregaRow::fecha).setHeader("Fecha").setAutoWidth(true).setResizable(true);
        grid.addColumn(EntregaRow::autor).setHeader("Autor").setAutoWidth(true).setResizable(true);
        grid.addColumn(EntregaRow::equipo).setHeader("Equipo").setAutoWidth(true).setResizable(true);
        grid.setHeight("70vh");
    }

    private void openNuevaSeccionDialog() {
        Dialog d = new Dialog();
        d.setHeaderTitle("Nueva sección");

        TextField titulo = new TextField("Título");
        TextArea desc = new TextArea("Descripción");
        desc.setHeight("120px");

        Button guardar = new Button("Guardar", ev -> {
            String t = Optional.ofNullable(titulo.getValue()).orElse("").trim();
            if (t.isBlank()) { Notification.show("Ingrese un título"); return; }
            try {
                Seccion s = new Seccion();
                s.setTitulo(t);
                s.setDescripcion(desc.getValue());
                seccionRepo.save(s);
                d.close();
                safeRefreshSecciones(true);
                Notification.show("Sección creada");
            } catch (Exception ex) {
                notifyError("No se pudo crear la sección", ex);
            }
        });
        Button cancelar = new Button("Cancelar", ev -> d.close());

        d.add(new FormLayout(titulo, desc));
        d.getFooter().add(new HorizontalLayout(cancelar, guardar));
        d.open();
    }

    private void openNuevaZonaDialog() {
        if (selSeccion.getValue() == null) {
            Notification.show("Seleccione una sección primero");
            return;
        }
        Dialog d = new Dialog();
        d.setHeaderTitle("Nueva zona");

        TextField titulo = new TextField("Título");
        DatePicker fecha = new DatePicker("Fecha límite (opcional)");

        Button guardar = new Button("Guardar", ev -> {
            String tz = Optional.ofNullable(titulo.getValue()).orElse("").trim();
            if (tz.isBlank()) { Notification.show("Ingrese un título"); return; }
            try {
                ZonaEntrega z = new ZonaEntrega();
                z.setTitulo(tz);
                LocalDate f = fecha.getValue();
                if (f != null) z.setFechaCierre(f);
                z.setSeccion(selSeccion.getValue());
                zonaRepo.save(z);
                d.close();
                refreshZonas(selSeccion.getValue(), true);
                Notification.show("Zona creada");
            } catch (Exception ex) {
                notifyError("No se pudo crear la zona", ex);
            }
        });
        Button cancelar = new Button("Cancelar", ev -> d.close());

        d.add(new FormLayout(titulo, fecha));
        d.getFooter().add(new HorizontalLayout(cancelar, guardar));
        d.open();
    }

    // ===================== Data loading =====================

    private void safeRefreshAll() {
        try {
            safeRefreshSecciones(/*preserve*/ true);
            safeRefreshEntregas();
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

    /**
     * Carga Entregas y las mapea a DTO plano dentro de una transacción de solo lectura.
     * Evita LazyInitializationException en el Grid y permite formatear campos null-safe.
     */
    protected List<EntregaRow> loadEntregaRows(Long zonaId) {
        List<Entrega> entregas = entregaRepo.findByZonaEntregaIdWithAutorEquipo(zonaId);
        return mapRows(entregas);
    }

    private List<EntregaRow> mapRows(List<Entrega> entregas) {
        return entregas.stream()
                .map(e -> new EntregaRow(
                        Optional.ofNullable(e.getNombreArchivo()).orElse("—"),
                        fmtFecha(e.getFechaHora()),
                        nombreAutor(e),
                        equipoAutor(e)
                ))
                .collect(Collectors.toList());
    }

    // ===================== Helpers =====================

    private static String fmtFecha(Object dt) {
        return (dt == null) ? "—" : dt.toString();
    }

    private static String nombreAutor(Entrega e) {
        if (e.getAutor() == null) return "—";
        String nombre = Optional.ofNullable(e.getAutor().getNombre()).orElse("").trim();
        String apellido = Optional.ofNullable(e.getAutor().getApellido()).orElse("").trim();
        String full = (nombre + " " + apellido).trim();
        return full.isBlank()
                ? Optional.ofNullable(e.getAutor().getEmail()).orElse("—")
                : full;
    }

    private static String equipoAutor(Entrega e) {
        try {
            if (e.getAutor() instanceof Estudiante est) {
                Equipo eq = est.getEquipo();
                if (eq != null) {
                    if (eq.getNumero() != null) return "Eq " + eq.getNumero();
                    String nombreEq = Optional.ofNullable(eq.getNombre()).orElse("").trim();
                    if (!nombreEq.isBlank()) return nombreEq;
                }
            }
        } catch (Exception ignored) {}
        return "—";
    }

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
}
