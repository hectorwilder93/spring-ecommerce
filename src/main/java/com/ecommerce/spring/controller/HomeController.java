package com.ecommerce.spring.controller;


import com.ecommerce.spring.model.DetalleOrden;
import com.ecommerce.spring.model.Orden;
import com.ecommerce.spring.model.Producto;
import com.ecommerce.spring.model.Usuario;
import com.ecommerce.spring.service.IDetalleOrdenService;
import com.ecommerce.spring.service.IOrdenService;
import com.ecommerce.spring.service.IUsuarioService;
import com.ecommerce.spring.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {
    private final Logger log=LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IOrdenService ordenService;

    @Autowired
    private IDetalleOrdenService detalleOrdenService;

    //Almacena los detalles de la orden
    List<DetalleOrden> detalles= new ArrayList<DetalleOrden>();

    //Datos de la orden
    Orden orden = new Orden();


    @GetMapping("")
    public String home(Model model, HttpSession session){
        log.info("Sesión de usuario: {}", session.getAttribute("idusuario"));
        model.addAttribute("productos", productoService.findAll());

        //session
        model.addAttribute("sesion", session.getAttribute("idusuario"));
        return "usuario/home";
    }

    @GetMapping("productohome/{id}")
    public String productoHome(@PathVariable Integer id, Model model){
        log.info("Id producto enviado como parámetro {}",id);
        Producto producto = new Producto();
        Optional<Producto> productoOptional = productoService.get(id);
        producto = productoOptional.get();
        model.addAttribute("producto", producto);

        return "usuario/productohome";
    }

    @PostMapping("/cart")
    public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model){
        DetalleOrden detalleOrden= new DetalleOrden();
        Producto producto= new Producto();

        Optional<Producto> optionalProducto = productoService.get(id);
        log.info("Producto añadido: {}", optionalProducto.get());
        log.info("Cantidad: {}", cantidad);
        producto=optionalProducto.get();

        detalleOrden.setCantidad(cantidad);
        detalleOrden.setPrecio(producto.getPrecio());
        detalleOrden.setNombre(producto.getNombre());
        detalleOrden.setTotal(producto.getPrecio()* cantidad);
        detalleOrden.setProducto(producto);

        //Validación para que le producto no se agregue dos veces
        Integer idProducto = producto.getId();
        boolean ingresado = detalles.stream().anyMatch(p ->  p.getProducto().getId()==idProducto);

        if (!ingresado){
            detalles.add(detalleOrden);
        }

        double sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

        orden.setTotal(sumaTotal);
        model.addAttribute("cart", detalles);
        model.addAttribute("orden", orden);

        return "usuario/carrito";
    }
    //Quitar el producto del carro de compras
    @GetMapping("/delete/cart/{id}")
    public String deleteProductCart(@PathVariable Integer id, Model model){

        //Lista de productos nuevos
        List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();
        for (DetalleOrden detalleOrden: detalles){
            if (detalleOrden.getProducto().getId()!=id){
                ordenesNueva.add(detalleOrden);
            }
        }

        //Nos deja la nueva lista con los productos restantes
        detalles=ordenesNueva;

        double sumaTotal=0;
        sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

        orden.setTotal(sumaTotal);
        model.addAttribute("cart", detalles);
        model.addAttribute("orden", orden);

        return "usuario/carrito";
    }

    @GetMapping("/getCart")
    public String getCart(Model model, HttpSession session){
        model.addAttribute("cart", detalles);
        model.addAttribute("orden", orden);

        //session
        model.addAttribute("sesion", session.getAttribute("idusuario"));
        return "/usuario/carrito";
    }

    @GetMapping("/order")
    public String order(Model model, HttpSession session){
        Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();

        model.addAttribute("cart", detalles);
        model.addAttribute("orden", orden);
        model.addAttribute("usuario", usuario);

        return "usuario/resumenorden";
    }

    //Guardar la orden
    @GetMapping("/saveOrder")
    public String saveOrder (HttpSession session){
        Date fechaCreacion = new Date();
        orden.setFechaCreacion(fechaCreacion);
        orden.setNumero(ordenService.generarNumeroOrden());

        //Usuario
        Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
        orden.setUsuario(usuario);
        ordenService.save(orden);

        //Guardar detalles
        for (DetalleOrden dt:detalles){
            dt.setOrden(orden);
            detalleOrdenService.save(dt);
        }

        //Restablecer los valores de la lista y orden
        orden = new Orden();
        detalles.clear();
        return "redirect:/";
    }

    @PostMapping("/search")
    public String searchProduct(@RequestParam String nombre, Model model){
        log.info("Nombre del producto: {}", nombre);
        List<Producto> productos= productoService.findAll().stream().filter(p -> p.getNombre().contains(nombre)).toList();
        model.addAttribute("productos", productos);

        return "usuario/home";
    }
}
