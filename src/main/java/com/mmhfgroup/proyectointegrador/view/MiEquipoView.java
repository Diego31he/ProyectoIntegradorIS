package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import com.mmhfgroup.proyectointegrador.service.EntregaService;
import com.mmhfgroup.proyectointegrador.service.EquipoService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Route(value = "mi-equipo", layout = EstudianteLayout.class)
@PageTitle("Mi equipo | Proyecto Integrador")
@PermitAll
public class MiEquipoView extends VerticalLayout {

    private final UsuarioRepository usuarioRepo;
    private final EquipoService equipoService;
    private final EntregaService entregaService;

    // Secciones
    private final VerticalLayout entregasLayout = new VerticalLayout();
    private final VerticalLayout auditoriasLayout = new VerticalLayout();

    // Grids
    private final Grid<Estudiante> gridIntegrantes = new Grid<>(Estudiante.class, false);
    private final Grid<Entrega> gridEntregas = new Grid<>(Entrega.class, false);

    // Estado
    private Equipo equipoActual;
    private Estudiante yo;

    public MiEquipoView(UsuarioRepository usuarioRepo,
                        EquipoService equipoService,
                        EntregaService entregaService) {
        this.usuarioRepo = usuarioRepo;
        this.equipoService = equipoService;
        this.entregaService = entregaService;

        setPadding(true);
        setSpacing(true);
        setSizeFull();

        add(new H2("Mi equipo"));

        // Usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : null;

        if (email == null) {
            Notification.show("No se pudo identificar al usuario actual.", 4000, Notification.Position.MIDDLE);
            add(new Paragraph("Error de autenticación."));
            return;
        }

        Optional<Usuario> maybeUser = usuarioRepo.findByEmail(email);
        if (maybeUser.isEmpty() || !(maybeUser.get() instanceof Estudiante)) {
            add(new Paragraph("Tu usuario no está registrado como Estudiante."));
            return;
        }
        yo = (Estudiante) maybeUser.get();
        equipoActual = yo.getEquipo();

        if (equipoActual == null) {
            Paragraph header = new Paragraph("Sin equipo asignado");
            header.getStyle().set("font-weight", "600");
            add(header);

            add(new Paragraph("Cuando tengas equipo asignado, acá vas a ver: número, nombre, auditor, integrantes y entregas."));
            HorizontalLayout atajos = new HorizontalLayout(
                    crearBotonAtajo("Notificaciones", VaadinIcon.BELL, "notificaciones"),
                    crearBotonAtajo("Foro", VaadinIcon.MAILBOX, "foro")
            );
            add(atajos);
            return;
        }

        // Cabecera con datos del equipo
        String auditor = (equipoActual.getAuditor() == null || equipoActual.getAuditor().isBlank()) ? "—" : equipoActual.getAuditor();
        Paragraph header = new Paragraph("Equipo N° " + equipoActual.getNumero() + " — " + equipoActual.getNombre() + " (Auditor: " + auditor + ")");
        header.getStyle().set("font-weight", "600");
        add(header);

        // === Botones grandes “Entregas / Auditorías” (estilo inicio) ===
        HorizontalLayout botonesAcceso = new HorizontalLayout();
        botonesAcceso.setWidthFull();
        botonesAcceso.setSpacing(true);
        botonesAcceso.getStyle()
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("justify-content", "center");

        Button btnEntregas = new Button("Entregas", new Icon(VaadinIcon.CLIPBOARD_CHECK));
        btnEntregas.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnEntregas.getStyle()
                .set("width", "220px")
                .set("height", "80px")
                .set("font-size", "18px");

        Button btnAuditorias = new Button("Auditorías", new Icon(VaadinIcon.SEARCH));
        btnAuditorias.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_LARGE);
        btnAuditorias.getStyle()
                .set("width", "220px")
                .set("height", "80px")
                .set("font-size", "18px");

        btnEntregas.addClickListener(e -> {
            entregasLayout.setVisible(true);
            auditoriasLayout.setVisible(false);
        });
        btnAuditorias.addClickListener(e -> {
            entregasLayout.setVisible(false);
            auditoriasLayout.setVisible(true);
        });

        botonesAcceso.add(btnEntregas, btnAuditorias);
        add(botonesAcceso);

        // === Integrantes del equipo ===
        List<Estudiante> integrantes = equipoService.integrantesDe(equipoActual.getId());
        gridIntegrantes.addColumn(Estudiante::getApellido).setHeader("Apellido").setAutoWidth(true).setSortable(true);
        gridIntegrantes.addColumn(Estudiante::getNombre).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        gridIntegrantes.addColumn(Estudiante::getEmail).setHeader("Email").setAutoWidth(true);
        gridIntegrantes.addColumn(Estudiante::getLegajo).setHeader("Legajo").setAutoWidth(true);
        gridIntegrantes.setItems(integrantes);
        gridIntegrantes.setWidthFull();
        gridIntegrantes.setHeight("40vh");

        add(new H3("Integrantes"), gridIntegrantes);

        // === Sección ENTREGAS (subida + grid) ===
        construirSeccionEntregas();

        // === Sección AUDITORÍAS (placeholder para poblar luego) ===
        construirSeccionAuditorias();

        // Visibilidad inicial
        entregasLayout.setVisible(true);
        auditoriasLayout.setVisible(false);

        add(entregasLayout, auditoriasLayout);
        setFlexGrow(1, entregasLayout, auditoriasLayout);
    }

    private void construirSeccionEntregas() {
        entregasLayout.setWidthFull();
        entregasLayout.setSpacing(true);

        entregasLayout.add(new H3("Entregas del equipo"));

        // Upload grande y claro
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        Button seleccionarBtn = new Button("Seleccionar archivo");
        seleccionarBtn.getStyle()
                .set("font-weight", "600")
                .set("padding", "0.4rem 1rem");
        upload.setUploadButton(seleccionarBtn);

        upload.setDropLabel(new com.vaadin.flow.component.html.Span(
                "Arrastrá tu archivo aquí o hacé clic en “Seleccionar archivo”"
        ));
        upload.setAcceptedFileTypes(".pdf", ".docx", ".jpg", ".png");
        upload.setMaxFiles(1);
        upload.setAutoUpload(true);

        upload.setWidthFull();
        upload.getElement().getStyle()
                .set("border", "2px dashed var(--lumo-primary-color)")
                .set("border-radius", "10px")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-m)");
        // upload.getElement().getStyle().set("min-height", "160px"); // opcional, más alto

        upload.addSucceededListener(ev -> {
            String nombreArchivo = ev.getFileName();
            try (InputStream is = buffer.getInputStream()) {
                // Si luego guardás el archivo real, hacelo aquí con 'is'
                entregaService.registrarEntrega(equipoActual, nombreArchivo);
                Notification n = Notification.show("Entrega registrada: " + nombreArchivo, 3000, Notification.Position.BOTTOM_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refrescarEntregas();
            } catch (Exception ex) {
                Notification n = Notification.show("Error al registrar la entrega: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Div uploadWrapper = new Div(upload);
        uploadWrapper.getStyle()
                .set("width", "100%")
                .set("margin-bottom", "var(--lumo-space-m)");

        // Grid de entregas
        gridEntregas.addColumn(Entrega::getNombreArchivo).setHeader("Archivo").setAutoWidth(true);
        gridEntregas.addColumn(e -> e.getFechaHora().toString()).setHeader("Fecha y hora").setAutoWidth(true);
        gridEntregas.setItems(entregaService.listarPorEquipo(equipoActual.getId()));
        gridEntregas.setWidthFull();
        gridEntregas.setHeight("40vh");

        Button eliminar = new Button("Eliminar entrega seleccionada", e -> {
            Entrega seleccionada = gridEntregas.asSingleSelect().getValue();
            if (seleccionada != null) {
                entregaService.eliminar(seleccionada.getId());
                Notification.show("Entrega eliminada", 2500, Notification.Position.BOTTOM_CENTER);
                refrescarEntregas();
            }
        });

        HorizontalLayout acciones = new HorizontalLayout(eliminar);
        acciones.setSpacing(true);

        entregasLayout.add(uploadWrapper, acciones, gridEntregas);
    }

    private void construirSeccionAuditorias() {
        auditoriasLayout.setWidthFull();
        auditoriasLayout.setSpacing(true);

        auditoriasLayout.add(new H3("Auditorías programadas"));

        // Placeholder: más adelante lo conectamos a AuditoriaService/Repo
        Paragraph vacio = new Paragraph(
                "Aún no hay auditorías programadas para este equipo. " +
                        "Cuando estén disponibles, las verás listadas aquí."
        );
        vacio.getStyle().set("color", "var(--lumo-secondary-text-color)");

        auditoriasLayout.add(vacio);
    }

    private void refrescarEntregas() {
        gridEntregas.setItems(entregaService.listarPorEquipo(equipoActual.getId()));
    }

    private Button crearBotonAtajo(String texto, VaadinIcon icono, String ruta) {
        Button b = new Button(texto, new Icon(icono));
        b.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        b.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ruta)));
        return b;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // Por si el usuario reabre la vista, refrescamos la grilla de entregas
        if (equipoActual != null) {
            refrescarEntregas();
        }
    }
}
