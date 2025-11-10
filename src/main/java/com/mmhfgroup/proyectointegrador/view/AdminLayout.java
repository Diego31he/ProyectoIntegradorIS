package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.view.util.ViewModeUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminLayout extends AppLayout {

    private final SecurityService securityService;

    @Autowired
    public AdminLayout(SecurityService securityService) {
        this.securityService = securityService;
        setDrawerOpened(false);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Panel de Administración");
        logo.addClassNames("text-l", "m-m");

        Button viewAsCatedra = new Button("Ver como Cátedra", e -> {
            ViewModeUtil.enableViewAsStudent();
            com.vaadin.flow.component.UI.getCurrent().navigate("catedra");
        });
        viewAsCatedra.setIcon(VaadinIcon.ACADEMY_CAP.create());

        Button viewAsEstudiante = new Button("Ver como Estudiante", e -> {
            ViewModeUtil.enableViewAsStudent();
            com.vaadin.flow.component.UI.getCurrent().navigate("");
        });
        viewAsEstudiante.setIcon(VaadinIcon.USER.create());

        Button logoutButton = new Button("Cerrar Sesión", e -> securityService.logout());
        logoutButton.setIcon(VaadinIcon.SIGN_OUT.create());

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(), logo, viewAsCatedra, viewAsEstudiante, logoutButton
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);
    }

    private void createDrawer() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(
                createTab(VaadinIcon.DASHBOARD, "Inicio", AdminDashboardView.class),
                createTab(VaadinIcon.USER_CHECK, "Gestionar Usuarios", AdminUsuariosView.class),
                createTab(VaadinIcon.UPLOAD, "Importar Datos", AdminImportView.class),

                // --- INICIO DE CORRECCIÓN (Petición 3) ---
                createTab(VaadinIcon.USERS, "Participantes", ParticipantesAdminView.class) // <-- AÑADIDO
                // --- FIN DE CORRECCIÓN ---
        );
        VerticalLayout drawerLayout = new VerticalLayout(tabs);
        addToDrawer(drawerLayout);
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget);
        Icon icon = viewIcon.create();
        Span text = new Span(viewName);
        link.add(icon, text);
        link.getStyle().set("display", "flex").set("align-items", "center").set("gap", "10px");
        Tab tab = new Tab(link);
        return tab;
    }
}