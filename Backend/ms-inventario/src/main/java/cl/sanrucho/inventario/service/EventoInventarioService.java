package cl.sanrucho.inventario.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.inventario.dto.EventoInventarioRequest;
import cl.sanrucho.inventario.dto.EventoInventarioResponse;
import cl.sanrucho.inventario.mapper.EventoInventarioMapper;
import cl.sanrucho.inventario.model.EventoInventario;
import cl.sanrucho.inventario.repository.EventoInventarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de consulta y administracion de los eventos de inventario.
 *
 * Los eventos se escriben sobre todo desde InventarioService, que los crea en la
 * misma transaccion que el movimiento de stock. Este servicio cubre el ciclo de
 * vida posterior: consultarlos y marcar los pendientes como publicados.
 *
 * Sobre el outbox: hoy marcar como publicado solo actualiza la fecha, porque no
 * hay broker. Cuando se agregue Kafka, el metodo publicarPendientes es el punto
 * exacto donde hay que enviar el evento al topic y recien despues marcarlo, de
 * modo que un fallo en el broker no pierda el evento ni lo duplique.
 *
 * El eventId es la clave de idempotencia: permite reintentar la publicacion sin
 * que el consumidor procese dos veces el mismo hecho.
 */
@Service
@RequiredArgsConstructor
public class EventoInventarioService {

    private static final String ENTIDAD = "Eventos de inventario";

    private final EventoInventarioRepository eventoInventarioRepository;
    private final EventoInventarioMapper eventoInventarioMapper;

    // ─── Consultas ───────────────────────────────────────────────────────────

    public List<EventoInventarioResponse> findAll() {
        return eventoInventarioMapper.toResponseList(eventoInventarioRepository.findAll());
    }

    public EventoInventarioResponse findById(int id) {
        return eventoInventarioMapper.toResponse(getEventoById(id));
    }

    public EventoInventarioResponse findByEventId(String eventId) {
        return eventoInventarioRepository.findByEventId(eventId)
                .map(eventoInventarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(ENTIDAD, "eventId", eventId));
    }

    public List<EventoInventarioResponse> findByProductoId(int productoId) {
        return eventoInventarioMapper.toResponseList(
                eventoInventarioRepository.findByProductoId(productoId));
    }

    public List<EventoInventarioResponse> findPendientes() {
        return eventoInventarioMapper.toResponseList(
                eventoInventarioRepository.findByPublicado(false));
    }

    public List<EventoInventarioResponse> findPublicados() {
        return eventoInventarioMapper.toResponseList(
                eventoInventarioRepository.findByPublicado(true));
    }

    // ─── Alta manual y publicacion ───────────────────────────────────────────

    @Transactional
    public EventoInventarioResponse create(EventoInventarioRequest request) {

        if (eventoInventarioRepository.existsByEventId(request.getEventId())) {
            throw new DuplicateResourceException(ENTIDAD, "eventId", request.getEventId(),
                    request.getSku());
        }

        EventoInventario evento = eventoInventarioMapper.toModel(request);

        evento.setEventId(request.getEventId().trim());
        evento.setFechaEvento(LocalDateTime.now());

        boolean publicado = Boolean.TRUE.equals(request.getPublicado());
        evento.setPublicado(publicado);
        evento.setFechaPublicacion(publicado ? LocalDateTime.now() : null);

        return eventoInventarioMapper.toResponse(eventoInventarioRepository.save(evento));
    }

    @Transactional
    public EventoInventarioResponse marcarPublicado(int id) {
        return eventoInventarioMapper.toResponse(publicar(getEventoById(id)));
    }

    @Transactional
    public List<EventoInventarioResponse> marcarPendientesComoPublicados() {
        List<EventoInventario> pendientes = eventoInventarioRepository.findByPublicado(false);

        for (EventoInventario evento : pendientes) {
            publicar(evento);
        }

        return eventoInventarioMapper.toResponseList(pendientes);
    }

    // ─── Metodos privados auxiliares ─────────────────────────────────────────

    private EventoInventario publicar(EventoInventario evento) {

        // Idempotente: publicar dos veces el mismo evento no cambia la fecha
        // original, asi el consumidor puede usar esa fecha para deduplicar.
        if (Boolean.TRUE.equals(evento.getPublicado())) {
            return evento;
        }

        evento.setPublicado(true);
        evento.setFechaPublicacion(LocalDateTime.now());

        return eventoInventarioRepository.save(evento);
    }

    private EventoInventario getEventoById(int id) {
        return eventoInventarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTIDAD, "ID", id));
    }
}
