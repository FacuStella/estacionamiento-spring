package com.estacionamiento.controllers;

import com.estacionamiento.dto.AdministradorRequest;
import com.estacionamiento.models.Usuario;
import com.estacionamiento.services.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Usuarios administradores. Se identifican por su username. */
@RestController
@RequestMapping("/api/admin/administradores")
public class ControllerAdminAdministrador {

    private final UsuarioService usuarioService;

    public ControllerAdminAdministrador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Crea el administrador con una clave temporal que le llega por mail. */
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crearAdministrador(@Valid @RequestBody AdministradorRequest admin) {
        return usuarioService.crearAdministrador(admin);
    }

    @GetMapping("/mostrar/{username}")
    public Usuario obtenerAdministrador(@PathVariable String username) {
        return usuarioService.buscarAdministrador(username);
    }

    @GetMapping("/listar")
    public List<Usuario> listarAdministradores() {
        return usuarioService.listarAdministradores();
    }

    @PutMapping("/actualizar/{username}")
    public Usuario actualizarAdministrador(@PathVariable String username, @Valid @RequestBody AdministradorRequest admin) {
        return usuarioService.actualizarAdministrador(username, admin);
    }

    @DeleteMapping("/eliminar/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarAdministrador(@PathVariable String username, Authentication auth) {
        usuarioService.eliminarAdministrador(username, auth.getName());
    }
}
