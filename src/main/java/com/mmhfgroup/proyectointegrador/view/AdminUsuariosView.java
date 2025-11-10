package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.CatedraRepository;
import com.mmhfgroup.proyectointegrador.repository.EquipoRepository;
import com.mmhfgroup.proyectointegrador.repository.EstudianteRepository;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Route(value = "admin/usuarios", layout = AdminLayout.class)
@PageTitle("Usuarios")
@RolesAllowed("ROLE_ADMIN")
public class AdminUsuariosView extends VerticalLayout {

    private final UsuarioRepository usuarioRepo;
    private final EstudianteRepository estudianteRepo;
    private final CatedraRepository catedraRepo;
    private final EquipoRepository equipoRepo;

    private final Grid<Usuario> grid = new Grid<>(Usuario.class, false);

    public AdminUsuariosView(UsuarioRepository usuarioRepo,
                             EstudianteRepository estudianteRepo,
                             CatedraRepository catedraRepo,
                             EquipoRepository equipoRepo) {
        this.usuarioRepo = usuarioRepo;
        this.estudianteRepo = estudianteRepo;
        this.catedraRepo = catedraRepo;
        this.equipoRepo = equipoRepo;

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        var title = new H2("Administración de Usuarios");
        var recargar = new Button("Recargar", new Icon(VaadinIcon.REFRESH), e -> recargarDatos());
        add(new HorizontalLayout(title, recargar));

        configurarGrid();
        recargarDatos();

        getUI().ifPresent(ui ->
                ui.getSession().setErrorHandler(event -> {
                    event.getThrowable().printStackTrace();
                    Notification n = Notification.show("Ocurrió un error. Revisá la consola.");
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                })
        );
    }

    private enum RolOption { ESTUDIANTE, CATEDRA, ADMIN }

    private void configurarGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addColumn(Usuario::getEmail)
                .setHeader("Email").setAutoWidth(true).setResizable(true);

        grid.addColumn(u -> ((u.getNombre() == null ? "" : u.getNombre()) + " " + (u.getApellido() == null ? "" : u.getApellido())).trim())
                .setHeader("Nombre").setAutoWidth(true).setResizable(true);

        // Rol (select)
        grid.addColumn(new ComponentRenderer<>(this::roleSelect))
                .setHeader("Rol").setAutoWidth(true);

        // Equipo (texto)
        grid.addColumn(this::equipoTexto)
                .setHeader("Equipo").setAutoWidth(true);

        // Acciones de equipo (siempre para Estudiante)
        grid.addColumn(new ComponentRenderer<>(this::accionesEquipoSiempreEstudiante))
                .setHeader("Acciones de equipo").setAutoWidth(true);

        add(grid);
    }

    private Component roleSelect(Usuario u) {
        var sel = new Select<RolOption>();
        sel.setItems(RolOption.values());
        sel.setWidth("180px");
        sel.setValue(detectarRol(u));
        sel.addValueChangeListener(ev -> {
            if (!ev.isFromClient()) return;
            try {
                cambiarRol(u.getId(), ev.getValue());
                Notification n = Notification.show("Rol actualizado a " + ev.getValue());
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                recargarDatos();
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification n = Notification.show("No se pudo cambiar el rol: " + ex.getMessage());
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                usuarioRepo.findById(u.getId()).ifPresent(actual -> sel.setValue(detectarRol(actual)));
            }
        });
        return sel;
    }

    private RolOption detectarRol(Usuario u) {
        if (u instanceof Catedra c) return c.isAdmin() ? RolOption.ADMIN : RolOption.CATEDRA;
        return RolOption.ESTUDIANTE;
    }

    private void recargarDatos() {
        grid.setItems(usuarioRepo.findAll());
    }

    private String equipoTexto(Usuario u) {
        if (!(u instanceof Estudiante)) return "—";
        // Intentamos refrescar desde repo para evitar problemas de LAZY en render
        Estudiante e = estudianteRepo.findById(u.getId()).orElse(null);
        if (e == null || e.getEquipo() == null) return "Sin equipo asignado";
        Integer num = e.getEquipo().getNumero();
        return num != null ? "Equipo " + num : "Equipo asignado";
    }

    /** SIEMPRE renderiza acciones cuando la fila es Estudiante (con o sin equipo). */
    private Component accionesEquipoSiempreEstudiante(Usuario u) {
        if (!(u instanceof Estudiante)) return new Span("—");

        // Refrescamos entidad completa para leer equipo sin problemas de LAZY
        Estudiante e = estudianteRepo.findById(u.getId()).orElse(null);
        if (e == null) return new Span("—");

        var selectEquipo = new Select<Equipo>();
        selectEquipo.setItemLabelGenerator(eq -> "Equipo " + eq.getNumero() + (eq.getNombre() != null ? (" - " + eq.getNombre()) : ""));
        selectEquipo.setPlaceholder("Elegir equipo…");
        selectEquipo.setItems(equipoRepo.findAllByOrderByNumeroAsc());

        // Preseleccionar equipo actual si tiene
        if (e.getEquipo() != null && e.getEquipo().getId() != null) {
            equipoRepo.findById(e.getEquipo().getId()).ifPresent(selectEquipo::setValue);
        }

        var btnAsignar = new Button("Asignar/Actualizar", new Icon(VaadinIcon.CHECK));
        btnAsignar.getElement().setProperty("title", "Asigna el equipo elegido al estudiante");
        btnAsignar.addClickListener(click -> {
            Equipo elegido = selectEquipo.getValue();
            if (elegido == null) {
                Notification.show("Seleccioná un equipo", 3000, Notification.Position.MIDDLE);
                return;
            }
            try {
                asignarEstudianteAEquipo(e.getId(), elegido.getId());
                Notification n = Notification.show("Asignado a Equipo " + elegido.getNumero());
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                recargarDatos();
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification n = Notification.show("No se pudo asignar: " + ex.getMessage());
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        var btnCrear = new Button("Crear y asignar", new Icon(VaadinIcon.PLUS_SQUARE_O));
        btnCrear.getElement().setProperty("title", "Crea un nuevo equipo (número max+1) y lo asigna");
        btnCrear.addClickListener(click -> {
            try {
                Equipo nuevo = crearEquipoSiguiente();
                asignarEstudianteAEquipo(e.getId(), nuevo.getId());
                Notification n = Notification.show("Creado Equipo " + nuevo.getNumero() + " y asignado");
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                recargarDatos();
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification n = Notification.show("No se pudo crear/asignar: " + ex.getMessage());
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        var btnQuitar = new Button("Quitar", new Icon(VaadinIcon.CLOSE_CIRCLE_O));
        btnQuitar.getElement().setProperty("title", "Deja al estudiante sin equipo");
        btnQuitar.addClickListener(click -> {
            try {
                quitarEquipo(e.getId());
                Notification n = Notification.show("Equipo quitado");
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                recargarDatos();
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification n = Notification.show("No se pudo quitar: " + ex.getMessage());
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        HorizontalLayout hl = new HorizontalLayout(selectEquipo, btnAsignar, btnCrear, btnQuitar);
        hl.setAlignItems(Alignment.BASELINE);
        return hl;
    }

    // ======== Cambio de rol ========

    @Transactional
    protected void cambiarRol(Long usuarioId, RolOption nuevoRol) {
        Usuario base = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado id=" + usuarioId));

        boolean esCatedra = base instanceof Catedra;
        boolean esEst = base instanceof Estudiante;

        switch (nuevoRol) {
            case ADMIN -> {
                if (esCatedra) {
                    Catedra c = catedraRepo.findById(usuarioId)
                            .orElseThrow(() -> new IllegalStateException("Fila cátedra no encontrada id=" + usuarioId));
                    c.setAdmin(true);
                    catedraRepo.save(c);
                } else {
                    if (esEst) estudianteRepo.deleteById(usuarioId);
                    Catedra c = new Catedra();
                    copiarBase(base, c);
                    c.setId(usuarioId);
                    c.setCargo("Profesor");
                    c.setAdmin(true);
                    catedraRepo.save(c);
                }
            }
            case CATEDRA -> {
                if (esCatedra) {
                    Catedra c = catedraRepo.findById(usuarioId)
                            .orElseThrow(() -> new IllegalStateException("Fila cátedra no encontrada id=" + usuarioId));
                    c.setAdmin(false);
                    catedraRepo.save(c);
                } else {
                    if (esEst) estudianteRepo.deleteById(usuarioId);
                    Catedra c = new Catedra();
                    copiarBase(base, c);
                    c.setId(usuarioId);
                    c.setCargo("Profesor");
                    c.setAdmin(false);
                    catedraRepo.save(c);
                }
            }
            case ESTUDIANTE -> {
                if (esEst) {
                    Estudiante e = estudianteRepo.findById(usuarioId)
                            .orElseThrow(() -> new IllegalStateException("Fila estudiante no encontrada id=" + usuarioId));
                    estudianteRepo.save(e);
                } else {
                    if (esCatedra) catedraRepo.deleteById(usuarioId);
                    Estudiante e = new Estudiante();
                    copiarBase(base, e);
                    e.setId(usuarioId);
                    if (e.getLegajo() == null || e.getLegajo().isBlank()) {
                        e.setLegajo("LEG-" + usuarioId);
                    }
                    estudianteRepo.save(e);
                }
            }
        }
    }

    private void copiarBase(Usuario src, Usuario dst) {
        dst.setNombre(src.getNombre());
        dst.setApellido(src.getApellido());
        dst.setEmail(src.getEmail());
        dst.setPassword(src.getPassword());
    }

    // ======== Equipo: asignar / crear / quitar ========

    @Transactional
    protected void asignarEstudianteAEquipo(Long estudianteId, Long equipoId) {
        Estudiante e = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado id=" + estudianteId));
        Equipo eq = equipoRepo.findById(equipoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado id=" + equipoId));
        e.setEquipo(eq);
        estudianteRepo.save(e);
    }

    @Transactional
    protected Equipo crearEquipoSiguiente() {
        Integer next = equipoRepo.findTopByOrderByNumeroDesc()
                .map(Equipo::getNumero)
                .map(n -> n + 1)
                .orElse(1);
        Equipo nuevo = new Equipo();
        nuevo.setNumero(next);
        nuevo.setNombre("Equipo " + next);
        return equipoRepo.save(nuevo);
    }

    @Transactional
    protected void quitarEquipo(Long estudianteId) {
        Estudiante e = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado id=" + estudianteId));
        e.setEquipo(null);
        estudianteRepo.save(e);
    }
}
