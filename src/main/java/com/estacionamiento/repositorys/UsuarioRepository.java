package com.estacionamiento.repositorys;

import com.estacionamiento.TipoUsuario;
import com.estacionamiento.models.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByTipoUsuario(TipoUsuario tipoUsuario);
    List<Usuario> findByTipoUsuarioOrderByUsernameAsc(TipoUsuario tipoUsuario);
}
