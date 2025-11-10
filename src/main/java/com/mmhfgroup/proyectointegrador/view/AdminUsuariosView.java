package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.service.UsuarioAdminService; // <-- Servicio correcto
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
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

@Route(value = "admin/usuarios", layout = AdminLayout.class)
@PageTitle("Usuarios")
@RolesAllowed("ROLE_ADMIN")
public class AdminUsuariosView extends VerticalLayout {

    // --- INICIO DE CORRECCIÓN ---

    // Solo necesitamos el UsuarioAdminService
    private final UsuarioAdminService usuarioService;

    private final Grid<Usuario> grid = new Grid<>(Usuario.class, false);

    // Mantenemos solo el Select de Rol para el formulario
    private Select<String> selectRol = new Select<>();
    private Button botonGuardarRol = new Button("Guardar Rol");
    private Usuario usuarioSeleccionado;

    public AdminUsuariosView(UsuarioAdminService usuarioService) {
        this.usuarioService = usuarioService;
        // --- FIN DE CORRECCIÓN ---

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        var title = new H2("Administración de Usuarios y Roles");
        var recargar = new Button("Recargar", new Icon(VaadinIcon.REFRESH), e -> recargarDatos());
        add(new HorizontalLayout(title, recargar));

        configurarGrid();
        recargarDatos();
    }

    // Definimos los roles que soporta el UsuarioAdminService
    private enum RolOption { ESTUDIANTE, CATEDRA, ADMIN }

    private void configurarGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addColumn(Usuario::getEmail)
                .setHeader("Email").setAutoWidth(true).setResizable(true);

        grid.addColumn(u -> ((u.getNombre() == null ? "" : u.getNombre()) + " " + (u.getApellido() == null ? "" : u.getApellido())).trim())
                .setHeader("Nombre").setAutoWidth(true).setResizable(true);

        // Rol (select)
        grid.addColumn(new ComponentRenderer<>(this::createRoleSelect))
                .setHeader("Rol").setAutoWidth(true);

        // --- INICIO DE CORRECCIÓN ---
        // Columnas de "Equipo" y "Acciones de equipo" eliminadas
        // --- FIN DE CORRECCIÓN ---

        add(grid);
    }

    private Component createRoleSelect(Usuario u) {
        Select<RolOption> sel = new Select<>();
        sel.setItems(RolOption.values());
        sel.setWidth("180px");
        sel.setValue(detectarRol(u));

        sel.addValueChangeListener(ev -> {
            if (!ev.isFromClient()) return;
            try {
                // CORRECCIÓN: Usamos el ID del usuario y el nombre del Rol (String)
                usuarioService.cambiarRol(u.getId(), ev.getValue().name());

                Notification n = Notification.show("Rol actualizado a " + ev.getValue());
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                recargarDatos();
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification n = Notification.show("No se pudo cambiar el rol: " + ex.getMessage());
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                // Revertir el cambio visual en el select
                sel.setValue(ev.getOldValue());
            }
        });
        return sel;
    }

    private RolOption detectarRol(Usuario u) {
        if (u instanceof Catedra c) {
            return c.isAdmin() ? RolOption.ADMIN : RolOption.CATEDRA;
        }
        return RolOption.ESTUDIANTE;
    }

    private void recargarDatos() {
        // CORRECCIÓN: Usamos el método 'listarTodos' del servicio
        grid.setItems(usuarioService.listarTodos());
    }

    // --- INICIO DE CORRECCIÓN ---
    // Toda la lógica de equipo (accionesEquipoSiempreEstudiante, asignarEstudianteAEquipo, etc.)
    // ha sido eliminada de esta vista.
    // --- FIN DE CORRECCIÓN ---
}