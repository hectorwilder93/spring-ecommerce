package com.ecommerce.spring.repository;

import com.ecommerce.spring.model.Orden;
import com.ecommerce.spring.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {
    List<Orden> findByUsuario(Usuario usuario);
}
