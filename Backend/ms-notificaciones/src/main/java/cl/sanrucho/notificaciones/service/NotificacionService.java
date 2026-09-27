package cl.sanrucho.notificaciones.service;

import java.time.LocalDateTime;
import java.util.List;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import cl.sanrucho.notificaciones.dto.NotificacionRequest;
import cl.sanrucho.notificaciones.dto.NotificacionResponse;
import cl.sanrucho.notificaciones.model.entity.Notificacion;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.notificaciones.mapper.NotificacionMapper;
import cl.sanrucho.notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class NotificacionService {
 
    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;
 
    @Transactional
    public NotificacionResponse crear(NotificacionRequest dto) {
        Notificacion notificacion = notificacionMapper.toEntity(dto);
 
        if (notificacion.getPrioridad() == null) {
            notificacion.setPrioridad("normal");
        }
        notificacion.setEstado("pendiente");
 
        Notificacion guardada = notificacionRepository.save(notificacion);
        return notificacionMapper.toResponse(guardada);
    }
 
    @Transactional(readOnly = true)
    public NotificacionResponse obtenerPorId(Integer id) {
        Notificacion notificacion = buscarPorId(id);
        return notificacionMapper.toResponse(notificacion);
    }
 
    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarPorUsuario(Integer usuarioId) {
        return notificacionMapper.toResponseList(notificacionRepository.findByUsuarioId(usuarioId));
    }
 
    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarPorEstado(String estado) {
        return notificacionMapper.toResponseList(notificacionRepository.findByEstado(estado));
    }
 
    @Transactional
    public NotificacionResponse marcarComoEnviada(Integer id) {
        Notificacion notificacion = buscarPorId(id);
        notificacion.setEstado("enviada");
        notificacion.setFechaEnvio(LocalDateTime.now());
        return notificacionMapper.toResponse(notificacionRepository.save(notificacion));
    }
 
    @Transactional
    public NotificacionResponse marcarComoLeida(Integer id) {
        Notificacion notificacion = buscarPorId(id);
        notificacion.setEstado("leida");
        notificacion.setFechaLectura(LocalDateTime.now());
        return notificacionMapper.toResponse(notificacionRepository.save(notificacion));
    }
 
    @Transactional
    public NotificacionResponse marcarComoFallida(Integer id) {
        Notificacion notificacion = buscarPorId(id);
        notificacion.setEstado("fallida");
        return notificacionMapper.toResponse(notificacionRepository.save(notificacion));
    }
 
    @Transactional
    public void eliminar(Integer id) {
        Notificacion notificacion = buscarPorId(id);
        notificacionRepository.delete(notificacion);
    }
 
    private Notificacion buscarPorId(Integer id) {
        return notificacionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ID NOTIFICACION", "ID", id));
    }
}
 