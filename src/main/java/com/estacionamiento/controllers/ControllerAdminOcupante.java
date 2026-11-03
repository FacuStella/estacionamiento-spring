package com.estacionamiento.controllers;

import com.estacionamiento.models.Ocupante;
import com.estacionamiento.services.OcupanteService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Entradas y salidas de vehiculos (estadias). */
@RestController
@RequestMapping("/api/admin/ocupantes")
public class ControllerAdminOcupante {

    private final OcupanteService ocupanteService;

    public ControllerAdminOcupante(OcupanteService ocupanteService) {
        this.ocupanteService = ocupanteService;
    }

    @PostMapping("/entrada/{numero}/{patente}")
    @ResponseStatus(HttpStatus.CREATED)
    public Ocupante registrarEntrada(@PathVariable Integer numero, @PathVariable String patente) {
        return ocupanteService.registrarEntrada(numero, patente);
    }

    @PostMapping("/salida/{numero}")
    public Ocupante registrarSalida(@PathVariable Integer numero) {
        return ocupanteService.registrarSalida(numero);
    }

    /** Los vehiculos que estan adentro ahora. */
    @GetMapping("/activos")
    public List<Ocupante> listarActivos() {
        return ocupanteService.listarActivos();
    }

    /** Todas las estadias, de la mas nueva a la mas vieja. */
    @GetMapping("/listar")
    public List<Ocupante> listarTodos() {
        return ocupanteService.listarTodos();
    }

    @GetMapping("/vehiculo/{patente}")
    public List<Ocupante> listarPorVehiculo(@PathVariable String patente) {
        return ocupanteService.listarPorVehiculo(patente);
    }
}
