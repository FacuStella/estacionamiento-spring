package com.estacionamiento.controllers;

import com.estacionamiento.dto.SocioRequest;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Socio;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.services.SocioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/socios")
public class ControllerAdminSocio {

    private final SocioService socioService;

    public ControllerAdminSocio(SocioService socioService) {
        this.socioService = socioService;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Socio crearSocio(@Valid @RequestBody SocioRequest socio) {
        return socioService.crearSocio(socio);
    }

    @GetMapping("/mostrar/{dni}")
    public Socio obtenerSocio(@PathVariable Integer dni) {
        return socioService.buscarPorDni(dni);
    }

    @GetMapping("/listar")
    public List<Socio> listarSocios() {
        return socioService.listarSocios();
    }

    @PutMapping("/actualizar/{dni}")
    public Socio actualizarSocio(@PathVariable Integer dni, @Valid @RequestBody SocioRequest socio) {
        return socioService.actualizarSocio(dni, socio);
    }

    @DeleteMapping("/eliminar/{dni}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarSocio(@PathVariable Integer dni) {
        socioService.eliminarSocio(dni);
    }

    @GetMapping("/listarVehiculos/{dni}")
    public List<Vehiculo> listarSocioVehiculos(@PathVariable Integer dni) {
        return socioService.listarVehiculos(dni);
    }

    @GetMapping("/listarGarages/{dni}")
    public List<Garage> listarSocioGarages(@PathVariable Integer dni) {
        return socioService.listarGarages(dni);
    }
}
