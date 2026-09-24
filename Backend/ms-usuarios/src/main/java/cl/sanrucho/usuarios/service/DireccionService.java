package cl.sanrucho.usuarios.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.usuarios.dto.DireccionRequest;
import cl.sanrucho.usuarios.dto.DireccionResponse;
import cl.sanrucho.usuarios.mapper.DireccionMapper;
import cl.sanrucho.usuarios.model.Direccion;
import cl.sanrucho.usuarios.model.Usuario;
import cl.sanrucho.usuarios.repository.DireccionRepository;
import cl.sanrucho.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de la logica de negocio de direcciones.
 *
 * La direccion siempre pertenece a un usuario existente: el service resuelve
 * la relacion a partir del usuarioId del request antes de persistir.
 */
@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionMapper direccionMapper;

    public List<DireccionResponse> findAll() {
        return direccionMapper.toResponseList(direccionRepository.findAll());
    }

    public DireccionResponse findById(int id) {
        return direccionMapper.toResponse(getDireccionById(id));
    }

    public List<DireccionResponse> findByUsuarioId(int usuarioId) {
        return direccionMapper.toResponseList(direccionRepository.findByUsuarioId(usuarioId));
    }

    @Transactional
    public DireccionResponse create(DireccionRequest request) {
        Direccion direccion = direccionMapper.toModel(request);
        direccion.setUsuario(getUsuarioById(request.getUsuarioId()));
        direccion.setCreatedAt(LocalDateTime.now());
        if (direccion.getPais() == null || direccion.getPais().isBlank()) {
            direccion.setPais("Chile");
        }
        if (direccion.getVersion() == null) {
            direccion.setVersion(1);
        }

        Direccion guardada = direccionRepository.save(direccion);
        return direccionMapper.toResponse(guardada);
    }

    @Transactional
    public DireccionResponse update(int id, DireccionRequest request) {
        Direccion direccion = getDireccionById(id);

        // Si el request apunta a otro usuario, se reasigna la relacion
        if (!direccion.getUsuario().getId().equals(request.getUsuarioId())) {
            direccion.setUsuario(getUsuarioById(request.getUsuarioId()));
        }

        // Dirty Checking: JPA detecta los cambios y genera el UPDATE solo
        direccionMapper.updateEntity(request, direccion);

        return direccionMapper.toResponse(direccionRepository.save(direccion));
    }

    @Transactional
    public void deleteById(int id) {
        Direccion direccion = getDireccionById(id);
        direccionRepository.delete(direccion);
    }

    // ─── Metodos privados auxiliares ─────────────────────────────────────────

    private Direccion getDireccionById(int id) {
        return direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una direccion con id: " + id));
    }

    private Usuario getUsuarioById(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con id: " + id));
    }
}
