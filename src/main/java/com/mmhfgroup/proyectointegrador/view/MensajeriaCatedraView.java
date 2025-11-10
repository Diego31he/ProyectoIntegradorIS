package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Mensaje;
import com.mmhfgroup.proyectointegrador.model.MensajePrivado;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.repository.UsuarioRepository;
import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.service.ForoService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Mensajería de Cátedra")
@Route(value = "catedra/mensajeria", layout = CatedraLayout.class)
@RolesAllowed({"CATEDRA", "ADMIN"})
public class MensajeriaCatedraView extends HorizontalLayout {

    private final ForoService foroService;
    private final UsuarioRepository usuarioRepository;
    private final Usuario usuarioActual;

    private final Grid<Mensaje> gridPublico = new Grid<>(Mensaje.class);
    private final Grid<MensajePrivado> gridPrivados = new Grid<>(MensajePrivado.class);

    @Autowired
    public MensajeriaCatedraView(ForoService foroService, SecurityService securityService, UsuarioRepository usuarioRepository) {
        this.foroService = foroService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioActual = securityService.getAuthenticatedUser();

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // ... (El código de 'FORO PÚBLICO' se mantiene igual) ...
        VerticalLayout foroPublico = new VerticalLayout();
        foroPublico.setSizeFull();
        foroPublico.setWidth("50%");
        foroPublico.add(new H2("Foro de Consultas (Público)"));
        TextArea mensaje = new TextArea("Mensaje");
        mensaje.setLabel("Escribe tu mensaje (publicado como: " + usuarioActual.getNombre() + ")");
        mensaje.setWidthFull();
        Button publicar = new Button("Publicar");
        publicar.addClickListener(e -> {
            foroService.publicarMensaje(usuarioActual, mensaje.getValue());
            mensaje.clear();
            refrescarMensajesPublicos();
            Notification.show("Mensaje publicado", 3000, Notification.Position.BOTTOM_CENTER);
        });
        gridPublico.setColumns("autorNombre", "contenido", "fechaHora");
        gridPublico.getColumnByKey("autorNombre").setHeader("Autor");
        gridPublico.getColumnByKey("contenido").setHeader("Mensaje");
        gridPublico.getColumnByKey("fechaHora").setHeader("Fecha y Hora");
        foroPublico.add(mensaje, publicar, gridPublico);
        foroPublico.setFlexGrow(1, gridPublico);

        // === MENSAJES PRIVADOS ===
        VerticalLayout panelPrivado = new VerticalLayout();
        panelPrivado.setSizeFull();
        panelPrivado.setWidth("50%");

        H2 tituloPrivado = new H2("Mensajes Privados");
        Button botonPrivado = new Button("✉️ Nuevo Mensaje Privado");
        botonPrivado.addClickListener(e -> abrirDialogoPrivado());

        VerticalLayout encabezadoPrivado = new VerticalLayout(tituloPrivado, botonPrivado);
        encabezadoPrivado.setAlignItems(Alignment.CENTER);
        encabezadoPrivado.setSpacing(false);
        encabezadoPrivado.setPadding(false);

        gridPrivados.setColumns("titulo", "remitenteNombre", "destinatariosNombres", "fechaHora");
        gridPrivados.getColumnByKey("titulo").setHeader("Título");
        gridPrivados.getColumnByKey("remitenteNombre").setHeader("Remitente");
        gridPrivados.getColumnByKey("destinatariosNombres").setHeader("Destinatarios");
        gridPrivados.getColumnByKey("fechaHora").setHeader("Fecha y Hora");

        // --- INICIO DE CORRECCIÓN (Llamada inicial) ---
        gridPrivados.setItems(foroService.listarPrivados(usuarioActual)); // <-- Pasamos el usuario
        // --- FIN DE CORRECCIÓN ---

        gridPrivados.setWidthFull();
        panelPrivado.add(encabezadoPrivado, gridPrivados);
        panelPrivado.setFlexGrow(1, gridPrivados);

        setFlexGrow(1, foroPublico, panelPrivado);
        add(foroPublico, panelPrivado);

        // === ACTUALIZACIÓN AUTOMÁTICA ===
        UI ui = UI.getCurrent();
        getUI().ifPresent(current -> current.setPollInterval(2000));
        addAttachListener(event -> ui.addPollListener(ev -> {
            refrescarMensajesPublicos();
            refrescarPrivados(); // <-- Este método ahora usará el usuario actual
        }));

        refrescarMensajesPublicos();
        refrescarPrivados();
    }

    private void refrescarMensajesPublicos() {
        gridPublico.setItems(foroService.listarMensajesPublicos());
    }

    // --- INICIO DE CORRECCIÓN (Método de refresco) ---
    private void refrescarPrivados() {
        // Pasamos el usuario (que es un campo 'final' de la clase)
        gridPrivados.setItems(foroService.listarPrivados(usuarioActual));
    }
    // --- FIN DE CORRECCIÓN ---

    private void abrirDialogoPrivado() {
        // ... (Este método se mantiene igual) ...
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Enviar Mensaje Privado");
        TextField titulo = new TextField("Título");
        MultiSelectComboBox<Usuario> destinatarios = new MultiSelectComboBox<>("Destinatarios");
        List<Usuario> todosMenosYo = usuarioRepository.findAll().stream()
                .filter(u -> !u.getId().equals(usuarioActual.getId()))
                .toList();
        destinatarios.setItems(todosMenosYo);
        destinatarios.setItemLabelGenerator(Usuario::getNombre);
        TextArea contenido = new TextArea("Mensaje");
        contenido.setWidth("400px");
        Button enviar = new Button("Enviar", e -> {
            List<Usuario> listaDest = new ArrayList<>(destinatarios.getValue());
            MensajePrivado msg = new MensajePrivado(
                    titulo.getValue(),
                    usuarioActual,
                    listaDest,
                    contenido.getValue()
            );
            foroService.enviarPrivado(msg);
            Notification.show("Mensaje privado enviado", 4000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
            refrescarPrivados();
        });
        Button cancelar = new Button("Cancelar", e -> dialog.close());
        dialog.add(new VerticalLayout(titulo, destinatarios, contenido, new HorizontalLayout(enviar, cancelar)));
        dialog.open();
    }
}