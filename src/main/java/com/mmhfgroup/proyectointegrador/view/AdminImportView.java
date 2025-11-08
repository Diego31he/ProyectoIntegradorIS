package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.service.DataImportService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.InputStream;

@Route(value = "admin/importar", layout = AdminLayout.class) // <-- 1. CAMBIAR LAYOUT
@PageTitle("Importar Datos")
@RolesAllowed("ROLE_ADMIN")
public class AdminImportView extends VerticalLayout {

    private final DataImportService dataImportService;

    public AdminImportView(DataImportService dataImportService) {
        this.dataImportService = dataImportService;

        add(new H2("Carga de Datos Históricos (Excel)"));

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xlsx");
        upload.setDropLabel(new com.vaadin.flow.component.html.Span("Arrastrar archivo .xlsx aquí"));

        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            try {
                // Llama al servicio refactorizado
                dataImportService.importarDesdeStream(inputStream);

                Notification.show("¡Importación completada con éxito!", 5000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Error en la importación: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        add(upload);
    }
}