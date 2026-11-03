package com.estacionamiento.controllers;

import com.estacionamiento.dto.GarageRequest;
import com.estacionamiento.models.Garage;
import com.estacionamiento.services.GarageService;
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
@RequestMapping("/api/admin/garages")
public class ControllerAdminGarage {

    private final GarageService garageService;

    public ControllerAdminGarage(GarageService garageService) {
        this.garageService = garageService;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Garage crearGarage(@Valid @RequestBody GarageRequest garage) {
        return garageService.crearGarage(garage);
    }

    @GetMapping("/mostrar/{numero}")
    public Garage obtenerGarage(@PathVariable Integer numero) {
        return garageService.buscarPorNumero(numero);
    }

    @GetMapping("/listar")
    public List<Garage> listarGarages() {
        return garageService.listarGarages();
    }

    @PutMapping("/actualizar/{numero}")
    public Garage actualizarGarage(@PathVariable Integer numero, @Valid @RequestBody GarageRequest garage) {
        return garageService.actualizarGarage(numero, garage);
    }

    @DeleteMapping("/eliminar/{numero}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarGarage(@PathVariable Integer numero) {
        garageService.eliminarGarage(numero);
    }

    @PutMapping("/asignarZona/{numero}/{letra}")
    public Garage asignarGarageZona(@PathVariable Integer numero, @PathVariable String letra) {
        return garageService.asignarZona(numero, letra);
    }

    @PutMapping("/asignarSocio/{numero}/{dniSocio}")
    public Garage asignarGarageSocio(@PathVariable Integer numero, @PathVariable Integer dniSocio) {
        return garageService.asignarSocio(numero, dniSocio);
    }

    @DeleteMapping("/quitarSocio/{numero}")
    public Garage quitarGarageSocio(@PathVariable Integer numero) {
        return garageService.quitarSocio(numero);
    }
}
