package com.estacionamiento.repositorys;

import com.estacionamiento.models.Garage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {
    Optional<Garage> findByNumero(Integer numero);
    boolean existsByNumero(Integer numero);
    boolean existsByZonaId(Long zonaId);
    boolean existsBySocioId(Long socioId);
    List<Garage> findBySocioId(Long socioId);
    List<Garage> findByZonaLetra(String letra);
    List<Garage> findByZonaEmpleadosId(Long empleadoId);
}
