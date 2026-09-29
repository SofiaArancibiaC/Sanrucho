package cl.sanrucho.reviews.repository;

import cl.sanrucho.reviews.model.EventoReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoReviewRepository extends JpaRepository<EventoReview, Long> {

    Optional<EventoReview> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

    List<EventoReview> findByReviewId(Integer reviewId);

    List<EventoReview> findByProductoId(Integer productoId);

    List<EventoReview> findByPublicado(Boolean publicado);

    List<EventoReview> findByTipoEvento(EventoReview.TipoEvento tipoEvento);
}
