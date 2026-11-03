package com.estacionamiento.repositorys;

import com.estacionamiento.models.Empleado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByDni(Integer dni);
    boolean existsByDni(Integer dni);
    boolean existsByCodigo(Integer codigo);
    Optional<Empleado> findByUsuarioUsername(String username);
}
