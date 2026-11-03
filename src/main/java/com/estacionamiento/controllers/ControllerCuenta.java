package com.estacionamiento.controllers;

import com.estacionamiento.dto.CambioClaveRequest;
import com.estacionamiento.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * La cuenta del usuario logueado, sea cual sea su rol. Es lo unico que puede usar
 * un usuario con clave temporal (ver JwtAuthFilter).
 */
@RestController
@RequestMapping("/api/cuenta")
public class ControllerCuenta {

    private final UsuarioService usuarioService;

    public ControllerCuenta(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cambiarClave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarClave(@Valid @RequestBody CambioClaveRequest r, Authentication auth) {
        usuarioService.cambiarClavePropia(auth.getName(), r.actual(), r.nueva());
    }
}
