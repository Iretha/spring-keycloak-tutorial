package com.smdev.demo.frontend.controller;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.IDToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Handles home requests and adds name of the user to the model, so that we can display it on the home page
     *
     * @param model
     * @return
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        String username = getUsername("Guest");

        model.addAttribute("name", username);
        return "/home";
    }

    /**
     * Retrieves the username of the user
     *
     * @param defaultUsername
     * @return
     */
    public String getUsername(String defaultUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == null) {
            return defaultUsername;
        }

        if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal userDetails = (KeycloakPrincipal) authentication.getPrincipal();
            IDToken idToken = userDetails.getKeycloakSecurityContext().getIdToken();
            return idToken != null ? idToken.getPreferredUsername() : idToken.getGivenName();
        } else if (authentication.getPrincipal() instanceof String) {
            return String.class.cast(authentication.getPrincipal());
        }
        return defaultUsername;
    }
}
