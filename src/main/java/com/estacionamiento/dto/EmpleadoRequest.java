package com.estacionamiento.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * username es obligatorio al registrar y al actualizar se ignora. La contraseña no se informa:
 * se genera una temporal y se manda al mail.
 */
public record EmpleadoRequest(
        @NotBlank String nombre,
        @NotNull Integer dni,
        String direccion,
        String telefono,
        @NotNull Integer codigo,
        @NotBlank String especialidad,
        String username,
        @NotBlank @Email String mail) {
}
