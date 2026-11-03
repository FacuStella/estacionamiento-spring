package com.estacionamiento.controllers;

import com.estacionamiento.dto.EmpleadoRequest;
import com.estacionamiento.models.Empleado;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.models.Zona;
import com.estacionamiento.services.EmpleadoService;
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
@RequestMapping("/api/admin/empleados")
public class ControllerAdminEmpleado {

    private final EmpleadoService empleadoService;

    public ControllerAdminEmpleado(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Empleado crearEmpleado(@Valid @RequestBody EmpleadoRequest empleado) {
        return empleadoService.crearEmpleado(empleado);
    }

    @GetMapping("/mostrar/{dni}")
    public Empleado obtenerEmpleado(@PathVariable Integer dni) {
        return empleadoService.buscarPorDni(dni);
    }

    @GetMapping("/listar")
    public List<Empleado> listarEmpleados() {
        return empleadoService.listarEmpleados();
    }

    @PutMapping("/actualizar/{dni}")
    public Empleado actualizarEmpleado(@PathVariable Integer dni, @Valid @RequestBody EmpleadoRequest empleado) {
        return empleadoService.actualizarEmpleado(dni, empleado);
    }

    @DeleteMapping("/eliminar/{dni}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarEmpleado(@PathVariable Integer dni) {
        empleadoService.eliminarEmpleado(dni);
    }

    @PostMapping("/asignarZona/{dni}/{letra}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void asignarEmpleadoZona(@PathVariable Integer dni, @PathVariable String letra) {
        empleadoService.asignarZona(dni, letra);
    }

    @DeleteMapping("/quitarZona/{dni}/{letra}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarEmpleadoZona(@PathVariable Integer dni, @PathVariable String letra) {
        empleadoService.quitarZona(dni, letra);
    }

    @PostMapping("/asignarVehiculo/{dni}/{patente}")
    public Vehiculo asignarVehiculoEmpleado(@PathVariable Integer dni, @PathVariable String patente) {
        return empleadoService.asignarVehiculo(dni, patente);
    }

    @DeleteMapping("/quitarVehiculo/{dni}/{patente}")
    public Vehiculo quitarEmpleadoVehiculo(@PathVariable Integer dni, @PathVariable String patente) {
        return empleadoService.quitarVehiculo(dni, patente);
    }

    @GetMapping("/listarVehiculos/{dni}")
    public List<Vehiculo> listarEmpleadoVehiculos(@PathVariable Integer dni) {
        return empleadoService.listarVehiculos(dni);
    }

    @GetMapping("/listarZonas/{dni}")
    public List<Zona> listarEmpleadoZonas(@PathVariable Integer dni) {
        return empleadoService.listarZonas(dni);
    }
}
