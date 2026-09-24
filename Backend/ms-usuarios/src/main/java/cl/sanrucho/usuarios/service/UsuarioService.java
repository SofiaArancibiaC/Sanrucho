package cl.sanrucho.usuarios.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.usuarios.dto.UsuarioRequest;
import cl.sanrucho.usuarios.dto.UsuarioResponse;
import cl.sanrucho.usuarios.mapper.UsuarioMapper;
import cl.sanrucho.usuarios.model.Usuario;
import cl.sanrucho.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de la logica de negocio de usuarios.
 *
 * Gestiona operaciones CRUD sobre PostgreSQL mediante JPA:
 * - El ID lo genera la base de datos (@GeneratedValue).
 * - La actualizacion busca la entidad existente, la modifica y JPA
 *   envia el UPDATE automatico al terminar el metodo (@Transactional).
 * - Al eliminar un usuario, las direcciones y eventos asociados se
 *   eliminan en cascada gracias a la relacion cascade = ALL de la entidad.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public List<UsuarioResponse> findAll() {
        return usuarioMapper.toResponseList(usuarioRepository.findAll());
    }

    public UsuarioResponse findById(int id) {
        return usuarioMapper.toResponse(getUsuarioById(id));
    }

    public Optional<UsuarioResponse> findByEmail(String email) {
        return usuarioRepository.findByEmail(email).map(usuarioMapper::toResponse);
    }

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + request.getEmail());
        }

        Usuario usuario = usuarioMapper.toModel(request);
        // Valores por defecto que no vienen en el request
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setCreatedAt(LocalDateTime.now());
        if (usuario.getEstado() == null) {
            usuario.setEstado(Usuario.EstadoUsuario.activo);
        }
        if (usuario.getNotificacionesEmail() == null) {
            usuario.setNotificacionesEmail(true);
        }
        if (usuario.getNotificacionesPush() == null) {
            usuario.setNotificacionesPush(true);
        }
        if (usuario.getVersion() == null) {
            usuario.setVersion(1);
        }
        if (usuario.getDirecciones() == null) {
            usuario.setDirecciones(new java.util.ArrayList<>());
        }
        if (usuario.getEventos() == null) {
            usuario.setEventos(new java.util.ArrayList<>());
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(guardado);
    }

    @Transactional
    public UsuarioResponse update(int id, UsuarioRequest request) {
        Usuario usuario = getUsuarioById(id);

        // Valida unicidad del email solo si cambio
        if (!usuario.getEmail().equalsIgnoreCase(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + request.getEmail());
        }

        // Vuelca el request sobre la entidad gestionada por JPA; al terminar el
        // metodo transaccionado, el Dirty Checking genera el UPDATE solo.
        usuarioMapper.updateEntity(request, usuario);
        usuario.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : request.getCreatedBy());

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deleteById(int id) {
        Usuario usuario = getUsuarioById(id);
        usuarioRepository.delete(usuario);
    }

    // ─── Metodos privados auxiliares ─────────────────────────────────────────

    private Usuario getUsuarioById(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con id: " + id));
    }
}
