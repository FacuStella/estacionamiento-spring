package com.estacionamiento.repositorys;

import com.estacionamiento.models.Ocupante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcupanteRepository extends JpaRepository<Ocupante, Long> {
    /** Los vehiculos que estan adentro ahora. */
    List<Ocupante> findBySalidaIsNull();
    Optional<Ocupante> findByGarageIdAndSalidaIsNull(Long garageId);
    Optional<Ocupante> findByVehiculoIdAndSalidaIsNull(Long vehiculoId);
    boolean existsByGarageIdAndSalidaIsNull(Long garageId);
    boolean existsByVehiculoIdAndSalidaIsNull(Long vehiculoId);
    boolean existsByGarageId(Long garageId);
    boolean existsByVehiculoId(Long vehiculoId);
    List<Ocupante> findByVehiculoIdOrderByEntradaDesc(Long vehiculoId);
    List<Ocupante> findAllByOrderByEntradaDesc();
}
