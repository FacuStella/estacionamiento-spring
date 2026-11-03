package com.estacionamiento.repositorys;

import com.estacionamiento.models.Zona;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRepository extends JpaRepository<Zona, Long> {
    Optional<Zona> findByLetra(String letra);
    boolean existsByLetra(String letra);
    List<Zona> findByEmpleadosId(Long empleadoId);
}
