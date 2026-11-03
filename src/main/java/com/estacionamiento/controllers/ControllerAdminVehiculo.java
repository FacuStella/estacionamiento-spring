package com.estacionamiento.controllers;

import com.estacionamiento.dto.VehiculoRequest;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.services.VehiculoService;
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
@RequestMapping("/api/admin/vehiculos")
public class ControllerAdminVehiculo {

    private final VehiculoService vehiculoService;

    public ControllerAdminVehiculo(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehiculo registrarVehiculo(@Valid @RequestBody VehiculoRequest vehiculo) {
        return vehiculoService.crearVehiculo(vehiculo);
    }

    @GetMapping("/mostrar/{patente}")
    public Vehiculo obtenerVehiculo(@PathVariable String patente) {
        return vehiculoService.buscarPorPatente(patente);
    }

    @GetMapping("/listar")
    public List<Vehiculo> listarVehiculos() {
        return vehiculoService.listarVehiculos();
    }

    @PutMapping("/actualizar/{patente}")
    public Vehiculo actualizarVehiculo(@PathVariable String patente, @Valid @RequestBody VehiculoRequest vehiculo) {
        return vehiculoService.actualizarVehiculo(patente, vehiculo);
    }

    @DeleteMapping("/eliminar/{patente}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarVehiculo(@PathVariable String patente) {
        vehiculoService.eliminarVehiculo(patente);
    }
}
