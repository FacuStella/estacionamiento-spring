package com.estacionamiento.services;

import com.estacionamiento.TipoUsuario;
import com.estacionamiento.dto.AdministradorRequest;
import com.estacionamiento.exceptions.ConflictoException;
import com.estacionamiento.exceptions.NoEncontradoException;
import com.estacionamiento.exceptions.SolicitudInvalidaException;
import com.estacionamiento.models.Usuario;
import com.estacionamiento.repositorys.UsuarioRepository;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private static final int LARGO_MINIMO_PASSWORD = 6;

    /** Sin letras ni numeros que se confundan (0/O, 1/l/I). */
    private static final String CARACTERES_CLAVE = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int LARGO_CLAVE_TEMPORAL = 10;
    private static final SecureRandom AZAR = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    /**
     * Si el usuario tiene una clave temporal, sus credenciales figuran como vencidas:
     * JwtAuthFilter solo lo deja usar /api/cuenta hasta que la cambie.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return org.springframework.security.core.userdetails.User.withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getTipoUsuario().name())
                .credentialsExpired(u.isDebeCambiarClave())
                .build();
    }

    /**
     * Arma un Usuario nuevo (sin guardarlo) con una clave temporal que se le manda por mail.
     * En su primer login tiene que cambiarla. Lo guarda en cascada la entidad que lo contiene (Socio / Empleado).
     */
    public Usuario nuevoUsuario(String username, String mail, TipoUsuario tipo) {
        if (username == null || username.isBlank()) {
            throw new SolicitudInvalidaException("El username es obligatorio");
        }
        validarMail(mail);
        if (usuarioRepository.existsByUsername(username)) {
            throw new ConflictoException("Ya existe un usuario con username '" + username + "'");
        }
        String clave = claveTemporal();
        Usuario usuario = new Usuario(username, passwordEncoder.encode(clave), tipo);
        usuario.setMail(mail.trim());
        usuario.setDebeCambiarClave(true);
        mailService.enviarClaveTemporal(usuario, clave);
        return usuario;
    }

    /** Usuario con una clave conocida (el administrador inicial, que sale de application.properties). */
    public Usuario usuarioConClave(String username, String password, TipoUsuario tipo, String mail, boolean debeCambiarClave) {
        validarPassword(password);
        Usuario usuario = new Usuario(username, passwordEncoder.encode(password), tipo);
        usuario.setMail(mail == null || mail.isBlank() ? null : mail.trim());
        usuario.setDebeCambiarClave(debeCambiarClave);
        return usuario;
    }

    public void cambiarMail(Usuario usuario, String mail) {
        validarMail(mail);
        usuario.setMail(mail.trim());
    }

    public boolean coincidePassword(Usuario usuario, String passwordIngresada) {
        return passwordEncoder.matches(passwordIngresada, usuario.getPassword());
    }

    // ---------- el propio usuario ----------

    /** Cambio de clave del usuario logueado. Si tenia una clave temporal, deja de tenerla. */
    @Transactional
    public void cambiarClavePropia(String username, String actual, String nueva) {
        Usuario usuario = buscarPorUsername(username);
        if (!coincidePassword(usuario, actual)) {
            throw new SolicitudInvalidaException("La contraseña actual no es correcta");
        }
        validarPassword(nueva);
        if (nueva.equals(actual)) {
            throw new SolicitudInvalidaException("La contraseña nueva tiene que ser distinta de la actual");
        }
        usuario.setPassword(passwordEncoder.encode(nueva));
        usuario.setDebeCambiarClave(false);
    }

    // ---------- lo que hace el administrador ----------

    /** Genera una clave temporal nueva, se la manda por mail y obliga a cambiarla en el proximo login. */
    @Transactional
    public void restablecerClave(String username) {
        Usuario usuario = buscarPorUsername(username);
        if (usuario.getMail() == null || usuario.getMail().isBlank()) {
            throw new SolicitudInvalidaException("El usuario " + username + " no tiene mail: cargalo antes de restablecer la clave");
        }
        String clave = claveTemporal();
        usuario.setPassword(passwordEncoder.encode(clave));
        usuario.setDebeCambiarClave(true);
        mailService.enviarClaveTemporal(usuario, clave);
    }

    @Transactional
    public Usuario crearAdministrador(AdministradorRequest r) {
        return usuarioRepository.save(nuevoUsuario(r.username(), r.mail(), TipoUsuario.ADMINISTRADOR));
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarAdministradores() {
        return usuarioRepository.findByTipoUsuarioOrderByUsernameAsc(TipoUsuario.ADMINISTRADOR);
    }

    @Transactional(readOnly = true)
    public Usuario buscarAdministrador(String username) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getTipoUsuario() == TipoUsuario.ADMINISTRADOR)
                .orElseThrow(() -> new NoEncontradoException("No existe un administrador con usuario " + username));
    }

    /** Del administrador solo se cambia el mail; el username es fijo. */
    @Transactional
    public Usuario actualizarAdministrador(String username, AdministradorRequest r) {
        Usuario admin = buscarAdministrador(username);
        cambiarMail(admin, r.mail());
        return admin;
    }

    /** Un administrador no se puede eliminar a si mismo (asi siempre queda al menos uno). */
    @Transactional
    public void eliminarAdministrador(String username, String quienElimina) {
        Usuario admin = buscarAdministrador(username);
        if (admin.getUsername().equals(quienElimina)) {
            throw new ConflictoException("No podés eliminar tu propio usuario");
        }
        usuarioRepository.delete(admin);
    }

    // ---------- auxiliares ----------

    private Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new NoEncontradoException("No existe el usuario " + username));
    }

    private static String claveTemporal() {
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < LARGO_CLAVE_TEMPORAL; i++) {
            clave.append(CARACTERES_CLAVE.charAt(AZAR.nextInt(CARACTERES_CLAVE.length())));
        }
        return clave.toString();
    }

    private void validarMail(String mail) {
        if (mail == null || !mail.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new SolicitudInvalidaException("El mail es obligatorio y tiene que ser válido (ejemplo: nombre@dominio.com)");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < LARGO_MINIMO_PASSWORD) {
            throw new SolicitudInvalidaException(
                    "La contraseña es obligatoria y debe tener al menos " + LARGO_MINIMO_PASSWORD + " caracteres");
        }
    }
}
