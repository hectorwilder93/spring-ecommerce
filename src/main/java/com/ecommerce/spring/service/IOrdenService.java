package com.ecommerce.spring.service;

import com.ecommerce.spring.model.Orden;
import com.ecommerce.spring.model.Usuario;

import java.util.List;

public interface IOrdenService {
    List<Orden> findAll();
    Orden save(Orden orden);
    String generarNumeroOrden();
    List<Orden> findByUsuario(Usuario usuario);
}
