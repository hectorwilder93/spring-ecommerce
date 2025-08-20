package com.ecommerce.spring.controller;

import com.ecommerce.spring.model.Orden;
import com.ecommerce.spring.model.Usuario;
import com.ecommerce.spring.service.IOrdenService;
import com.ecommerce.spring.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    private final Logger logger= LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IOrdenService ordenService;

    @GetMapping("/registro")
    public String create(){
        return "usuario/registro";
    }

    @PostMapping("/save")
    public String save(Usuario usuario){
        logger.info("Usuario registro: {}", usuario);
         usuario.setTipo("USER");
         usuarioService.save(usuario);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "usuario/login";
    }
    /*public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOptional = usuarioService.findByEmail(email);

        if (usuarioOptional.isPresent() && usuarioOptional.get().getPassword().equals(password)) {
            session.setAttribute("idusuario", usuarioOptional.get().getId());
            return "redirect:/productos";
        } else {
            redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas");
            return "redirect:/login";
        }
    }*/

    @PostMapping("/acceder")
    public String acceder(@Validated Usuario usuario, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes){
        logger.info("Accesos : {}", usuario);
        if (result.hasErrors()){
            redirectAttributes.addFlashAttribute("error","Datos inválidos");
            return "redirect:/usuario/login";
        }

        Optional<Usuario> user=usuarioService.findByEmail(usuario.getEmail());

        if(user.isPresent() && user.get().getPassword().equals(usuario.getPassword())){
            session.setAttribute("idusuario", user.get().getId());
            session.setAttribute("usuario", user.get().getNombre());
            if (user.get().getTipo().equals("ADMIN")){
                return "redirect:/administrador";
            }else {
                return "redirect:/";
            }
        }else {
            logger.info("Credenciales inválidas");
            redirectAttributes.addFlashAttribute("Error", "Email o contraseña incorrectos");
            return "redirect:/";
        }
    }

    @GetMapping("/compras")
    public String obtenerCompras(Model model, HttpSession session){
        model.addAttribute("sesion", session.getAttribute("idusuario"));
        Usuario usuario;
        usuario = usuarioService.findById( Integer.parseInt(session.getAttribute("idusuario").toString()) ).get();
        List<Orden> ordenes= ordenService.findByUsuario(usuario);
        model.addAttribute("ordenes", ordenes);

        return "usuario/compras";
    }


    @GetMapping("/detalle/{id}")
    public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model){
        logger.info("Id de la orden: {}", id);
        Optional<Orden> orden = ordenService.findById(id);

        model.addAttribute("detalles", orden.get().getDetalle());

        //session
        model.addAttribute("sesion", session.getAttribute("idusuario"));

        return"usuario/detallecompra";
    }

    @GetMapping("/cerrar")
    public String cerrarSesion(HttpSession session){
        session.removeAttribute("idusuario");
        return "redirect:/";
    }
}
