package cl.sanrucho.catalogo.repository;

import com.tienda.mscatalogo.entity.EventoCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;
import java.util.Optional;


public interface EventoCatalogoRepository extends JpaRepository<EventoCatalogo, Long>{
 
    Optional<EventoCatalogo> findByEventId(String eventId);
 
    List<EventoCatalogo> findByPublicadoFalseOrderByFechaEventoAsc();
 
    List<EventoCatalogo> findByProductoIdOrderByFechaEventoDesc(Long productoId);
}
