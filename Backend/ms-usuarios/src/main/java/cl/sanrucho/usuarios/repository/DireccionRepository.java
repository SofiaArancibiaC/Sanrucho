package cl.sanrucho.usuarios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.usuarios.model.Direccion;

/**
 * Repositorio JPA de la entidad Direccion.
 *
 * Query Methods automaticas (sin SQL manual):
 * - findByUsuarioId: trae todas las direcciones de un usuario.
 * - existsByUsuarioId: valida rapido si un usuario tiene direcciones.
 */
@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

    List<Direccion> findByUsuarioId(Integer usuarioId);

    boolean existsByUsuarioId(Integer usuarioId);
}
