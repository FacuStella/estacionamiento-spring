package com.estacionamiento.services;

import com.estacionamiento.dto.GarageRequest;
import com.estacionamiento.exceptions.ConflictoException;
import com.estacionamiento.exceptions.NoEncontradoException;
import com.estacionamiento.exceptions.SolicitudInvalidaException;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Socio;
import com.estacionamiento.repositorys.GarageRepository;
import com.estacionamiento.repositorys.OcupanteRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GarageService {

    private final GarageRepository garageRepository;
    private final OcupanteRepository ocupanteRepository;
    private final ZonaService zonaService;
    private final SocioService socioService;

    @Transactional
    public Garage crearGarage(GarageRequest r) {
        if (garageRepository.existsByNumero(r.numero())) {
            throw new ConflictoException("Ya existe el garage " + r.numero());
        }
        Garage garage = new Garage(r.numero(), zonaService.buscarPorLetra(r.letraZona()));
        aplicarOpcionales(garage, r);
        return garageRepository.save(garage);
    }

    @Transactional(readOnly = true)
    public Garage buscarPorNumero(Integer numero) {
        return garageRepository.findByNumero(numero)
                .orElseThrow(() -> new NoEncontradoException("No existe el garage " + numero));
    }

    @Transactional(readOnly = true)
    public List<Garage> listarGarages() {
        return garageRepository.findAll();
    }

    @Transactional
    public Garage actualizarGarage(Integer numero, GarageRequest r) {
        Garage garage = buscarPorNumero(numero);
        if (!garage.getNumero().equals(r.numero()) && garageRepository.existsByNumero(r.numero())) {
            throw new ConflictoException("Ya existe el garage " + r.numero());
        }
        garage.setNumero(r.numero());
        garage.setZona(zonaService.buscarPorLetra(r.letraZona()));
        aplicarOpcionales(garage, r);
        return garageRepository.save(garage);
    }

    @Transactional
    public void eliminarGarage(Integer numero) {
        Garage garage = buscarPorNumero(numero);
        if (ocupanteRepository.existsByGarageId(garage.getId())) {
            throw new ConflictoException("No se puede eliminar el garage: tiene estadías registradas");
        }
        garageRepository.delete(garage);
    }

    @Transactional
    public Garage asignarZona(Integer numero, String letra) {
        Garage garage = buscarPorNumero(numero);
        garage.setZona(zonaService.buscarPorLetra(letra));
        return garageRepository.save(garage);
    }

    @Transactional
    public Garage asignarSocio(Integer numero, Integer dniSocio) {
        Garage garage = buscarPorNumero(numero);
        cambiarPropietario(garage, socioService.buscarPorDni(dniSocio));
        return garageRepository.save(garage);
    }

    @Transactional
    public Garage quitarSocio(Integer numero) {
        Garage garage = buscarPorNumero(numero);
        cambiarPropietario(garage, null);
        return garageRepository.save(garage);
    }

    private void aplicarOpcionales(Garage garage, GarageRequest r) {
        if (r.lecturaContadorLuz() != null) {
            if (r.lecturaContadorLuz() < 0) {
                throw new SolicitudInvalidaException("La lectura del contador de luz no puede ser negativa");
            }
            garage.setLecturaContadorLuz(r.lecturaContadorLuz());
        }
        if (r.mantenimientoContratado() != null) {
            garage.setMantenimientoContratado(r.mantenimientoContratado());
        }
        if (r.dniSocio() != null) {
            cambiarPropietario(garage, socioService.buscarPorDni(r.dniSocio()));
        }
    }

    /** Cambiar de propietario no es posible mientras haya un vehículo estacionado. */
    private void cambiarPropietario(Garage garage, Socio nuevo) {
        boolean esElMismo = garage.getSocio() != null && nuevo != null
                && garage.getSocio().getId().equals(nuevo.getId());
        if (esElMismo) {
            return;
        }
        if (garage.getId() != null && ocupanteRepository.existsByGarageIdAndSalidaIsNull(garage.getId())) {
            throw new ConflictoException(
                    "El garage tiene un vehículo estacionado; registrá su salida antes de cambiar de propietario");
        }
        garage.setSocio(nuevo);
        garage.setFechaCompra(nuevo == null ? null : LocalDate.now());
    }
}
