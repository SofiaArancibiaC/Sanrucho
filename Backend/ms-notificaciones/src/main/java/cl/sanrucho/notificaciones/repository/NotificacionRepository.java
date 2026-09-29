package cl.sanrucho.notificaciones.repository;

import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import cl.sanrucho.notificaciones.model.entity.Notificacion;
 
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
 
    List<Notificacion> findByUsuarioId(Integer usuarioId);
 
    List<Notificacion> findByUsuarioIdAndEstado(Integer usuarioId, String estado);
 
    List<Notificacion> findByEstado(String estado);
 
    List<Notificacion> findByCanal(String canal);
 
    List<Notificacion> findByTipo(String tipo);
}
 