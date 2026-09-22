package cl.sanrucho.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.reviews.model.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoReviewRepository extends JpaRepository<EventoReview, Integer> {

Optional<EventoReview> findByEventId(String eventId);

boolean existsByEventId(String eventId);

List<EventoReview> findByReviewId(Integer reviewId);

List<EventoReview> findByProductoId(Integer productoId);

List<EventoReview> findByPublicado(Boolean publicado);

List<EventoReview> findByTipoEvento(EventoReview.TipoEvento tipoEvento);

}
