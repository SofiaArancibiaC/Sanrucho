package cl.sanrucho.reviews.repository;

import cl.sanrucho.reviews.model.RespuestaReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaReviewRepository extends JpaRepository<RespuestaReview, Long> {

    List<RespuestaReview> findByReviewId(Integer reviewId);

    List<RespuestaReview> findByUsuarioId(Integer usuarioId);
}
