package com.ecommerce.spring.component;

import com.ecommerce.spring.model.Usuario;
import com.ecommerce.spring.service.IUsuarioService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordMigrator {
    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrator.class);


    private IUsuarioService usuarioService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public void setUsuarioService(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Autowired
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void migratePassword(){
        try {
            logger.info("Iniciando migración de contraseñas...");

            List<Usuario> usuarios = usuarioService.findAll();
            logger.info("Encontrados {} usuarios para migrar", usuarios.size());

            for (Usuario usuario : usuarios){
                //Si la password no empieza con $2$ (formato BCrypt), esta en texto plano
                if (usuario.getPassword() !=null && !usuario.getPassword().startsWith("$2a$")){

                    String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
                    usuarioService.updatePassword(usuario.getId(), passwordEncriptada);
                    logger.info("Password migrada para usuario: " + usuario.getEmail());
                } else {
                    logger.info("Password ya encriptada para usuario: {}", usuario.getEmail());
                }
            }
            logger.info("Migración de contraseñas completada");
        } catch (Exception e){
            logger.error("Error en migración de password: {} ", e.getMessage(), e);
        }
    }
}
