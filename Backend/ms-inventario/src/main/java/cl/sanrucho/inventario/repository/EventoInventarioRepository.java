package cl.sanrucho.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.inventario.model.EventoInventario;

import java.util.Optional;
import java.util.List;

@Repository
public interface EventoInventarioRepository extends JpaRepository<EventoInventario, Integer> {

    Optional<EventoInventario> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

    List<EventoInventario> findByPublicado(Boolean publicado);
}