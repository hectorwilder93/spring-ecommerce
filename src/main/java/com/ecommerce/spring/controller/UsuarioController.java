package com.ecommerce.spring.controller;

import com.ecommerce.spring.model.Orden;
import com.ecommerce.spring.model.Usuario;
import com.ecommerce.spring.service.IOrdenService;
import com.ecommerce.spring.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/registro")
    public String create(Model model){
        if (!model.containsAttribute("usuario")){
            model.addAttribute("usuario", new Usuario());
        }
        return "usuario/registro";
    }

    @PostMapping("/save")
    public String save(@Validated @ModelAttribute("usuario") Usuario usuario, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {
        logger.info("Usuario registro: {}", usuario);
        logger.info("Session attributes: {}", session.getAttributeNames());
        logger.info("idusuario session attribute: {}", session.getAttribute("idusuario"));

        if (result.hasErrors()) {

            redirectAttributes.addFlashAttribute("error", "Datos invalidos");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/usuario/registro";
        }
        //Verificar si el email ya existe
        Optional<Usuario> existingUser = usuarioService.findByEmail(usuario.getEmail());
        if (existingUser.isPresent()) {
            redirectAttributes.addFlashAttribute("Error", "El email ya esta registrado");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/usuario/registro";
        }

        usuario.setTipo("USER");
        Usuario usuarioGuardado = usuarioService.save(usuario, passwordEncoder);

        //Auto-login despues del registro
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            session.setAttribute("idusuario", usuarioGuardado.getId());
            session.setAttribute("usuario", usuarioGuardado.getNombre());
            session.setAttribute("tipoUsuario", usuarioGuardado.getTipo());

            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Bienvenido!");

            //Redirigir según tipo de usuario
            if ("ADMIN".equals(usuario.getTipo())) {
                return "redirect:/administrador";
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            logger.error("Error en auto-login despues de registro: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("succes", "Registro exitoso. Por favor inicie sesión");
            return "redirect://usuario/login";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(value="error", required = false)String error,
                        @RequestParam(value = "logout", required = false)String logout,
                        HttpSession session, Model model){

        // Mostrar mensajes flash de redirect
        if (session.getAttribute("error") != null) {
            model.addAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        if (session.getAttribute("success") != null) {
            model.addAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }

        //Mostrar mensajes de spring security
        if(error != null){
            model.addAttribute("error", "credenciales inválidas");
        }
        if (logout != null){
            model.addAttribute("success", "Sesión cerrada exitosamente");
        }

        return "usuario/login";
    }


    @GetMapping("/compras")
    public String obtenerCompras(Model model, HttpSession session){
        if (session.getAttribute("idusuario")== null){
            return "redirect:/usuario/login";
        }

        Usuario usuario = usuarioService.findById( Integer.parseInt(session.getAttribute("idusuario").toString()) ).get();
        List<Orden> ordenes= ordenService.findByUsuario(usuario);
        model.addAttribute("ordenes", ordenes);
        model.addAttribute("sesion", session.getAttribute("idusuario"));

        return "usuario/compras";
    }


    @GetMapping("/detalle/{id}")
    public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model){
        logger.info("Id de la orden: {}", id);
        if (session.getAttribute("idusuario")== null){
            return "redirect:/usuario/login";
        }

        Optional<Orden> orden = ordenService.findById(id);
        if (orden.isPresent()){
            model.addAttribute("detalles", orden.get().getDetalle());
            model.addAttribute("sesion", session.getAttribute("idusuario"));
            return "usuario/detalleCompra";
        } else {
            return "redirect:/usuario/compras";
        }
    }

    @GetMapping("/cerrar")
    public String cerrarSesion(HttpSession session){
        session.removeAttribute("idusuario");
        session.removeAttribute("usuario");
        session.removeAttribute("tipoUsuario");
        return "redirect:/";
    }
}
