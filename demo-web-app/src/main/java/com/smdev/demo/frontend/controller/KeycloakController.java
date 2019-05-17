package com.smdev.demo.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class KeycloakController {

    @Value("${logout-url}")
    private String SERVER_LOGOUT_URL;

    @RequestMapping("/logout-from-keycloak")
    public RedirectView logoutFromKeycloak() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(SERVER_LOGOUT_URL);
        return redirectView;
    }
}
