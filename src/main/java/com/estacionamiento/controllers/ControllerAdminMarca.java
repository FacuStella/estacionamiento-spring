package com.estacionamiento.controllers;

import com.estacionamiento.models.Marca;
import com.estacionamiento.repositorys.MarcaRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Opciones para el desplegable de marca. Con idTipo devuelve solo las marcas de ese tipo de vehiculo. */
@RestController
@RequestMapping("/api/admin/marcas")
public class ControllerAdminMarca {

    private final MarcaRepository marcaRepository;

    public ControllerAdminMarca(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @GetMapping("/listar")
    public List<Marca> listarMarcas(@RequestParam(required = false) Long idTipo) {
        return idTipo == null ? marcaRepository.findAllByOrderByMarcaAsc()
                : marcaRepository.findByTiposIdTipoOrderByMarcaAsc(idTipo);
    }
}
