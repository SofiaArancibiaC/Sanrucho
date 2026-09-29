package cl.sanrucho.reviews.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.reviews.dto.ReviewRequest;
import cl.sanrucho.reviews.dto.ReviewResponse;
import cl.sanrucho.reviews.mapper.ReviewMapper;
import cl.sanrucho.reviews.model.Review;
import cl.sanrucho.reviews.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewResponse> findAll() {
        return reviewMapper.toResponseList(reviewRepository.findAll());
    }

    public ReviewResponse findById(long id) {
        return reviewMapper.toResponse(getReviewById(id));
    }

    public List<ReviewResponse> findByProductoId(Integer productoId) {
        return reviewMapper.toResponseList(reviewRepository.findByProductoId(productoId));
    }

    public List<ReviewResponse> findByUsuarioId(Integer usuarioId) {
        return reviewMapper.toResponseList(reviewRepository.findByUsuarioId(usuarioId));
    }

    public List<ReviewResponse> findByPedidoId(Integer pedidoId) {
        return reviewMapper.toResponseList(reviewRepository.findByPedidoId(pedidoId));
    }

    public List<ReviewResponse> findByEstado(Review.EstadoReview estado) {
        return reviewMapper.toResponseList(reviewRepository.findByEstado(estado));
    }

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        validateReviewUnica(request.getProductoId(), request.getUsuarioId(), request.getPedidoId());

        Review review = reviewMapper.toEntity(request);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponse update(long id, ReviewRequest request) {
        Review review = getReviewById(id);

        if (!review.getProductoId().equals(request.getProductoId()) ||
            !review.getUsuarioId().equals(request.getUsuarioId()) ||
            !review.getPedidoId().equals(request.getPedidoId())) {
            validateReviewUnica(request.getProductoId(), request.getUsuarioId(), request.getPedidoId());
        }

        reviewMapper.updateEntity(request, review);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteById(long id) {
        Review review = getReviewById(id);
        reviewRepository.delete(review);
    }

    private Review getReviewById(long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reviews", "ID", id));
    }

    private void validateReviewUnica(Integer productoId, Integer usuarioId, Integer pedidoId) {
        reviewRepository.findByProductoIdAndUsuarioIdAndPedidoId(productoId, usuarioId, pedidoId)
                .ifPresent(r -> {
                    throw new DuplicateResourceException(
                            "Review",
                            "productoId/usuarioId/pedidoId",
                            productoId + "/" + usuarioId + "/" + pedidoId,
                            "El usuario ya ha realizado una review para este producto en este pedido"
                    );
                });
    }
}
