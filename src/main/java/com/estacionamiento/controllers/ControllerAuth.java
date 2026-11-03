package com.estacionamiento.controllers;

import com.estacionamiento.dto.LoginRequest;
import com.estacionamiento.dto.LoginResponse;
import com.estacionamiento.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ControllerAuth {

    private final AuthService authService;

    public ControllerAuth(AuthService authService) {
        this.authService = authService;
    }

    /** Devuelve un token JWT para usar en el header "Authorization: Bearer <token>". */
    @PostMapping("/autenticar")
    public LoginResponse autenticar(@Valid @RequestBody LoginRequest request) {
        return authService.autenticar(request);
    }
}
