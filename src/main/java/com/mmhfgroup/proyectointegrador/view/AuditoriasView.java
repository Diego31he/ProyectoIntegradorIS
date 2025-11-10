package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.EventoCalendario;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.EquipoRepository;
import com.mmhfgroup.proyectointegrador.repository.EventoCalendarioRepository;
import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.service.CalendarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span; // <-- AÑADIDO
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Gestión de Auditorías")
@Route(value = "catedra/auditorias", layout = CatedraLayout.class)
@RolesAllowed({"CATEDRA", "ADMIN"})
public class AuditoriasView extends VerticalLayout {

    private final CalendarioService calendarioService;
    private final EquipoRepository equipoRepository;
    private final EventoCalendarioRepository eventoRepo;
    private final SecurityService securityService;

    private final Usuario auditorLogueado; // <-- AÑADIDO

    private final Grid<EventoCalendario> grid = new Grid<>(EventoCalendario.class);
    private final ComboBox<Equipo> selectEquipo = new ComboBox<>("Equipo");
    private final DatePicker fecha = new DatePicker("Fecha");
    private final TextField titulo = new TextField("Título de la Auditoría");
    private final Button btnCrear = new Button("Programar Auditoría", VaadinIcon.PLUS.create());

    @Autowired
    public AuditoriasView(CalendarioService calendarioService,
                          EquipoRepository equipoRepository,
                          EventoCalendarioRepository eventoRepo,
                          SecurityService securityService) {
        this.calendarioService = calendarioService;
        this.equipoRepository = equipoRepository;
        this.eventoRepo = eventoRepo;
        this.securityService = securityService;
        this.auditorLogueado = securityService.getAuthenticatedUser(); // <-- Obtenemos el usuario

        setSizeFull();
        setPadding(true);

        add(new H2("Gestión de Auditorías"));

        configurarFormulario();
        configurarGrid();

        // Creamos un layout para el formulario
        HorizontalLayout formulario = new HorizontalLayout(selectEquipo, fecha, titulo, btnCrear);
        formulario.setDefaultVerticalComponentAlignment(Alignment.BASELINE); // Alinea los campos

        add(formulario);
        add(new H3("Auditorías Programadas"), grid);

        refrescarGrid();
    }

    private void configurarFormulario() {
        // --- INICIO DE CORRECCIÓN ---

        // Obtenemos el nombre completo del auditor logueado
        String nombreAuditor = auditorLogueado.getNombreCompleto();

        // Buscamos SÓLO los equipos de este auditor
        List<Equipo> misEquipos = equipoRepository.findByAuditor(nombreAuditor);

        // Si es Admin, puede ver todos (opcional)
        if (securityService.isUserAdmin()) {
            misEquipos = equipoRepository.findAll();
        }

        // Ponemos los equipos filtrados en el ComboBox
        selectEquipo.setItems(misEquipos);

        // --- FIN DE CORRECCIÓN ---

        selectEquipo.setItemLabelGenerator(eq -> "Equipo " + eq.getNumero() + " (" + eq.getNombre() + ")");
        selectEquipo.setWidth("350px");

        titulo.setWidth("300px");
        btnCrear.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);

        btnCrear.addClickListener(e -> crearAuditoria());
    }

    private void configurarGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(EventoCalendario::getTitulo).setHeader("Título").setFlexGrow(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        grid.addColumn(new TextRenderer<>(evento ->
                evento.getFecha().format(formatter)
        )).setHeader("Fecha").setWidth("120px").setFlexGrow(0);

        grid.addColumn(evento -> {
            Equipo eq = evento.getEquipo();
            return (eq != null) ? "Equipo " + eq.getNumero() : "N/A";
        }).setHeader("Equipo").setWidth("150px").setFlexGrow(0);

        grid.addColumn(evento -> {
            Usuario creador = evento.getCreador();
            return (creador != null) ? creador.getNombreCompleto() : "N/A";
        }).setHeader("Auditor (Creador)").setFlexGrow(1);

        grid.addColumn(new TextRenderer<>(evento -> {
            // --- CORRECIÓN DE NPE ---
            if (evento.getEquipo() == null || evento.getEquipo().getIntegrantes() == null) {
                return "0";
            }
            // --- FIN CORRECCIÓN ---
            return String.valueOf(evento.getEquipo().getIntegrantes().size());
        })).setHeader("Integrantes Notificados").setWidth("180px").setFlexGrow(0);
    }

    private void crearAuditoria() {
        Equipo equipo = selectEquipo.getValue();
        // El creador es el auditor logueado
        Usuario auditor = this.auditorLogueado;

        if (equipo == null || fecha.isEmpty() || titulo.isEmpty()) {
            Notification.show("Todos los campos son obligatorios.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            EventoCalendario auditoria = new EventoCalendario(
                    fecha.getValue(),
                    titulo.getValue(),
                    "Auditoría programada por " + auditor.getNombreCompleto()
            );

            auditoria.setEquipo(equipo);

            // El servicio (actualizado en el paso anterior) guarda Y notifica
            calendarioService.agregarEvento(auditoria, auditor);

            Notification.show("Auditoría programada y notificada.", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            selectEquipo.clear();
            fecha.clear();
            titulo.clear();
            refrescarGrid();

        } catch (Exception e) {
            Notification.show("Error al crear auditoría: " + e.getMessage(), 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private void refrescarGrid() {
        // --- INICIO DE CORRECCIÓN ---
        // Mostramos solo las auditorías del auditor logueado
        // (o todas si es Admin)
        String nombreAuditor = auditorLogueado.getNombreCompleto();
        List<Equipo> misEquipos = equipoRepository.findByAuditor(nombreAuditor);

        if (securityService.isUserAdmin()) {
            // Si es Admin, busca todos los eventos
            grid.setItems(eventoRepo.findAll());
        } else {
            // Si es Cátedra, busca solo eventos de sus equipos
            grid.setItems(eventoRepo.findByEquipoIn(misEquipos));
        }
        // --- FIN DE CORRECCIÓN ---
    }
}