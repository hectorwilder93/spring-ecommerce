package com.ecommerce.spring.service;

import ch.qos.logback.classic.Logger;
import com.ecommerce.spring.model.Usuario;
import com.ecommerce.spring.repository.IUsuarioRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService{

    private static final Logger logger = (Logger) LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario, BCryptPasswordEncoder passwordEncoder) {
        logger.info("Guardando usuario: {}", usuario.getEmail());

        //Encriptar la contraseña antes de guardar
        if (usuario.getPassword() !=null){
            String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(passwordEncriptada);
            logger.info("Password encriptada para usuario: {}", usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public void updatePassword(Integer id, String passwordEncriptada) {
        logger.info("Actualizando password para usuario ID: {}",id);

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()){
            Usuario usuario = usuarioOptional.get();
            usuario.setPassword(passwordEncriptada);
            usuarioRepository.save(usuario);
            logger.info("Password actualizad para usuario: {}", usuario.getEmail());
        } else {
            logger.warn("Usuario con ID {} no encontrado para actualizar password", id);
        }
    }
}

