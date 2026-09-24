package cl.sanrucho.usuarios.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.usuarios.dto.EventoUsuarioRequest;
import cl.sanrucho.usuarios.dto.EventoUsuarioResponse;
import cl.sanrucho.usuarios.mapper.EventoUsuarioMapper;
import cl.sanrucho.usuarios.model.EventoUsuario;
import cl.sanrucho.usuarios.model.Usuario;
import cl.sanrucho.usuarios.repository.EventoUsuarioRepository;
import cl.sanrucho.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de la logica de negocio de eventos de usuario.
 *
 * Cada evento tiene un eventId unico (clave de negocio) y pertenece a un
 * usuario existente, resuelto desde el usuarioId del request.
 */
@Service
@RequiredArgsConstructor
public class EventoUsuarioService {

    private final EventoUsuarioRepository eventoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoUsuarioMapper eventoUsuarioMapper;

    public List<EventoUsuarioResponse> findAll() {
        return eventoUsuarioMapper.toResponseList(eventoUsuarioRepository.findAll());
    }

    public EventoUsuarioResponse findById(int id) {
        return eventoUsuarioMapper.toResponse(getEventoById(id));
    }

    public List<EventoUsuarioResponse> findByUsuarioId(int usuarioId) {
        return eventoUsuarioMapper.toResponseList(eventoUsuarioRepository.findByUsuarioId(usuarioId));
    }

    @Transactional
    public EventoUsuarioResponse create(EventoUsuarioRequest request) {
        if (eventoUsuarioRepository.existsByEventId(request.getEventId())) {
            throw new RuntimeException("Ya existe un evento con eventId: " + request.getEventId());
        }

        EventoUsuario evento = eventoUsuarioMapper.toModel(request);
        evento.setUsuario(getUsuarioById(request.getUsuarioId()));
        evento.setFechaEvento(LocalDateTime.now());
        if (evento.getPublicado() == null) {
            evento.setPublicado(false);
        }
        if (evento.getPublicado() && evento.getFechaPublicacion() == null) {
            evento.setFechaPublicacion(LocalDateTime.now());
        }

        EventoUsuario guardado = eventoUsuarioRepository.save(evento);
        return eventoUsuarioMapper.toResponse(guardado);
    }

    @Transactional
    public EventoUsuarioResponse update(int id, EventoUsuarioRequest request) {
        EventoUsuario evento = getEventoById(id);

        // Si el request apunta a otro usuario, se reasigna la relacion
        if (!evento.getUsuario().getId().equals(request.getUsuarioId())) {
            evento.setUsuario(getUsuarioById(request.getUsuarioId()));
        }

        // Dirty Checking: JPA detecta los cambios y genera el UPDATE solo
        eventoUsuarioMapper.updateEntity(request, evento);

        if (evento.getPublicado() && evento.getFechaPublicacion() == null) {
            evento.setFechaPublicacion(LocalDateTime.now());
        }

        return eventoUsuarioMapper.toResponse(eventoUsuarioRepository.save(evento));
    }

    @Transactional
    public void deleteById(int id) {
        EventoUsuario evento = getEventoById(id);
        eventoUsuarioRepository.delete(evento);
    }

    // ─── Metodos privados auxiliares ─────────────────────────────────────────

    private EventoUsuario getEventoById(int id) {
        return eventoUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un evento con id: " + id));
    }

    private Usuario getUsuarioById(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con id: " + id));
    }
}
