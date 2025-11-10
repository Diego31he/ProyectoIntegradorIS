package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.model.Seccion;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.model.ZonaEntrega;
import com.mmhfgroup.proyectointegrador.repository.SeccionRepository;
import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.service.EntregaService;
import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.FinishedEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@PageTitle("Entregas")
@Route(value = "entregas", layout = EstudianteLayout.class)
@RolesAllowed({"ROLE_ESTUDIANTE", "ROLE_ADMIN"})
public class EntregasView extends VerticalLayout {

    private final EntregaService entregaService;
    private final SeccionRepository seccionRepo;
    private final SecurityService securityService;
    private final NotificacionService notificacionService;

    public EntregasView(EntregaService entregaService, SeccionRepository seccionRepo,
                        SecurityService securityService, NotificacionService notificacionService) {

        this.entregaService = entregaService;
        this.seccionRepo = seccionRepo;
        this.securityService = securityService;
        this.notificacionService = notificacionService;

        setPadding(true);
        setSpacing(true);
        add(new H2("Secciones de Entrega"));

        Accordion accordion = new Accordion();
        accordion.setWidthFull();

        List<Seccion> secciones = seccionRepo.findAll();

        for (Seccion seccion : secciones) {
            VerticalLayout zonasLayout = new VerticalLayout();
            zonasLayout.setSpacing(true);

            List<ZonaEntrega> zonas = seccion.getZonasDeEntrega();

            // --- INICIO DE LA CORRECCIÓN ---
            // Comprobamos si la lista es nula ANTES de usarla
            if (zonas == null || zonas.isEmpty()) {
                // --- FIN DE LA CORRECCIÓN ---
                zonasLayout.add(new Span("No hay zonas de entrega disponibles en esta sección."));
            } else {
                for (ZonaEntrega zona : zonas) {
                    zonasLayout.add(createZonaLayout(zona));
                }
            }
            accordion.add(seccion.getTitulo(), zonasLayout);
        }

        add(accordion);
    }

    private VerticalLayout createZonaLayout(ZonaEntrega zona) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);

        String titulo = zona.getTitulo();
        String fechaCierre = (zona.getFechaCierre() != null)
                ? " (Vence: " + zona.getFechaCierre().toString() + ")"
                : "";

        layout.add(new H3(titulo + fechaCierre));

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".pdf", ".docx", ".jpg", ".png", ".zip");
        upload.setDropLabel(new Span("Arrastrar archivo aquí"));

        if (zona.getFechaCierre() != null && zona.getFechaCierre().isBefore(LocalDate.now())) {
            upload.setEnabled(false);
            layout.add(new Span("La fecha de entrega para esta zona ha finalizado."));
        }

        upload.addFinishedListener(event -> {
            String nombreArchivo = event.getFileName();
            InputStream inputStream = buffer.getInputStream(nombreArchivo);

            Usuario autor = securityService.getAuthenticatedUser();

            Entrega nueva = new Entrega(nombreArchivo, LocalDateTime.now(), zona, autor);
            entregaService.registrarEntrega(nueva);

            String mensaje = "📩 " + autor.getNombre() + " ha subido una entrega: " + nombreArchivo + " (en " + zona.getTitulo() + ")";
            notificacionService.agregarNotificacion(mensaje);
            Notification.show("Entrega '" + nombreArchivo + "' subida con éxito.", 4000, Notification.Position.BOTTOM_CENTER);
        });

        layout.add(upload);
        return layout;
    }
}
