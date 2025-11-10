package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.model.Usuario;
import com.mmhfgroup.proyectointegrador.security.SecurityService;
import com.mmhfgroup.proyectointegrador.service.NotificacionService;
import com.mmhfgroup.proyectointegrador.view.util.ViewModeUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
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

public class EstudianteLayout extends AppLayout {

    private final SecurityService securityService;
    private final NotificacionService notificacionService;
    private final Usuario usuarioActual;

    @Autowired
    public EstudianteLayout(SecurityService securityService, NotificacionService notificacionService) {
        this.securityService = securityService;
        this.notificacionService = notificacionService;
        this.usuarioActual = securityService.getAuthenticatedUser();

        setDrawerOpened(false); // <--- 3) PANEL LATERAL CERRADO

        createHeader();
        createDrawer();
    }

    // ... (createHeader, createDrawer, y createTab se mantienen SIN CAMBIOS) ...

    private void createHeader() {
        H1 logo = new H1("Plataforma de cursada");
        logo.addClassNames("text-l", "m-m");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

        if (ViewModeUtil.isViewingAsStudent()) { // <--- Lógica de ViewModeUtil
            Button returnButton = new Button("Volver a mi vista", e -> {
                ViewModeUtil.disableViewAsStudent();
                ViewModeUtil.goToHomeForCurrentRole();
            });
            returnButton.setIcon(VaadinIcon.ARROW_BACKWARD.create());
            header.add(returnButton);
        }

        Avatar avatar = new Avatar(usuarioActual.getNombre());
        avatar.getElement().setAttribute("title", usuarioActual.getEmail());
        Span nombreUsuario = new Span("Hola, " + usuarioActual.getNombre());
        Button logoutButton = new Button("Cerrar Sesión", e -> securityService.logout());
        logoutButton.setIcon(VaadinIcon.SIGN_OUT.create());
        header.add(nombreUsuario, avatar, logoutButton);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);
    }

    private void createDrawer() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        Tab notificacionesTab = createTab(VaadinIcon.BELL, "Notificaciones", NotificacionesView.class);
        long nuevas = notificacionService.contarNuevasPorUsuario(usuarioActual);
        if (nuevas > 0) {
            Span badge = new Span(String.valueOf(nuevas));
            badge.getElement().getThemeList().add("badge contrast primary");
            badge.getStyle().set("margin-inline-start", "auto");
            notificacionesTab.add(badge);
        }
        tabs.add(
                createTab(VaadinIcon.HOME, "Inicio", MainView.class),
                createTab(VaadinIcon.ARCHIVE, "Entregas", EntregasView.class),
                createTab(VaadinIcon.GROUP, "Mi Equipo", MiEquipoView.class),
                createTab(VaadinIcon.CALENDAR, "Calendario", CalendarioView.class),
                createTab(VaadinIcon.COMMENTS, "Foro", ForoView.class),
                notificacionesTab
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