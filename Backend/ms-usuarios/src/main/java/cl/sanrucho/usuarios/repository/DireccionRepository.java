package cl.sanrucho.usuarios.repository;

import cl.sanrucho.usuarios.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

List<Direccion> findByUsuarioId(Integer usuarioId);

List<Direccion> findByUsuarioIdAndEsPrincipal(Integer usuarioId, Boolean esPrincipal);

}

