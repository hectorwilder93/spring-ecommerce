package com.ecommerce.spring.service;

import com.ecommerce.spring.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Integer id);
    Usuario save(Usuario usuario, BCryptPasswordEncoder passwordEncoder);
    Optional<Usuario> findByEmail(String email);
    void updatePassword(Integer id, String passwordEncriptada);
}
