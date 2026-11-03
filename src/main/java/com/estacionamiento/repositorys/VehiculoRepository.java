package com.estacionamiento.repositorys;

import com.estacionamiento.models.Vehiculo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPatente(String patente);
    boolean existsByPatente(String patente);
    boolean existsByPropietarioId(Long socioId);
    List<Vehiculo> findByPropietarioId(Long socioId);
    List<Vehiculo> findByEmpleadoAsignadoId(Long empleadoId);
}
