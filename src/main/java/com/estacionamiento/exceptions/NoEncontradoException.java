package com.estacionamiento.exceptions;

/** Recurso inexistente (HTTP 404). */
public class NoEncontradoException extends RuntimeException {
    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
