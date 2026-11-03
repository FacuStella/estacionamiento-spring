package com.estacionamiento.services;

import com.estacionamiento.dto.LoginRequest;
import com.estacionamiento.dto.LoginResponse;
import com.estacionamiento.models.Usuario;
import com.estacionamiento.repositorys.UsuarioRepository;
import com.estacionamiento.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!usuarioService.coincidePassword(usuario, request.password())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        String token = jwtService.generarToken(usuario.getUsername(), usuario.getTipoUsuario());
        return new LoginResponse(token, usuario.getTipoUsuario().name(), usuario.getUsername(), usuario.isDebeCambiarClave());
    }
}
