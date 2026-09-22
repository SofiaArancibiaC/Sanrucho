package cl.sanrucho.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.reviews.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

List<Review> findByProductoId(Integer productoId);

List<Review> findByUsuarioId(Integer usuarioId);

List<Review> findByPedidoId(Integer pedidoId);

List<Review> findByEstado(Review.EstadoReview estado);

Optional<Review> findById(Integer id);

}
