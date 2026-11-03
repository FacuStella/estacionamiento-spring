package com.estacionamiento.services;

import com.estacionamiento.dto.VehiculoRequest;
import com.estacionamiento.exceptions.ConflictoException;
import com.estacionamiento.exceptions.NoEncontradoException;
import com.estacionamiento.exceptions.SolicitudInvalidaException;
import com.estacionamiento.models.Marca;
import com.estacionamiento.models.Socio;
import com.estacionamiento.models.TipoVehiculo;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.repositorys.MarcaRepository;
import com.estacionamiento.repositorys.OcupanteRepository;
import com.estacionamiento.repositorys.SocioRepository;
import com.estacionamiento.repositorys.TipoVehiculoRepository;
import com.estacionamiento.repositorys.VehiculoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final SocioRepository socioRepository;
    private final OcupanteRepository ocupanteRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final MarcaRepository marcaRepository;

    @Transactional
    public Vehiculo crearVehiculo(VehiculoRequest r) {
        String patente = normalizar(r.patente());
        if (vehiculoRepository.existsByPatente(patente)) {
            throw new ConflictoException("Ya existe un vehículo con patente " + patente);
        }
        TipoVehiculo tipo = buscarTipo(r.idTipo());
        Vehiculo v = new Vehiculo(patente, buscarMarca(r.idMarca(), tipo), tipo, r.dimensiones(), buscarSocio(r.dniPropietario()));
        return vehiculoRepository.save(v);
    }

    @Transactional(readOnly = true)
    public Vehiculo buscarPorPatente(String patente) {
        String p = normalizar(patente);
        return vehiculoRepository.findByPatente(p)
                .orElseThrow(() -> new NoEncontradoException("No existe un vehículo con patente " + p));
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    @Transactional
    public Vehiculo actualizarVehiculo(String patente, VehiculoRequest r) {
        Vehiculo v = buscarPorPatente(patente);
        String nuevaPatente = normalizar(r.patente());
        if (!v.getPatente().equals(nuevaPatente) && vehiculoRepository.existsByPatente(nuevaPatente)) {
            throw new ConflictoException("Ya existe un vehículo con patente " + nuevaPatente);
        }
        Socio nuevoPropietario = buscarSocio(r.dniPropietario());
        if (!mismoSocio(v.getPropietario(), nuevoPropietario)
                && ocupanteRepository.existsByVehiculoIdAndSalidaIsNull(v.getId())) {
            throw new ConflictoException("No se puede cambiar el propietario mientras el vehículo esté estacionado");
        }
        v.setPatente(nuevaPatente);
        TipoVehiculo tipo = buscarTipo(r.idTipo());
        v.setMarca(buscarMarca(r.idMarca(), tipo));
        v.setTipo(tipo);
        v.setDimensiones(r.dimensiones());
        v.setPropietario(nuevoPropietario);
        return vehiculoRepository.save(v);
    }

    /** Todo vehiculo que paso queda registrado: solo se puede eliminar si nunca estaciono. */
    @Transactional
    public void eliminarVehiculo(String patente) {
        Vehiculo v = buscarPorPatente(patente);
        if (ocupanteRepository.existsByVehiculoId(v.getId())) {
            throw new ConflictoException("No se puede eliminar el vehículo: tiene estadías registradas");
        }
        vehiculoRepository.delete(v);
    }

    private TipoVehiculo buscarTipo(Long idTipo) {
        return tipoVehiculoRepository.findById(idTipo)
                .orElseThrow(() -> new NoEncontradoException("No existe el tipo de vehículo " + idTipo));
    }

    /** La marca tiene que fabricar vehiculos del tipo elegido (por ejemplo, Yamaha no es una marca de autos). */
    private Marca buscarMarca(Long idMarca, TipoVehiculo tipo) {
        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new NoEncontradoException("No existe la marca " + idMarca));
        if (!marca.fabrica(tipo)) {
            throw new SolicitudInvalidaException(marca.getMarca() + " no es una marca de " + tipo.getTipo().toLowerCase());
        }
        return marca;
    }

    /** null si no se indico socio. */
    private Socio buscarSocio(Integer dni) {
        if (dni == null) {
            return null;
        }
        return socioRepository.findByDni(dni)
                .orElseThrow(() -> new NoEncontradoException("No existe un socio con DNI " + dni));
    }

    private boolean mismoSocio(Socio a, Socio b) {
        return a == null ? b == null : b != null && a.getId().equals(b.getId());
    }

    private String normalizar(String patente) {
        return patente == null ? null : patente.trim().toUpperCase();
    }
}
