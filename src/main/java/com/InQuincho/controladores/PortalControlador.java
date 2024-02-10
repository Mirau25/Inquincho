package com.InQuincho.controladores;

import com.InQuincho.entidades.Usuario;
import com.InQuincho.excepciones.MiException;
import com.InQuincho.repositorios.UsuarioRepositorio;
import com.InQuincho.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api")
public class PortalControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;
    private UsuarioRepositorio usuarioRepositorio;


    @GetMapping("/")
    public String index() {

         return "forward:/index.html";
    }

    @GetMapping("/registrar")
    public String registrar() {
        return "{\"message\": \"Registro exitoso\"}";
    }

    @PostMapping("/api/registro")
    public ResponseEntity<String> registro(@RequestParam String nombre, @RequestParam String email, @RequestParam String password,
            @RequestParam String password2, ModelMap modelo, MultipartFile archivo) {

        try {
            usuarioServicio.registrar(archivo, nombre, email, password, password2);

            return ResponseEntity.ok().body("Usuario registrado correctamente");
            
        } catch (MiException ex) {

             return ResponseEntity.badRequest().body(ex.getMessage());
        }
        
    }
    
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam(required = false) String error) {

        if (error != null) {
            return ResponseEntity.badRequest().body("Usuario o contraseña inválidos");
        }

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/inicio")
    public ResponseEntity<String> inicio(HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
      if (logueado != null && logueado.getRol().toString().equals("ADMIN")) {
            return ResponseEntity.ok().body("redirect:/admin/dashboard");
        }
        
         return ResponseEntity.ok().body("redirect:/inicio");
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    
    
    @GetMapping("/perfil")
    public ResponseEntity<Usuario> perfil(HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
         return ResponseEntity.ok().body(usuario);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/perfil/{id}")
    
    public ResponseEntity<String> actualizar(@RequestParam("archivo") MultipartFile archivo,
                                         @RequestParam("id") String id,
                                         @RequestParam("nombre") String nombre,
                                         @RequestParam("email") String email,
                                         @RequestParam("password") String password,
                                         @RequestParam("password2") String password2) {

        try {
            usuarioServicio.actualizar(archivo, id, nombre, email, password, password2);

            return ResponseEntity.ok().body("Usuario actualizado correctamente");

        } catch (MiException ex) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el usuario");
        }

    }
    
    
}
