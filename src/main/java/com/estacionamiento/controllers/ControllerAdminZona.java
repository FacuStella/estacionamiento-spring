package com.estacionamiento.controllers;

import com.estacionamiento.dto.ZonaRequest;
import com.estacionamiento.models.Empleado;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Zona;
import com.estacionamiento.services.ZonaService;
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
@RequestMapping("/api/admin/zonas")
public class ControllerAdminZona {

    private final ZonaService zonaService;

    public ControllerAdminZona(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Zona registrarZona(@Valid @RequestBody ZonaRequest zona) {
        return zonaService.crearZona(zona);
    }

    @GetMapping("/mostrar/{letra}")
    public Zona obtenerZona(@PathVariable String letra) {
        return zonaService.buscarPorLetra(letra);
    }

    @GetMapping("/listar")
    public List<Zona> listarZonas() {
        return zonaService.listarZonas();
    }

    @PutMapping("/actualizar/{letra}")
    public Zona actualizarZona(@PathVariable String letra, @Valid @RequestBody ZonaRequest zona) {
        return zonaService.actualizarZona(letra, zona);
    }

    @DeleteMapping("/eliminar/{letra}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarZona(@PathVariable String letra) {
        zonaService.eliminarZona(letra);
    }

    @PostMapping("/asignarEmpleado/{letra}/{dniEmpleado}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void asignarEmpleado(@PathVariable String letra, @PathVariable Integer dniEmpleado) {
        zonaService.asignarEmpleado(letra, dniEmpleado);
    }

    @DeleteMapping("/quitarEmpleado/{letra}/{dniEmpleado}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarEmpleado(@PathVariable String letra, @PathVariable Integer dniEmpleado) {
        zonaService.quitarEmpleado(letra, dniEmpleado);
    }

    @GetMapping("/listarGarages/{letra}")
    public List<Garage> listarGarages(@PathVariable String letra) {
        return zonaService.listarGarages(letra);
    }

    @GetMapping("/listarEmpleados/{letra}")
    public List<Empleado> listarEmpleados(@PathVariable String letra) {
        return zonaService.listarEmpleados(letra);
    }
}
