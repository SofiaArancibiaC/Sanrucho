package cl.sanrucho.notificaciones.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.notificaciones.dto.ConfiguracionNotificacionesRequest;
import cl.sanrucho.notificaciones.dto.ConfiguracionNotificacionesResponse;
import cl.sanrucho.notificaciones.mapper.ConfiguracionNotificacionesMapper;
import cl.sanrucho.notificaciones.model.entity.ConfiguracionNotificaciones;
import cl.sanrucho.notificaciones.repository.ConfiguracionNotificacionesRepository;
import cl.sanrucho.common.exception.*;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ConfiguracionNotificacionesService {
 
    private final ConfiguracionNotificacionesRepository configuracionRepository;
    private final ConfiguracionNotificacionesMapper configuracionMapper;
 
    @Transactional 
    public ConfiguracionNotificacionesResponse crear(ConfiguracionNotificacionesRequest dto) {
        if (configuracionRepository.existsByUsuarioId(dto.getUsuarioId())) {
            throw new IllegalStateException(
                "Ya existe una configuración de notificaciones para el usuarioId: " + dto.getUsuarioId());
        }
 
        ConfiguracionNotificaciones configuracion = configuracionMapper.toEntity(dto);
        ConfiguracionNotificaciones guardada = configuracionRepository.save(configuracion);
        return configuracionMapper.toResponse(guardada);
    }
 
    @Transactional(readOnly = true)
    public ConfiguracionNotificacionesResponse obtenerPorUsuarioId(Integer usuarioId) {
        ConfiguracionNotificaciones configuracion = buscarPorUsuarioId(usuarioId);
        return configuracionMapper.toResponse(configuracion);
    }
 
    @Transactional
    public ConfiguracionNotificacionesResponse actualizar(Integer usuarioId, ConfiguracionNotificacionesRequest dto) {
        ConfiguracionNotificaciones configuracion = buscarPorUsuarioId(usuarioId);
 
        configuracion.setEmailPedidos(dto.getEmailPedidos());
        configuracion.setEmailPromociones(dto.getEmailPromociones());
        configuracion.setEmailNewsletter(dto.getEmailNewsletter());
        configuracion.setPushPedidos(dto.getPushPedidos());
        configuracion.setPushPromociones(dto.getPushPromociones());
        configuracion.setSmsPedidos(dto.getSmsPedidos());
 
        return configuracionMapper.toResponse(configuracionRepository.save(configuracion));
    }
 
    private ConfiguracionNotificaciones buscarPorUsuarioId(Integer usuarioId) {
        return configuracionRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("ID USUARIO", "ID", usuarioId));
    }
}