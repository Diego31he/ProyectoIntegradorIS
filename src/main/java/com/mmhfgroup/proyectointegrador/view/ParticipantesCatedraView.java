package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.service.UsuarioAdminService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Participantes")
@Route(value = "catedra/participantes", layout = CatedraLayout.class) // <-- Layout de Cátedra
@RolesAllowed({"CATEDRA", "ADMIN"}) // <-- Seguridad para Cátedra
public class ParticipantesCatedraView extends VerticalLayout {

    private final Grid<Usuario> grid = new Grid<>(Usuario.class);

    @Autowired
    public ParticipantesCatedraView(UsuarioAdminService usuarioService) {
        setSizeFull();
        setPadding(true);
        add(new H2("Lista de Todos los Participantes"));

        configurarGrid();
        grid.setItems(usuarioService.listarTodos()); // <-- Llama al servicio

        add(grid);
    }

    private void configurarGrid() {
        grid.setSizeFull();
        grid.setColumns("id", "email");

        grid.addColumn(this::getNombreCompleto)
                .setHeader("Nombre Completo")
                .setSortable(true);

        grid.addColumn(this::getRol)
                .setHeader("Rol")
                .setSortable(true);
    }

    // Helper para mostrar el nombre
    private String getNombreCompleto(Usuario u) {
        String n = (u.getNombre() != null) ? u.getNombre() : "";
        String a = (u.getApellido() != null) ? u.getApellido() : "";
        return (n + " " + a).trim();
    }

    // Helper para mostrar el rol (basado en tu AdminUsuariosView)
    private String getRol(Usuario u) {
        if (u instanceof Catedra c) {
            return c.isAdmin() ? "Admin" : "Cátedra";
        } else if (u instanceof Estudiante) {
            return "Estudiante";
        }
        return "Usuario";
    }
}