package com.estacionamiento.repositorys;

import com.estacionamiento.models.Socio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {
    Optional<Socio> findByDni(Integer dni);
    boolean existsByDni(Integer dni);
    Optional<Socio> findByUsuarioUsername(String username);
}
