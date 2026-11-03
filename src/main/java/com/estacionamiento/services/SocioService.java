package com.estacionamiento.services;

import com.estacionamiento.TipoUsuario;
import com.estacionamiento.dto.SocioRequest;
import com.estacionamiento.exceptions.ConflictoException;
import com.estacionamiento.exceptions.NoEncontradoException;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Socio;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.repositorys.GarageRepository;
import com.estacionamiento.repositorys.SocioRepository;
import com.estacionamiento.repositorys.VehiculoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final SocioRepository socioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final GarageRepository garageRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public Socio crearSocio(SocioRequest r) {
        if (socioRepository.existsByDni(r.dni())) {
            throw new ConflictoException("Ya existe un socio con DNI " + r.dni());
        }
        Socio socio = new Socio(r.nombre(), r.dni(), r.direccion(), r.telefono());
        socio.setUsuario(usuarioService.nuevoUsuario(r.username(), r.mail(), TipoUsuario.SOCIO));
        return socioRepository.save(socio);
    }

    @Transactional(readOnly = true)
    public Socio buscarPorDni(Integer dni) {
        return socioRepository.findByDni(dni)
                .orElseThrow(() -> new NoEncontradoException("No existe un socio con DNI " + dni));
    }

    @Transactional(readOnly = true)
    public Socio buscarPorUsername(String username) {
        return socioRepository.findByUsuarioUsername(username)
                .orElseThrow(() -> new NoEncontradoException("No hay un socio asociado al usuario " + username));
    }

    @Transactional(readOnly = true)
    public List<Socio> listarSocios() {
        return socioRepository.findAll();
    }

    @Transactional
    public Socio actualizarSocio(Integer dni, SocioRequest r) {
        Socio socio = buscarPorDni(dni);
        if (!socio.getDni().equals(r.dni()) && socioRepository.existsByDni(r.dni())) {
            throw new ConflictoException("Ya existe un socio con DNI " + r.dni());
        }
        socio.setNombre(r.nombre());
        socio.setDni(r.dni());
        socio.setDireccion(r.direccion());
        socio.setTelefono(r.telefono());
        usuarioService.cambiarMail(socio.getUsuario(), r.mail());
        return socioRepository.save(socio);
    }

    @Transactional
    public void eliminarSocio(Integer dni) {
        Socio socio = buscarPorDni(dni);
        if (vehiculoRepository.existsByPropietarioId(socio.getId())
                || garageRepository.existsBySocioId(socio.getId())) {
            throw new ConflictoException(
                    "No se puede eliminar al socio: tiene vehículos o garages asociados");
        }
        socioRepository.delete(socio);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos(Integer dni) {
        return vehiculoRepository.findByPropietarioId(buscarPorDni(dni).getId());
    }

    @Transactional(readOnly = true)
    public List<Garage> listarGarages(Integer dni) {
        return garageRepository.findBySocioId(buscarPorDni(dni).getId());
    }
}
