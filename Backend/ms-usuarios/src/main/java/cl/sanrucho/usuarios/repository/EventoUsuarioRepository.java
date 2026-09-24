package cl.sanrucho.usuarios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.usuarios.model.EventoUsuario;

/**
 * Repositorio JPA de la entidad EventoUsuario.
 *
 * Query Methods automaticas (sin SQL manual):
 * - findByUsuarioId: trae los eventos de un usuario.
 * - existsByUsuarioId: valida si un usuario tiene eventos registrados.
 * - existsByEventId: valida que el eventId sea unico antes de insertar.
 */
@Repository
public interface EventoUsuarioRepository extends JpaRepository<EventoUsuario, Integer> {

    List<EventoUsuario> findByUsuarioId(Integer usuarioId);

    boolean existsByUsuarioId(Integer usuarioId);

    boolean existsByEventId(String eventId);
}
