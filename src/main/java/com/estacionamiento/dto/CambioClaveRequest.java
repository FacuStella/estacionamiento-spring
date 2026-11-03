package com.estacionamiento.dto;

import jakarta.validation.constraints.NotBlank;

public record CambioClaveRequest(@NotBlank String actual, @NotBlank String nueva) {
}
