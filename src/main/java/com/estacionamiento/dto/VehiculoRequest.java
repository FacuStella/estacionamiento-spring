package com.estacionamiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * idTipo e idMarca salen de las tablas tipos_vehiculo y marcas (la marca debe corresponder al tipo).
 * dniPropietario es opcional: solo si el vehiculo es de un socio.
 */
public record VehiculoRequest(
        @NotBlank String patente,
        @NotNull Long idMarca,
        @NotNull Long idTipo,
        @NotBlank String dimensiones,
        Integer dniPropietario) {
}
