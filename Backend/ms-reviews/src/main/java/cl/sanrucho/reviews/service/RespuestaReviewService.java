package cl.sanrucho.reviews.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.reviews.dto.RespuestaReviewRequest;
import cl.sanrucho.reviews.dto.RespuestaReviewResponse;
import cl.sanrucho.reviews.mapper.RespuestaReviewMapper;
import cl.sanrucho.reviews.model.RespuestaReview;
import cl.sanrucho.reviews.repository.RespuestaReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RespuestaReviewService {

    private final RespuestaReviewRepository respuestaRepository;
    private final RespuestaReviewMapper respuestaMapper;

    public List<RespuestaReviewResponse> findAll() {
        return respuestaMapper.toResponseList(respuestaRepository.findAll());
    }

    public RespuestaReviewResponse findById(long id) {
        return respuestaMapper.toResponse(getRespuestaById(id));
    }

    public List<RespuestaReviewResponse> findByReviewId(Integer reviewId) {
        return respuestaMapper.toResponseList(respuestaRepository.findByReviewId(reviewId));
    }

    @Transactional
    public RespuestaReviewResponse create(RespuestaReviewRequest request) {
        RespuestaReview respuesta = respuestaMapper.toEntity(request);
        return respuestaMapper.toResponse(respuestaRepository.save(respuesta));
    }

    @Transactional
    public RespuestaReviewResponse update(long id, RespuestaReviewRequest request) {
        RespuestaReview respuesta = getRespuestaById(id);
        respuestaMapper.updateEntity(request, respuesta);
        return respuestaMapper.toResponse(respuestaRepository.save(respuesta));
    }

    @Transactional
    public void deleteById(long id) {
        RespuestaReview respuesta = getRespuestaById(id);
        respuestaRepository.delete(respuesta);
    }

    private RespuestaReview getRespuestaById(long id) {
        return respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Respuestas Reviews", "ID", id));
    }
}
