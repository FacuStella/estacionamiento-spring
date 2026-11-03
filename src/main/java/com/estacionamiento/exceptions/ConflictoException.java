package com.estacionamiento.exceptions;

/** Duplicados o reglas de negocio que impiden la operación (HTTP 409). */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
