package com.estacionamiento.exceptions;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorRespuesta(int status, String mensaje, List<String> detalles) {
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(NoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorRespuesta> conflicto(ConflictoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> solicitudInvalida(SolicitudInvalidaException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorRespuesta> restricciones(ConstraintViolationException ex) {
        List<String> detalles = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> integridad(DataIntegrityViolationException ex) {
        return construir(HttpStatus.CONFLICT,
                "La operación viola una restricción de la base de datos (dato duplicado o registros relacionados)",
                List.of());
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorRespuesta> mail(MailException ex) {
        return construir(HttpStatus.BAD_GATEWAY, "No se pudo enviar el mail con la clave: no se guardó nada",
                List.of(String.valueOf(ex.getMessage())));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorRespuesta> credenciales(BadCredentialsException ex) {
        return construir(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", List.of());
    }

    private ResponseEntity<ErrorRespuesta> construir(HttpStatus status, String mensaje, List<String> detalles) {
        return ResponseEntity.status(status).body(new ErrorRespuesta(status.value(), mensaje, detalles));
    }
}
