package com.estacionamiento.services;

import com.estacionamiento.exceptions.ConflictoException;
import com.estacionamiento.exceptions.NoEncontradoException;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Ocupante;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.repositorys.GarageRepository;
import com.estacionamiento.repositorys.OcupanteRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Entradas y salidas de vehiculos. Cualquier vehiculo puede entrar a un garage libre y paga al salir
 * por hora (o fraccion) segun la tarifa de la zona; si el vehiculo es de un socio, tiene descuento.
 * Un garage con abono solo lo pueden usar los vehiculos de su dueño, y no pagan.
 */
@Service
@RequiredArgsConstructor
public class OcupanteService {

    private final OcupanteRepository ocupanteRepository;
    private final GarageRepository garageRepository;
    private final VehiculoService vehiculoService;

    @Value("${app.descuento-socio-porcentaje:20}")
    private double descuentoSocio;

    @Transactional
    public Ocupante registrarEntrada(Integer numeroGarage, String patente) {
        Garage garage = buscarGarage(numeroGarage);
        Vehiculo vehiculo = vehiculoService.buscarPorPatente(patente);

        ocupanteRepository.findByVehiculoIdAndSalidaIsNull(vehiculo.getId()).ifPresent(o -> {
            throw new ConflictoException("El vehículo " + vehiculo.getPatente()
                    + " ya está en el garage " + o.getGarage().getNumero());
        });
        if (ocupanteRepository.existsByGarageIdAndSalidaIsNull(garage.getId())) {
            throw new ConflictoException("El garage " + numeroGarage + " ya está ocupado");
        }
        if (garage.tienePropietario() && !esDelDuenoDelAbono(vehiculo, garage)) {
            throw new ConflictoException("El garage " + numeroGarage + " tiene abono de "
                    + garage.getSocio().getNombre() + ": solo pueden usarlo sus vehículos");
        }
        return ocupanteRepository.save(new Ocupante(vehiculo, garage, LocalDateTime.now()));
    }

    @Transactional
    public Ocupante registrarSalida(Integer numeroGarage) {
        Garage garage = buscarGarage(numeroGarage);
        Ocupante ocupante = ocupanteRepository.findByGarageIdAndSalidaIsNull(garage.getId())
                .orElseThrow(() -> new ConflictoException("El garage " + numeroGarage + " no tiene ningún vehículo"));

        LocalDateTime salida = LocalDateTime.now();
        long minutos = Math.max(1, Duration.between(ocupante.getEntrada(), salida).toMinutes());
        int horas = (int) ((minutos + 59) / 60);
        double bruto = horas * garage.getZona().getTarifaHora();

        double descuento;
        if (esDelDuenoDelAbono(ocupante.getVehiculo(), garage)) {
            descuento = 100;
        } else if (ocupante.getVehiculo().getPropietario() != null) {
            descuento = descuentoSocio;
        } else {
            descuento = 0;
        }
        double importe = Math.round(bruto * (100 - descuento)) / 100.0;
        ocupante.cerrar(salida, horas, descuento, importe);
        return ocupanteRepository.save(ocupante);
    }

    @Transactional(readOnly = true)
    public List<Ocupante> listarActivos() {
        return ocupanteRepository.findBySalidaIsNull();
    }

    @Transactional(readOnly = true)
    public List<Ocupante> listarTodos() {
        return ocupanteRepository.findAllByOrderByEntradaDesc();
    }

    @Transactional(readOnly = true)
    public List<Ocupante> listarPorVehiculo(String patente) {
        return ocupanteRepository.findByVehiculoIdOrderByEntradaDesc(vehiculoService.buscarPorPatente(patente).getId());
    }

    private boolean esDelDuenoDelAbono(Vehiculo vehiculo, Garage garage) {
        return garage.tienePropietario() && vehiculo.getPropietario() != null
                && garage.getSocio().getId().equals(vehiculo.getPropietario().getId());
    }

    private Garage buscarGarage(Integer numero) {
        return garageRepository.findByNumero(numero)
                .orElseThrow(() -> new NoEncontradoException("No existe el garage " + numero));
    }
}
