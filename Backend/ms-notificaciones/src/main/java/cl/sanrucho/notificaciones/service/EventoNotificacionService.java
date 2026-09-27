package cl.sanrucho.notificaciones.service;
import java.time.LocalDateTime;
import java.util.List;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import cl.sanrucho.notificaciones.dto.EventoNotificacionRequest;
import cl.sanrucho.notificaciones.dto.EventoNotificacionResponse;
import cl.sanrucho.notificaciones.model.entity.EventoNotificacion;
import cl.sanrucho.common.exception.*;
import cl.sanrucho.notificaciones.mapper.EventoNotificacionMapper;
import cl.sanrucho.notificaciones.repository.EventoNotificacionRepository;
import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class EventoNotificacionService {
 
    private final EventoNotificacionRepository eventoRepository;
    private final EventoNotificacionMapper eventoMapper;
 
    @Transactional
    public EventoNotificacionResponse crear(EventoNotificacionRequest dto) {
        if (eventoRepository.existsByEventId(dto.getEventId())) {
            throw new IllegalStateException("Ya existe un evento registrado con eventId: " + dto.getEventId());
        }
 
        EventoNotificacion evento = eventoMapper.toEntity(dto);
        evento.setPublicado(false);
 
        EventoNotificacion guardado = eventoRepository.save(evento);
        return eventoMapper.toResponseDTO(guardado);
    }
 
    @Transactional(readOnly = true)
    public List<EventoNotificacionResponse> listarPendientes() {
        return eventoMapper.toResponseDTOList(eventoRepository.findByPublicado(false));
    }
 
    @Transactional(readOnly = true)
    public List<EventoNotificacionResponse> listarPorNotificacion(Integer notificacionId) {
        return eventoMapper.toResponseDTOList(eventoRepository.findByNotificacionId(notificacionId));
    }
 
    @Transactional
    public EventoNotificacionResponse marcarComoPublicado(Integer id) {
        EventoNotificacion evento = eventoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ID EVENTO", "ID", id));
 
        evento.setPublicado(true);
        evento.setFechaPublicacion(LocalDateTime.now());
 
        return eventoMapper.toResponseDTO(eventoRepository.save(evento));
    }
}