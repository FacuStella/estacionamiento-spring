package com.estacionamiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * dniSocio, lecturaContadorLuz y mantenimientoContratado son opcionales.
 * Al actualizar, un campo opcional en null significa "no modificar".
 */
public record GarageRequest(
        @NotNull Integer numero,
        @NotBlank String letraZona,
        Integer dniSocio,
        Double lecturaContadorLuz,
        Boolean mantenimientoContratado) {
}
