package com.estacionamiento.dto;

/** debeCambiarClave: el usuario entro con una clave temporal y tiene que elegir una nueva antes de seguir. */
public record LoginResponse(String token, String tipoUsuario, String username, boolean debeCambiarClave) {
}
