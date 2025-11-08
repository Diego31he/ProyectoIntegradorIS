package com.mmhfgroup.proyectointegrador.view;

import com.mmhfgroup.proyectointegrador.view.util.ViewModeUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.spring.security.AuthenticationContext;

public class EstudianteLayout extends AppLayout {

    private AuthenticationContext auth;

    public EstudianteLayout(AuthenticationContext auth) {
        this.auth = auth;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        Image logo = new Image("images/mmhf_logo.png", "MMHF Logo");
        logo.setHeight("120px");
        logo.getStyle()
                .set("border-radius", "5px")
                .set("object-fit", "cover")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)");

        H2 titulo = new H2("Proyecto Integrador");
        titulo.getStyle().set("margin", "0").set("font-weight", "700").set("color", "white");

        Span subtitulo = new Span("MMHF Group — Ingeniería de Software");
        subtitulo.getStyle().set("font-size", "13px").set("color", "rgba(255,255,255,0.85)");

        VerticalLayout textos = new VerticalLayout(titulo, subtitulo);
        textos.setPadding(false);
        textos.setSpacing(false);
        textos.setAlignItems(Alignment.START);

        // Botón "Volver a mi vista" (solo visible si está en "ver como estudiante")
        Button backToMyView = new Button("Volver a mi vista", e -> {
            ViewModeUtil.disableViewAsStudent();
            ViewModeUtil.goToHomeForCurrentRole();
        });
        backToMyView.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backToMyView.setVisible(ViewModeUtil.isViewingAsStudent());

        // refrescar visibilidad cuando se navega dentro del layout
        getUI().ifPresent(ui -> ui.addBeforeEnterListener(ev ->
                backToMyView.setVisible(ViewModeUtil.isViewingAsStudent())
        ));

        Button logout = new Button("Salir", e -> auth.logout()); // <--- CAMBIO
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        logout.getStyle().set("color", "white");
        logout.setPrefixComponent(new Icon(VaadinIcon.SIGN_OUT));

        HorizontalLayout header = new HorizontalLayout(toggle, logo, textos, backToMyView, logout);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(textos);
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.getStyle()
                .set("background", "linear-gradient(90deg, #1E88E5, #42A5F5)")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)");

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();
        menu.addClassNames(LumoUtility.Padding.MEDIUM);
        menu.setSizeFull();

        VerticalLayout navLinks = new VerticalLayout(
                new RouterLink("Inicio", MainView.class),
                new RouterLink("Entregas", EntregasView.class),
                new RouterLink("Notificaciones", NotificacionesView.class),
                new RouterLink("Calendario", CalendarioView.class),
                new RouterLink("Mensajeria", ForoView.class)
        );
        navLinks.setPadding(false);
        navLinks.setSpacing(false);

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        Details nosotrosDetails = createNosotrosDetails();

        menu.add(navLinks, spacer, nosotrosDetails);
        addToDrawer(menu);
    }

    private Details createNosotrosDetails() {
        HorizontalLayout socialIconsLayout = new HorizontalLayout();
        socialIconsLayout.setWidthFull();
        socialIconsLayout.setJustifyContentMode(JustifyContentMode.EVENLY);
        socialIconsLayout.add(
                createSocialLink("Instagram", new Icon(VaadinIcon.CAMERA), "https://www.instagram.com/"),
                createSocialLink("X", new Icon(VaadinIcon.TWITTER), "https://www.x.com/"),
                createSocialLink("Web", new Icon(VaadinIcon.GLOBE), "http://mmhfgroup.com.ar")
        );

        VerticalLayout detailsContent = new VerticalLayout(socialIconsLayout);
        detailsContent.setAlignItems(Alignment.CENTER);
        detailsContent.setSpacing(true);
        detailsContent.setPadding(false);
        detailsContent.getStyle().set("padding-top", "var(--lumo-space-s)");

        Span nosotrosSummary = new Span("Nosotros");
        nosotrosSummary.getStyle().set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "500")
                .set("color", "var(--lumo-body-text-color)");

        Details nosotrosDetails = new Details(nosotrosSummary, detailsContent);
        nosotrosDetails.setWidthFull();
        return nosotrosDetails;
    }

    private VerticalLayout createSocialLink(String label, Icon icon, String url) {
        Anchor link = new Anchor(url, icon);
        link.setTarget("_blank");
        link.setTitle(label);

        Span text = new Span(label);
        text.getStyle().set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout column = new VerticalLayout(text, link);
        column.setAlignItems(Alignment.CENTER);
        column.setPadding(false);
        column.setSpacing(false);
        return column;
    }
}
