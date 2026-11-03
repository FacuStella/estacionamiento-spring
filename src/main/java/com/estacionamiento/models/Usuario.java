package com.estacionamiento.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.estacionamiento.TipoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    /** Hash BCrypt. Nunca se expone en las respuestas JSON. */
    @JsonIgnore
    @NotBlank
    @Column(nullable = false)
    private String password;

    /** A donde se manda la clave temporal. Los usuarios creados antes de este campo pueden no tenerlo. */
    @Column(length = 120)
    private String mail;

    /** true mientras tenga una clave temporal: hasta cambiarla solo puede usar /api/cuenta. */
    @Column(nullable = false)
    private boolean debeCambiarClave;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario;

    public Usuario() {
    }

    public Usuario(String username, String password, TipoUsuario tipoUsuario) {
        this.username = username;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isDebeCambiarClave() {
        return debeCambiarClave;
    }

    public void setDebeCambiarClave(boolean debeCambiarClave) {
        this.debeCambiarClave = debeCambiarClave;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    
    
}
