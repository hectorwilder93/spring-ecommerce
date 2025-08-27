package com.ecommerce.spring.controller;

import com.ecommerce.spring.model.Producto;
import com.ecommerce.spring.model.Usuario;
import com.ecommerce.spring.service.IUsuarioService;
import com.ecommerce.spring.service.ProductoService;
import com.ecommerce.spring.service.UploadFileService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/productos")

public class ProductoController {
    private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private UploadFileService upload;

    @GetMapping("")
    public String show(Model model){
        model.addAttribute("productos", productoService.findAll());
        return "productos/show";
    }

    @GetMapping("/create")
    public String create(){
        return "productos/create";
    }

    @PostMapping("/save")
    /*public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
        LOGGER.info("Este es el objeto producto {}",producto);

        Usuario u= usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
        producto.setUsuario(u);
        //Lógica para subir la imagen
        if (producto.getId()==null){//cuando se crea un producto
            String nombreImagen=upload.saveImage(file);
            producto.setImagen(nombreImagen);
        }else{

        }

        productoService.save(producto);
        return "redirect:/productos";
    }*/
    public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session, RedirectAttributes redirectAttributes) throws IOException {
        LOGGER.info("Este es el objeto producto {}", producto);

        // Verificar si el usuario está en sesión
        Object idUsuarioObj = session.getAttribute("idusuario");
        if (idUsuarioObj == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para crear productos");
            return "redirect:/usuario/login"; // Redirigir al login
        }

        try {
            Integer idUsuario = Integer.parseInt(idUsuarioObj.toString());
            Optional<Usuario> usuarioOptional = usuarioService.findById(idUsuario);

            if (usuarioOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/usuario/login";
            }

            Usuario u = usuarioOptional.get();
            producto.setUsuario(u);

            // Lógica para subir la imagen
            if (producto.getId() == null) { // cuando se crea un producto
                String nombreImagen = upload.saveImage(file);
                producto.setImagen(nombreImagen);
            } else {
                // Lógica para actualización si es necesario
            }

            productoService.save(producto);
            return "redirect:/productos";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "ID de usuario inválido");
            return "redirect:/usuario/login";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit( @PathVariable Integer id, Model model){
        Producto producto = new Producto();
        Optional<Producto> optionalProducto=productoService.get(id);
        producto= optionalProducto.get();

        LOGGER.info("Producto buscado:{}", producto);
        model.addAttribute("producto", producto);

        return "productos/edit";
    }

    @PostMapping("/update")
    public String update(Producto producto, @RequestParam("img") MultipartFile file ) throws IOException {
        Producto p= new Producto();
        p=productoService.get(producto.getId()).get();

        if (file.isEmpty()) {// editamos el producto pero no cambiamos la imagen
            producto.setImagen(p.getImagen());
        }else {// cuando se edita también la imagen//eliminar cuando no sea la imagen por defecto
            if (!p.getImagen().equals("default.jpg")) {
                upload.deleteImage(p.getImagen());
            }
            String nombreImagen= upload.saveImage(file);
            producto.setImagen(nombreImagen);
        }
        producto.setUsuario(p.getUsuario());
        productoService.update(producto);
        return "redirect:/productos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        Producto p= new Producto();
        p=productoService.get(id).get();

        //Eliminar cuando no sea la imagen por defecto
        if (p.getImagen() !=null && !p.getImagen().equals("default.jpg")){
            upload.deleteImage(p.getImagen());
        }
        productoService.delete(id);
        return "redirect:/productos";
    }
}
