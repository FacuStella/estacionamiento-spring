package com.estacionamiento.services;

import com.estacionamiento.TipoUsuario;
import com.estacionamiento.dto.EmpleadoRequest;
import com.estacionamiento.exceptions.ConflictoException;
import com.estacionamiento.exceptions.NoEncontradoException;
import com.estacionamiento.models.Empleado;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Vehiculo;
import com.estacionamiento.models.Zona;
import com.estacionamiento.repositorys.EmpleadoRepository;
import com.estacionamiento.repositorys.GarageRepository;
import com.estacionamiento.repositorys.VehiculoRepository;
import com.estacionamiento.repositorys.ZonaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ZonaRepository zonaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final GarageRepository garageRepository;
    private final UsuarioService usuarioService;
    private final ZonaService zonaService;
    private final VehiculoService vehiculoService;

    @Transactional
    public Empleado crearEmpleado(EmpleadoRequest r) {
        if (empleadoRepository.existsByDni(r.dni())) {
            throw new ConflictoException("Ya existe un empleado con DNI " + r.dni());
        }
        if (empleadoRepository.existsByCodigo(r.codigo())) {
            throw new ConflictoException("Ya existe un empleado con código " + r.codigo());
        }
        Empleado empleado = new Empleado(r.nombre(), r.dni(), r.direccion(), r.telefono(),
                r.codigo(), r.especialidad());
        empleado.setUsuario(usuarioService.nuevoUsuario(r.username(), r.mail(), TipoUsuario.EMPLEADO));
        return empleadoRepository.save(empleado);
    }

    @Transactional(readOnly = true)
    public Empleado buscarPorDni(Integer dni) {
        return empleadoRepository.findByDni(dni)
                .orElseThrow(() -> new NoEncontradoException("No existe un empleado con DNI " + dni));
    }

    @Transactional(readOnly = true)
    public Empleado buscarPorUsername(String username) {
        return empleadoRepository.findByUsuarioUsername(username)
                .orElseThrow(() -> new NoEncontradoException("No hay un empleado asociado al usuario " + username));
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    @Transactional
    public Empleado actualizarEmpleado(Integer dni, EmpleadoRequest r) {
        Empleado empleado = buscarPorDni(dni);
        if (!empleado.getDni().equals(r.dni()) && empleadoRepository.existsByDni(r.dni())) {
            throw new ConflictoException("Ya existe un empleado con DNI " + r.dni());
        }
        if (!empleado.getCodigo().equals(r.codigo()) && empleadoRepository.existsByCodigo(r.codigo())) {
            throw new ConflictoException("Ya existe un empleado con código " + r.codigo());
        }
        empleado.setNombre(r.nombre());
        empleado.setDni(r.dni());
        empleado.setDireccion(r.direccion());
        empleado.setTelefono(r.telefono());
        empleado.setCodigo(r.codigo());
        empleado.setEspecialidad(r.especialidad());
        usuarioService.cambiarMail(empleado.getUsuario(), r.mail());
        return empleadoRepository.save(empleado);
    }

    /** Al eliminar, el empleado se desvincula de sus zonas y de sus vehículos. */
    @Transactional
    public void eliminarEmpleado(Integer dni) {
        Empleado empleado = buscarPorDni(dni);
        for (Zona zona : zonaRepository.findByEmpleadosId(empleado.getId())) {
            zona.quitarEmpleado(empleado);
        }
        for (Vehiculo v : vehiculoRepository.findByEmpleadoAsignadoId(empleado.getId())) {
            v.setEmpleadoAsignado(null);
        }
        empleadoRepository.delete(empleado);
    }

    // ---------- zonas ----------

    @Transactional
    public void asignarZona(Integer dni, String letra) {
        zonaService.asignarEmpleado(letra, dni);
    }

    @Transactional
    public void quitarZona(Integer dni, String letra) {
        zonaService.quitarEmpleado(letra, dni);
    }

    @Transactional(readOnly = true)
    public List<Zona> listarZonas(Integer dni) {
        return zonaRepository.findByEmpleadosId(buscarPorDni(dni).getId());
    }

    /** Garages de todas las zonas en las que trabaja el empleado. */
    @Transactional(readOnly = true)
    public List<Garage> listarGarages(Integer dni) {
        return garageRepository.findByZonaEmpleadosId(buscarPorDni(dni).getId());
    }

    // ---------- vehículos ----------

    @Transactional
    public Vehiculo asignarVehiculo(Integer dni, String patente) {
        Empleado empleado = buscarPorDni(dni);
        Vehiculo v = vehiculoService.buscarPorPatente(patente);
        if (v.tieneEmpleadoAsignado() && !v.getEmpleadoAsignado().getId().equals(empleado.getId())) {
            throw new ConflictoException("El vehículo ya está asignado a otro empleado");
        }
        v.setEmpleadoAsignado(empleado);
        return vehiculoRepository.save(v);
    }

    @Transactional
    public Vehiculo quitarVehiculo(Integer dni, String patente) {
        Empleado empleado = buscarPorDni(dni);
        Vehiculo v = vehiculoService.buscarPorPatente(patente);
        if (!v.tieneEmpleadoAsignado() || !v.getEmpleadoAsignado().getId().equals(empleado.getId())) {
            throw new ConflictoException("El vehículo no está asignado a este empleado");
        }
        v.setEmpleadoAsignado(null);
        return vehiculoRepository.save(v);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos(Integer dni) {
        return vehiculoRepository.findByEmpleadoAsignadoId(buscarPorDni(dni).getId());
    }
}
