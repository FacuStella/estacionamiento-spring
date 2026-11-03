package com.estacionamiento.controllers;

import com.estacionamiento.models.Empleado;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.models.Zona;
import com.estacionamiento.services.EmpleadoService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints del empleado logueado: siempre trabajan sobre sus propios datos. */
@RestController
@RequestMapping("/api/empleado")
public class ControllerEmpleado {

    private final EmpleadoService empleadoService;

    public ControllerEmpleado(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping("/listarVehiculos")
    public List<Vehiculo> listarEmpleadoVehiculos(Authentication auth) {
        Empleado yo = empleadoService.buscarPorUsername(auth.getName());
        return empleadoService.listarVehiculos(yo.getDni());
    }

    @GetMapping("/listarGarages")
    public List<Garage> listarEmpleadoGarages(Authentication auth) {
        Empleado yo = empleadoService.buscarPorUsername(auth.getName());
        return empleadoService.listarGarages(yo.getDni());
    }

    @GetMapping("/listarZonas")
    public List<Zona> listarEmpleadoZonas(Authentication auth) {
        Empleado yo = empleadoService.buscarPorUsername(auth.getName());
        return empleadoService.listarZonas(yo.getDni());
    }
}
