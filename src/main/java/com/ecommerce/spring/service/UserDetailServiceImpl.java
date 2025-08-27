package com.ecommerce.spring.service;

import com.ecommerce.spring.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder bCrypt;

    @Autowired
    private HttpSession session;

    private Logger log= LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> optionalUser=usuarioService.findByEmail(username);

        if(optionalUser.isPresent()){
            Usuario usuario=optionalUser.get();
            log.info("Usuario encontrado: {}", usuario.getEmail());
            log.info("Password en BD: {}", usuario.getPassword());
            log.info("Tipo/Rol: {}", usuario.getTipo());

            return usuario;
        }else{
            log.error("Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + username);
        }
    }
}
