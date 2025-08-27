package com.ecommerce.spring.component;

import com.ecommerce.spring.model.Usuario;
import com.ecommerce.spring.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionUtils {

    @Autowired
    private IUsuarioService usuarioService;
    public void setSessionAttributes(Authentication authentication, HttpSession session){
        if (authentication !=null && authentication.isAuthenticated()){
            String username = authentication.getName();
            Optional<Usuario> usuarioOptional = usuarioService.findByEmail(username);

            if (usuarioOptional.isPresent()){
                Usuario usuario = usuarioOptional.get();
                session.setAttribute("idusuario", usuario.getId());
                session.setAttribute("usuario", usuario.getNombre());
                session.setAttribute("tipoUsuario", usuario.getTipo());

                //Log para debugging
                System.out.println("Session atributes set for user: " + usuario.getEmail());
                System.out.println("User ID: " + usuario.getId());
                System.out.println("User type: " + usuario.getTipo());
            }
        }
    }
}
