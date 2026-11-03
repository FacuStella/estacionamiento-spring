package com.estacionamiento.controllers;

import com.estacionamiento.models.TipoVehiculo;
import com.estacionamiento.repositorys.TipoVehiculoRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Opciones para el desplegable de tipo de vehiculo. */
@RestController
@RequestMapping("/api/admin/tiposVehiculo")
public class ControllerAdminTipoVehiculo {

    private final TipoVehiculoRepository tipoVehiculoRepository;

    public ControllerAdminTipoVehiculo(TipoVehiculoRepository tipoVehiculoRepository) {
        this.tipoVehiculoRepository = tipoVehiculoRepository;
    }

    @GetMapping("/listar")
    public List<TipoVehiculo> listarTipos() {
        return tipoVehiculoRepository.findAll();
    }
}
