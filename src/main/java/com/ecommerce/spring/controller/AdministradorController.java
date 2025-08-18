package com.ecommerce.spring.controller;


import com.ecommerce.spring.model.Producto;
import com.ecommerce.spring.service.IOrdenService;
import com.ecommerce.spring.service.IUsuarioService;
import com.ecommerce.spring.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IOrdenService ordenService;

    @GetMapping("")
    public String home(Model model){
        List<Producto> productos;
        productos = productoService.findAll();
        model.addAttribute("productos",
                productos);

        return "administrador/home";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model){
        model.addAttribute("usuarios", usuarioService.findAll());
        return "administrador/usuarios";
    }

    @GetMapping("/ordenes")
    public String ordenes(Model model){
        model.addAttribute("Ordenes", ordenService.findAll());
        return "administrador/ordenes";
    }
}
