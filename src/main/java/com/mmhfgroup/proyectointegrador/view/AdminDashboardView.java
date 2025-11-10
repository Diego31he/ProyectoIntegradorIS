package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.Component; // <-- AÑADIDO
import com.vaadin.flow.component.UI; // <-- AÑADIDO
import com.vaadin.flow.component.button.Button; // <-- AÑADIDO
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon; // <-- AÑADIDO
import com.vaadin.flow.component.orderedlayout.HorizontalLayout; // <-- AÑADIDO
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = AdminLayout.class) // <-- Ruta principal de admin
@PageTitle("Admin Dashboard")
@RolesAllowed("ROLE_ADMIN")
public class AdminDashboardView extends VerticalLayout {

    public AdminDashboardView() {
        setAlignItems(Alignment.CENTER);
        setSizeFull();

        add(new H1("Panel de Administración"));

        // --- INICIO DE CORRECCIÓN (Petición 2) ---

        // Creamos la fila de botones
        HorizontalLayout row1 = new HorizontalLayout();
        row1.add(
                createNavButton("Gestionar Usuarios", VaadinIcon.USER_CHECK, AdminUsuariosView.class),
                createNavButton("Importar Datos", VaadinIcon.UPLOAD, AdminImportView.class),
                createNavButton("Participantes",VaadinIcon.USERS, ParticipantesCatedraView.class)
        );

        add(row1);

        // --- FIN DE CORRECCIÓN ---
    }

    // --- MÉTODO AÑADIDO ---
    // Método para crear botones con el estilo estándar
    private Button createNavButton(String text, VaadinIcon icon, Class<? extends Component> navigationTarget) {
        Button button = new Button(text, icon.create());
        button.setHeight("100px");
        button.setWidth("200px");
        button.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        return button;
    }
}