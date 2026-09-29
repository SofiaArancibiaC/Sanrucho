package cl.sanrucho.reviews.repository;

import cl.sanrucho.reviews.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductoId(Integer productoId);

    List<Review> findByUsuarioId(Integer usuarioId);

    List<Review> findByPedidoId(Integer pedidoId);

    List<Review> findByEstado(Review.EstadoReview estado);

    Optional<Review> findByProductoIdAndUsuarioIdAndPedidoId(Integer productoId, Integer usuarioId, Integer pedidoId);
}
