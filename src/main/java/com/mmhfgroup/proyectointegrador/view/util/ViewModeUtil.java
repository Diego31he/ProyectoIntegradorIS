package com.mmhfgroup.proyectointegrador.view.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

public final class ViewModeUtil {
    private static final String ATTR = "viewAsStudent";

    private ViewModeUtil() {}

    public static void enableViewAsStudent() {
        VaadinSession s = VaadinSession.getCurrent();
        if (s != null) {
            s.setAttribute(ATTR, Boolean.TRUE);
        }
    }

    public static void disableViewAsStudent() {
        VaadinSession s = VaadinSession.getCurrent();
        if (s != null) {
            // remove: setAttribute con null elimina el atributo
            s.setAttribute(ATTR, null);
        }
    }

    public static boolean isViewingAsStudent() {
        VaadinSession s = VaadinSession.getCurrent();
        Object v = (s != null) ? s.getAttribute(ATTR) : null;
        return v instanceof Boolean b && b;
    }

    /** Navega al home real según el rol actual. */
    public static void goToHomeForCurrentRole() {
        UI ui = UI.getCurrent();
        if (ui == null) return;

        Set<String> roles = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_ADMIN")) {
            ui.navigate("admin/dashboard");
        } else if (roles.contains("ROLE_CATEDRA")) {
            ui.navigate("catedra");
        } else {
            ui.navigate("");
        }
    }
}
