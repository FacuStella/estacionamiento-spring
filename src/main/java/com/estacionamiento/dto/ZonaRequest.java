package com.estacionamiento.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ZonaRequest(
        @NotBlank String letra,
        @NotBlank String tipoVehiculos,
        @NotNull @Min(1) Integer profundidadGarage,
        @NotNull @Min(1) Integer anchoGarage,
        @NotNull Integer planta,
        @NotNull @Positive Double tarifaHora) {
}
