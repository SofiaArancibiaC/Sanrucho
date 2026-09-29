package cl.sanrucho.notificaciones.repository;

import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import cl.sanrucho.notificaciones.model.entity.EventoNotificacion;
 
public interface EventoNotificacionRepository extends JpaRepository<EventoNotificacion, Integer> {
 
    Optional<EventoNotificacion> findByEventId(String eventId);
 
    List<EventoNotificacion> findByPublicado(Boolean publicado);
 
    List<EventoNotificacion> findByNotificacionId(Integer notificacionId);
 
    boolean existsByEventId(String eventId);
}
 