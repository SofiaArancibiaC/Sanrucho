package cl.sanrucho.usuarios.repository;


import cl.sanrucho.usuarios.model.EventoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoUsuarioRepository extends JpaRepository<EventoUsuario, Integer> {

Optional<EventoUsuario> findByEventId(String eventId);

boolean existsByEventId(String eventId);

List<EventoUsuario> findByUsuarioId(Integer usuarioId);

List<EventoUsuario> findByPublicado(Boolean publicado);

}
