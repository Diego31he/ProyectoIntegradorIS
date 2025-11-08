package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Mensaje;
import com.mmhfgroup.proyectointegrador.model.MensajePrivado;
import com.mmhfgroup.proyectointegrador.service.ForoService;
import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@PageTitle("Foro de Consultas")
@Route(value = "foro", layout = EstudianteLayout.class)
public class ForoView extends HorizontalLayout {

    private final ForoService servicio = new ForoService();
    private final NotificacionService notificaciones = new NotificacionService();

    private final Grid<Mensaje> gridPublico = new Grid<>(Mensaje.class);
    private final Grid<MensajePrivado> gridPrivados = new Grid<>(MensajePrivado.class);

    public ForoView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // === FORO PÚBLICO ===
        VerticalLayout foroPublico = new VerticalLayout();
        foroPublico.setSizeFull();
        foroPublico.setWidth("50%");
        foroPublico.add(new H2("Foro de Consultas (Público)"));

        TextField autor = new TextField("Usuario");
        TextArea mensaje = new TextArea("Mensaje");
        mensaje.setWidthFull();
        Button publicar = new Button("Publicar");

        publicar.addClickListener(e -> {
            servicio.publicarMensaje(autor.getValue(), mensaje.getValue());
            mensaje.clear();
            refrescarMensajesPublicos();
            Notification.show("💬 Mensaje publicado", 3000, Notification.Position.BOTTOM_CENTER);
        });

        gridPublico.setColumns("autor", "contenido", "fechaHora");
        gridPublico.getColumnByKey("autor").setHeader("Autor");
        gridPublico.getColumnByKey("contenido").setHeader("Mensaje");
        gridPublico.getColumnByKey("fechaHora").setHeader("Fecha y Hora");

        foroPublico.add(new HorizontalLayout(autor, publicar), mensaje, gridPublico);
        foroPublico.setFlexGrow(1, gridPublico);

        // === MENSAJES PRIVADOS ===
        VerticalLayout panelPrivado = new VerticalLayout();
        panelPrivado.setSizeFull();
        panelPrivado.setWidth("50%");

        // Sub-layout centrado para el título y el botón
        H2 tituloPrivado = new H2("Mensajes Privados");
        Button botonPrivado = new Button("✉️ Nuevo Mensaje Privado");
        botonPrivado.addClickListener(e -> abrirDialogoPrivado());

        VerticalLayout encabezadoPrivado = new VerticalLayout(tituloPrivado, botonPrivado);
        encabezadoPrivado.setAlignItems(Alignment.CENTER); // centra el texto y botón
        encabezadoPrivado.setSpacing(false);
        encabezadoPrivado.setPadding(false);

        // Tabla de mensajes privados
        gridPrivados.setColumns("titulo", "remitente", "destinatarios", "fechaHora");
        gridPrivados.getColumnByKey("titulo").setHeader("Título");
        gridPrivados.getColumnByKey("remitente").setHeader("Remitente");
        gridPrivados.getColumnByKey("destinatarios").setHeader("Destinatarios");
        gridPrivados.getColumnByKey("fechaHora").setHeader("Fecha y Hora");
        gridPrivados.setItems(servicio.listarPrivados());
        gridPrivados.setWidthFull();

        panelPrivado.add(encabezadoPrivado, gridPrivados);
        panelPrivado.setFlexGrow(1, gridPrivados);

        // === DISTRIBUCIÓN IGUAL (50/50) ===
        setFlexGrow(1, foroPublico, panelPrivado);
        add(foroPublico, panelPrivado);

        // === ACTUALIZACIÓN AUTOMÁTICA ===
        UI ui = UI.getCurrent();
        getUI().ifPresent(current -> current.setPollInterval(2000));
        addAttachListener(event -> ui.addPollListener(ev -> {
            refrescarMensajesPublicos();
            refrescarPrivados();
        }));

        refrescarMensajesPublicos();
        refrescarPrivados();
    }

    private void refrescarMensajesPublicos() {
        gridPublico.setItems(servicio.listarMensajesPublicos());
    }

    private void refrescarPrivados() {
        gridPrivados.setItems(servicio.listarPrivados());
    }

    private void abrirDialogoPrivado() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Enviar Mensaje Privado");

        TextField remitente = new TextField("Remitente");
        TextField titulo = new TextField("Título");
        TextField destinatarios = new TextField("Destinatarios (separados por coma)");
        TextArea contenido = new TextArea("Mensaje");
        contenido.setWidth("400px");

        Button enviar = new Button("Enviar", e -> {
            List<String> listaDest = Arrays.asList(destinatarios.getValue().split(","));
            MensajePrivado msg = new MensajePrivado(
                    titulo.getValue(),
                    listaDest,
                    contenido.getValue(),
                    remitente.getValue(),
                    LocalDateTime.now()
            );

            servicio.enviarPrivado(msg);

            // Registrar en notificaciones
            String aviso = "✉️ Mensaje privado enviado: " + titulo.getValue() + " para " + destinatarios.getValue();
            notificaciones.agregarNotificacion(aviso);

            Notification notif = Notification.show("Mensaje privado enviado correctamente", 4000, Notification.Position.BOTTOM_CENTER);
            notif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            dialog.close();
            refrescarPrivados();
        });

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        dialog.add(new VerticalLayout(remitente, titulo, destinatarios, contenido, new HorizontalLayout(enviar, cancelar)));
        dialog.open();
    }
}
