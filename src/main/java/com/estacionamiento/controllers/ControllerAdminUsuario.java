package com.estacionamiento.controllers;

import com.estacionamiento.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Operaciones sobre cualquier usuario (socio, empleado o administrador). */
@RestController
@RequestMapping("/api/admin/usuarios")
public class ControllerAdminUsuario {

    private final UsuarioService usuarioService;

    public ControllerAdminUsuario(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Le manda al usuario una clave temporal nueva por mail; tiene que cambiarla al ingresar. */
    @PostMapping("/restablecerClave/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecerClave(@PathVariable String username) {
        usuarioService.restablecerClave(username);
    }
}
