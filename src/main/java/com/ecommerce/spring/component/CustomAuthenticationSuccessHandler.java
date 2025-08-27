package com.ecommerce.spring.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private SessionUtils sessionUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        sessionUtils.setSessionAttributes(authentication, session);

        //Redirigir según el rol
        Collection<? extends GrantedAuthority> authorities=authentication.getAuthorities();
        boolean isAdmin = authorities.stream().anyMatch(aut -> aut.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin){
            response.sendRedirect("/administrador");
        }else {
            response.sendRedirect("/");
        }
    }
}
