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
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"ROLE_CATEDRA","ROLE_ADMIN"})
public class CatedraLayout extends AppLayout {

    public CatedraLayout() {
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

        Button logout = new Button("Salir", e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/login")));
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        logout.getStyle().set("color", "white");

        HorizontalLayout header = new HorizontalLayout(toggle, logo, textos, logout);
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

        // --- Navegación Cátedra ---
                menu.add(
                        new RouterLink("Equipos", EquiposView.class),
                        new RouterLink("Entregas (Cátedra)", CatedraEntregasView.class), // <-- aquí
                        new RouterLink("Calendario", CalendarioView.class),
                        new RouterLink("Foro", ForoView.class),
                        new RouterLink("Notificaciones", NotificacionesView.class)
                );

        // Botón "Ver como Estudiante"
        Button verComoEst = new Button("Ver como Estudiante", e -> {
            ViewModeUtil.enableViewAsStudent();
            UI.getCurrent().navigate(""); // MainView estudiantes
        });
        verComoEst.setPrefixComponent(new Icon(VaadinIcon.EYE));
        verComoEst.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_CONTRAST);

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        Details nosotrosDetails = createNosotrosDetails();

        menu.add(verComoEst, spacer, nosotrosDetails);
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
        Anchor webLinkText = new Anchor("http://mmhfgroup.com.ar", "mmhfgroup.com.ar");
        webLinkText.setTarget("_blank");
        webLinkText.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-contrast-70pct)");

        VerticalLayout detailsContent = new VerticalLayout(socialIconsLayout, webLinkText);
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
