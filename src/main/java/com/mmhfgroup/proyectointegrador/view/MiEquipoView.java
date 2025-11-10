package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.*;
import com.mmhfgroup.proyectointegrador.repository.SeccionRepository;
import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.service.EntregaService;
import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
// No se necesitan imports de Location o JS
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@PageTitle("Mi equipo")
@Route(value = "mi-equipo", layout = EstudianteLayout.class)
@RolesAllowed({"ROLE_ESTUDIANTE","ROLE_CATEDRA","ROLE_ADMIN"})
public class MiEquipoView extends VerticalLayout {

    private final EntregaService entregaService;
    private final SeccionRepository seccionRepo;
    private final SecurityService securityService;
    private final NotificacionService notificacionService; // Se mantiene para el listener

    private final VerticalLayout entregasSection = new VerticalLayout();
    private final VerticalLayout auditoriasSection = new VerticalLayout();

    public MiEquipoView(EntregaService entregaService,
                        SeccionRepository seccionRepo,
                        SecurityService securityService,
                        NotificacionService notificacionService) {
        this.entregaService = entregaService;
        this.seccionRepo = seccionRepo;
        this.securityService = securityService;
        this.notificacionService = notificacionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Usuario actual = securityService.getAuthenticatedUser();
        Equipo equipo = (actual instanceof Estudiante est) ? est.getEquipo() : null;

        add(buildHeader(equipo));
        add(buildActionButtons(equipo));

        entregasSection.setVisible(false);
        auditoriasSection.setVisible(false);

        buildEntregasSection(equipo);
        buildAuditoriasSection(equipo);

        add(entregasSection, auditoriasSection);

        if (equipo == null) {
            Notification.show("No tenés equipo asignado. Pedí a cátedra que te asigne uno.",
                    4000, Notification.Position.TOP_CENTER);
        }
    }

    // ... (buildHeader se mantiene igual) ...
    private Component buildHeader(Equipo equipo) {
        H2 titulo = new H2("Mi equipo");
        titulo.getStyle().set("margin-bottom", "0");
        Paragraph sub = new Paragraph(
                (equipo == null)
                        ? "Sin equipo asignado"
                        : "Equipo N° " + equipo.getNumero() + " — " + equipo.getNombre() +
                        (equipo.getAuditor() != null ? " — Auditor: " + equipo.getAuditor() : "")
        );
        sub.getStyle().set("color", "#666");
        VerticalLayout box = new VerticalLayout(titulo, sub);
        box.setPadding(false);
        box.setSpacing(false);
        return box;
    }

    // ... (buildActionButtons se mantiene igual) ...
    private Component buildActionButtons(Equipo equipo) {
        HorizontalLayout contenedor = new HorizontalLayout();
        contenedor.setWidthFull();
        contenedor.setJustifyContentMode(JustifyContentMode.START);
        contenedor.setSpacing(true);
        contenedor.getStyle().set("flex-wrap", "wrap");

        contenedor.add(createBigCard(
                VaadinIcon.UPLOAD, "Entregas", "#9C27B0",
                () -> {
                    if (equipo == null) {
                        Notification.show("Asignate a un equipo para ver sus entregas.",
                                3000, Notification.Position.MIDDLE);
                        return;
                    }
                    auditoriasSection.setVisible(false);
                    entregasSection.setVisible(true);
                }
        ));
        contenedor.add(createBigCard(
                VaadinIcon.CLIPBOARD_CHECK, "Auditorías", "#03A9F4",
                () -> {
                    if (equipo == null) {
                        Notification.show("Asignate a un equipo para ver sus auditorías.",
                                3000, Notification.Position.MIDDLE);
                        return;
                    }
                    entregasSection.setVisible(false);
                    auditoriasSection.setVisible(true);
                }
        ));
        contenedor.add(createBigCard(
                VaadinIcon.LINK, "Enlace Director", "#4CAF50",
                () -> {
                    if (equipo == null) {
                        Notification.show("Asignate a un equipo para generar un enlace.",
                                3000, Notification.Position.MIDDLE);
                        return;
                    }
                    entregasSection.setVisible(false);
                    auditoriasSection.setVisible(false);
                    mostrarDialogoEnlace(equipo);
                }
        ));
        return contenedor;
    }

    // ... (createBigCard se mantiene igual) ...
    private Component createBigCard(VaadinIcon icono, String texto, String colorFondo, Runnable onClick) {
        Icon icon = icono.create();
        icon.setColor("white");
        icon.setSize("40px");
        Div fondoIcono = new Div(icon);
        fondoIcono.getStyle()
                .set("background-color", colorFondo)
                .set("width", "110px")
                .set("height", "110px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("border-radius", "14px")
                .set("box-shadow", "2px 2px 8px rgba(0,0,0,0.25)")
                .set("cursor", "pointer")
                .set("transition", "transform 0.18s ease-in-out");
        fondoIcono.getElement().addEventListener("mouseenter", e -> fondoIcono.getStyle().set("transform", "scale(1.05)"));
        fondoIcono.getElement().getStyle().set("margin-right", "18px");
        fondoIcono.getElement().getStyle().set("margin-bottom", "14px");
        fondoIcono.getElement().addEventListener("mouseleave", e -> fondoIcono.getStyle().set("transform", "scale(1)"));
        fondoIcono.addClickListener(e -> onClick.run());
        Paragraph etiqueta = new Paragraph(texto);
        etiqueta.getStyle()
                .set("margin", "10px 0 0 0")
                .set("font-weight", "600")
                .set("color", "#555")
                .set("font-size", "15px")
                .set("text-align", "center");
        VerticalLayout card = new VerticalLayout(fondoIcono, etiqueta);
        card.setAlignItems(Alignment.CENTER);
        card.setPadding(false);
        card.setSpacing(false);
        return card;
    }

    /**
     * Muestra un diálogo modal con el enlace público del equipo.
     */
    private void mostrarDialogoEnlace(Equipo equipo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Enlace para Director");

        // --- INICIO CORRECCIÓN 1: 'getLocation' / 'getHref' ---
        // Simplemente ponemos el texto visual que pediste
        String enlace = "proyectointegrador.com/equipo" + equipo.getNumero();
        // --- FIN CORRECCIÓN 1 ---

        TextField campoEnlace = new TextField("Enlace de solo lectura");
        campoEnlace.setValue(enlace);
        campoEnlace.setReadOnly(true);
        campoEnlace.setWidth("400px");

        // --- INICIO CORRECCIÓN 2: 'select' ---
        // Usamos JS para seleccionar el texto en el foco
        campoEnlace.addFocusListener(e -> {
            campoEnlace.getElement().executeJs("this.select()");
        });
        // --- FIN CORRECCIÓN 2 ---

        dialog.add(new VerticalLayout(
                new Span("Comparte este enlace con el director o auditor:"),
                campoEnlace
        ));

        Button cerrarButton = new Button("Cerrar", e -> dialog.close());
        dialog.getFooter().add(cerrarButton);

        dialog.open();
    }


    // ===================== ENTREGAS =====================

    private void buildEntregasSection(Equipo equipo) {
        // ... (Se mantiene igual) ...
        entregasSection.setWidthFull();
        entregasSection.setPadding(true);
        entregasSection.setSpacing(true);
        entregasSection.getStyle().set("background", "var(--lumo-base-color)");
        entregasSection.getStyle().set("border-radius", "12px");
        entregasSection.getStyle().set("box-shadow", "0 2px 8px rgba(0,0,0,0.06)");
        H3 titulo = new H3("Entregas del equipo por sección");
        titulo.getStyle().set("margin-top", "0");
        entregasSection.add(titulo);
        if (equipo == null) {
            entregasSection.add(new Paragraph("No tenés equipo asignado. No es posible subir entregas."));
            return;
        }
        List<Seccion> secciones = seccionRepo.findAll();
        if (secciones == null || secciones.isEmpty()) {
            entregasSection.add(new Paragraph("Aún no hay secciones de entrega creadas por cátedra."));
            return;
        }
        for (Seccion seccion : secciones) {
            VerticalLayout sectionBox = new VerticalLayout();
            sectionBox.setWidthFull();
            sectionBox.setPadding(true);
            sectionBox.setSpacing(false);
            sectionBox.getStyle()
                    .set("border", "1px solid var(--lumo-contrast-10pct)")
                    .set("border-radius", "10px")
                    .set("margin-bottom", "12px");
            H4 secTitle = new H4(seccion.getTitulo());
            secTitle.getStyle().set("margin", "0 0 6px 0");
            if (seccion.getDescripcion() != null && !seccion.getDescripcion().isBlank()) {
                Paragraph desc = new Paragraph(seccion.getDescripcion());
                desc.getStyle().set("margin", "0 0 12px 0").set("color", "#666");
                sectionBox.add(secTitle, desc);
            } else {
                sectionBox.add(secTitle);
            }
            List<ZonaEntrega> zonas = seccion.getZonasDeEntrega();
            if (zonas == null || zonas.isEmpty()) {
                sectionBox.add(new Paragraph("No hay zonas de entrega en esta sección."));
            } else {
                for (ZonaEntrega zona : zonas) {
                    sectionBox.add(buildZonaUploader(zona));
                }
            }
            entregasSection.add(sectionBox);
        }
    }

    private Component buildZonaUploader(ZonaEntrega zona) {
        VerticalLayout box = new VerticalLayout();
        box.setPadding(false);
        box.setSpacing(false);
        box.setWidthFull();
        String fecha = (zona.getFechaCierre() != null) ? " (vence: " + zona.getFechaCierre() + ")" : "";
        H5 title = new H5("Zona: " + zona.getTitulo() + fecha);
        title.getStyle().set("margin", "0 0 6px 0");
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropLabel(new Span("Arrastrar o seleccionar archivos aquí"));
        upload.setMaxFiles(5);
        upload.setWidthFull();
        upload.getElement().getStyle().set("min-height", "140px");
        upload.getElement().getStyle().set("border", "1px dashed var(--lumo-contrast-30pct)");
        upload.getElement().getStyle().set("border-radius", "10px");
        upload.getElement().getStyle().set("padding", "14px");
        upload.setAcceptedFileTypes(".pdf", ".docx", ".jpg", ".png", ".zip");
        if (zona.getFechaCierre() != null && zona.getFechaCierre().isBefore(LocalDate.now())) {
            upload.setEnabled(false);
            Paragraph closedMsg = new Paragraph("La fecha de entrega para esta zona ha finalizado.");
            closedMsg.getStyle().set("color", "#b00020");
            box.add(title, closedMsg);
            return box;
        }

        upload.addFinishedListener(e -> {
            String nombreArchivo = e.getFileName();
            InputStream is = buffer.getInputStream(nombreArchivo);
            Usuario autor = securityService.getAuthenticatedUser();

            Entrega nueva = new Entrega(nombreArchivo, LocalDateTime.now(), zona, autor);
            // Esta única línea se encarga de guardar Y notificar
            entregaService.registrarEntrega(nueva);

            // --- INICIO CORRECCIÓN 3: 'agregarNotificacion' ---
            // Esta línea causaba el error
            // notificacionService.agregarNotificacion(mensaje); // <-- ELIMINADO
            // --- FIN CORRECCIÓN 3 ---

            Notification.show("Archivo '" + nombreArchivo + "' subido correctamente.",
                    3500, Notification.Position.BOTTOM_CENTER);
        });

        box.add(title, upload);
        return box;
    }

    // ===================== AUDITORÍAS =====================

    private void buildAuditoriasSection(Equipo equipo) {
        // ... (Este método se mantiene igual) ...
        auditoriasSection.setWidthFull();
        auditoriasSection.setPadding(true);
        auditoriasSection.setSpacing(true);
        auditoriasSection.getStyle().set("background", "var(--lumo-base-color)");
        auditoriasSection.getStyle().set("border-radius", "12px");
        auditoriasSection.getStyle().set("box-shadow", "0 2px 8px rgba(0,0,0,0.06)");
        H3 titulo = new H3("Auditorías del equipo");
        titulo.getStyle().set("margin-top", "0");
        auditoriasSection.add(titulo);
        if (equipo == null) {
            auditoriasSection.add(new Paragraph("No tenés equipo asignado."));
            return;
        }
        Paragraph empty = new Paragraph("Próximas auditorías del equipo aparecerán aquí.");
        empty.getStyle().set("color", "#666");
        auditoriasSection.add(empty);
    }
}