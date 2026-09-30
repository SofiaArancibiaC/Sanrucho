package cl.sanrucho.catalogo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import cl.sanrucho.catalogo.model.entity.EventoCatalogo;


public interface EventoCatalogoRepository extends JpaRepository<EventoCatalogo, Long>{

    Optional<EventoCatalogo> findByEventId(String eventId);

    List<EventoCatalogo> findByPublicadoFalseOrderByFechaEventoAsc();

    List<EventoCatalogo> findByProductoIdOrderByFechaEventoDesc(Long productoId);
}
