package com.mmhfgroup.proyectointegrador.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2; // <-- El import que agregamos
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | Proyecto Integrador")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        // --- El texto que agregamos ---
        H2 tituloFasta = new H2("UNIVERSIDAD FASTA FACULTAD INGENIERÍA"+"\n"+"SIUF Web");
        tituloFasta.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-weight", "600")
                .set("margin-bottom", "0px");

        H1 tituloPrincipal = new H1("Proyecto Integrador - MMHF");
        tituloPrincipal.getStyle().set("margin-top", "0px");

        add(tituloFasta, tituloPrincipal, login);
    }

    // --- INICIO DE LA CORRECCIÓN ---
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // Muestra un error si el login falla (ej. mal usuario/pass)
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters() // <-- Esta era la línea que faltaba
                .containsKey("error")) {
            login.setError(true);
        }
    }
    // --- FIN DE LA CORRECCIÓN ---
}