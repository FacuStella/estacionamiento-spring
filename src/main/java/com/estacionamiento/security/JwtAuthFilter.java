package com.estacionamiento.security;

import com.estacionamiento.services.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lee el header "Authorization: Bearer ...", valida el token y deja al usuario autenticado.
 * El usuario y su rol se consultan en la base en cada request, así que si se elimina un
 * usuario o cambia su rol, el efecto es inmediato aunque su token no haya vencido.
 * Un usuario con clave temporal solo puede usar /api/cuenta (para cambiarla): el resto responde 403.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtAuthFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<String> username = jwtService.extraerUsername(header.substring(7));
            if (username.isPresent()) {
                try {
                    UserDetails usuario = usuarioService.loadUserByUsername(username.get());
                    if (!usuario.isCredentialsNonExpired() && !request.getRequestURI().startsWith("/api/cuenta/")) {
                        // Tiene una clave temporal: hasta que la cambie, solo puede usar /api/cuenta.
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"status\":403,\"mensaje\":\"Tenés que cambiar tu contraseña antes de seguir\",\"detalles\":[]}");
                        return;
                    }
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            usuario, null, usuario.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (UsernameNotFoundException e) {
                    // El usuario ya no existe: se sigue sin autenticar y Spring Security responde 401.
                }
            }
        }
        chain.doFilter(request, response);
    }
}
