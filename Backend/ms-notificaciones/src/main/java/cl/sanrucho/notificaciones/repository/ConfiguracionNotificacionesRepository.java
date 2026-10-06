package cl.sanrucho.notificaciones.repository;

import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import cl.sanrucho.notificaciones.model.entity.ConfiguracionNotificaciones;
 
public interface ConfiguracionNotificacionesRepository extends JpaRepository<ConfiguracionNotificaciones, Integer> {
 
    Optional<ConfiguracionNotificaciones> findByUsuarioId(Integer usuarioId);
 
    boolean existsByUsuarioId(Integer usuarioId);
}
 