package com.estacionamiento.repositorys;

import com.estacionamiento.models.Marca;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    List<Marca> findAllByOrderByMarcaAsc();
    /** Las marcas que fabrican vehiculos de ese tipo. */
    List<Marca> findByTiposIdTipoOrderByMarcaAsc(Long idTipo);
}
