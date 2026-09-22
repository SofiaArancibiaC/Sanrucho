package cl.sanrucho.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.reviews.*;

import java.util.List;

@Repository
public interface RespuestaReviewRepository extends JpaRepository<RespuestaReview, Integer> {

List<RespuestaReview> findByReviewId(Integer reviewId);

List<RespuestaReview> findByUsuarioId(Integer usuarioId);

}
