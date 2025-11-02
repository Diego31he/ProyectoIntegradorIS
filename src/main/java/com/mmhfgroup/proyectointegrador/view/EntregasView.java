package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Entrega;
import com.mmhfgroup.proyectointegrador.service.EntregaService;
import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.InputStream;
import java.time.LocalDateTime;

@PageTitle("Entregas")
@Route(value = "entregas", layout = MainLayout.class)
public class EntregasView extends VerticalLayout {

    private final EntregaService servicio; // <-- 1. Quitar el "new EntregaService()"
    private final NotificacionService notificacionService = new NotificacionService();
    private final Grid<Entrega> grid = new Grid<>(Entrega.class);

    // 2. Pedir el servicio en el constructor (Spring lo inyectará)
    public EntregasView(EntregaService servicio) {
        this.servicio = servicio; // 3. Asignarlo

        setPadding(true);        setSpacing(true);
        add(new H2("Gestión de Entregas"));

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropLabel(new com.vaadin.flow.component.html.Span("Arrastrar o seleccionar un archivo"));
        upload.setAcceptedFileTypes(".pdf", ".docx", ".jpg", ".png");

        upload.addSucceededListener(event -> {
            String nombreArchivo = event.getFileName();
            InputStream inputStream = buffer.getInputStream();

            // Registrar la entrega
            Entrega nueva = new Entrega(nombreArchivo, LocalDateTime.now());
            servicio.registrarEntrega(nueva);
            grid.setItems(servicio.listarEntregas());

            // Generar la notificación
            String mensaje = "📩 Se ha subido una nueva entrega: " + nombreArchivo;
            notificacionService.agregarNotificacion(mensaje);
            Notification.show(mensaje, 4000, Notification.Position.BOTTOM_CENTER);
        });

        grid.setColumns("nombreArchivo", "fechaHora");
        grid.getColumnByKey("nombreArchivo").setHeader("Archivo");
        grid.getColumnByKey("fechaHora").setHeader("Fecha y Hora");
        grid.setItems(servicio.listarEntregas());

        Button eliminar = new Button("Eliminar entrega seleccionada", e -> {
            Entrega seleccionada = grid.asSingleSelect().getValue();
            if (seleccionada != null) {
                servicio.eliminarEntrega(seleccionada);
                grid.setItems(servicio.listarEntregas());
                Notification.show("Entrega eliminada");
            }
        });

        add(upload, eliminar, grid);
        grid.setItems(servicio.listarEntregas());
    }
}
