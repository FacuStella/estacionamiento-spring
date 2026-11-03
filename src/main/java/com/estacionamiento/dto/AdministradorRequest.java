package com.estacionamiento.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Al registrar se usan los dos; al actualizar solo el mail. La clave se genera y se manda al mail. */
public record AdministradorRequest(
        String username,
        @NotBlank @Email String mail) {
}
