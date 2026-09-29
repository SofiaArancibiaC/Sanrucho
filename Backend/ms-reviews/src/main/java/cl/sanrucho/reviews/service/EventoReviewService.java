package cl.sanrucho.reviews.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.reviews.dto.EventoReviewRequest;
import cl.sanrucho.reviews.dto.EventoReviewResponse;
import cl.sanrucho.reviews.mapper.EventoReviewMapper;
import cl.sanrucho.reviews.model.EventoReview;
import cl.sanrucho.reviews.repository.EventoReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoReviewService {

    private final EventoReviewRepository eventoRepository;
    private final EventoReviewMapper eventoMapper;

    public List<EventoReviewResponse> findAll() {
        return eventoMapper.toResponseList(eventoRepository.findAll());
    }

    public EventoReviewResponse findById(long id) {
        return eventoMapper.toResponse(getEventoById(id));
    }

    public EventoReviewResponse findByEventId(String eventId) {
        return eventoMapper.toResponse(eventoRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Eventos Review", "event_id", eventId)));
    }

    public List<EventoReviewResponse> findByReviewId(Integer reviewId) {
        return eventoMapper.toResponseList(eventoRepository.findByReviewId(reviewId));
    }

    public List<EventoReviewResponse> findByProductoId(Integer productoId) {
        return eventoMapper.toResponseList(eventoRepository.findByProductoId(productoId));
    }

    public List<EventoReviewResponse> findByPublicado(Boolean publicado) {
        return eventoMapper.toResponseList(eventoRepository.findByPublicado(publicado));
    }

    public List<EventoReviewResponse> findByTipoEvento(EventoReview.TipoEvento tipoEvento) {
        return eventoMapper.toResponseList(eventoRepository.findByTipoEvento(tipoEvento));
    }

    @Transactional
    public EventoReviewResponse create(EventoReviewRequest request) {
        validateEventIdUnico(request.getEventId());

        EventoReview evento = eventoMapper.toEntity(request);
        return eventoMapper.toResponse(eventoRepository.save(evento));
    }

    @Transactional
    public EventoReviewResponse update(long id, EventoReviewRequest request) {
        EventoReview evento = getEventoById(id);

        if (!evento.getEventId().equals(request.getEventId())) {
            validateEventIdUnico(request.getEventId());
        }

        eventoMapper.updateEntity(request, evento);
        return eventoMapper.toResponse(eventoRepository.save(evento));
    }

    @Transactional
    public void deleteById(long id) {
        EventoReview evento = getEventoById(id);
        eventoRepository.delete(evento);
    }

    private EventoReview getEventoById(long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Eventos Review", "ID", id));
    }

    private void validateEventIdUnico(String eventId) {
        eventoRepository.findByEventId(eventId).ifPresent(e -> {
            throw new DuplicateResourceException(
                    "Evento Review",
                    "event_id",
                    eventId,
                    "El evento ya existe en el sistema"
            );
        });
    }
}
