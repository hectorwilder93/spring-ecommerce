package com.ecommerce.spring.service;

import com.ecommerce.spring.model.Orden;
import com.ecommerce.spring.repository.IOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenServiceImpl implements IOrdenService {

    @Autowired
    private IOrdenRepository ordenRepository;
    @Override
    public Orden save(Orden orden) {
        return ordenRepository.save(orden);
    }
}
