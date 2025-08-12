package com.ecommerce.spring.repository;

import com.ecommerce.spring.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductoRepository extends JpaRepository <Producto, Integer>{
}
