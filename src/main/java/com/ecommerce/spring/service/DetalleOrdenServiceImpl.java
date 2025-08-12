package com.ecommerce.spring.service;

import com.ecommerce.spring.model.DetalleOrden;
import com.ecommerce.spring.repository.IDetalleOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleOrdenServiceImpl implements IDetalleOrdenService {

    @Autowired
    private IDetalleOrdenRepository detalleOrdenRepository;

    @Override
    public DetalleOrden save(DetalleOrden detalleOrden){
        return detalleOrdenRepository.save(detalleOrden);
    }
}
