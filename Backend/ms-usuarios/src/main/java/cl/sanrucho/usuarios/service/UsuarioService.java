package cl.sanrucho.usuarios.service;

import cl.sanrucho.usuarios.dto.UsuarioRequest;
import cl.sanrucho.usuarios.dto.UsuarioResponse;
import cl.sanrucho.usuarios.model.Usuario;
import cl.sanrucho.usuarios.mapper.UsuarioMapper;
import cl.sanrucho.usuarios.repository.UsuarioRepository;
//confirmar si es así
import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

private final UsuarioRepository usuarioRepository;
private final UsuarioMapper usuarioMapper;

public List<UsuarioResponse> findAll() {
    log.info("Obteniendo lista de usuarios");

    return usuarioMapper.toResponseList(usuarioRepository.findAll());
}

public UsuarioResponse findById(Integer id) {
    log.info("Buscando usuario con ID {}", id);

    Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new EntityNotFoundException("Usuario", "id", id));

    return usuarioMapper.toResponse(usuario);
}

public UsuarioResponse findByEmail(String email) {
    log.info("Buscando usuario con email {}", email);

    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuario", "email", email));

    return usuarioMapper.toResponse(usuario);
}

public UsuarioResponse create(UsuarioRequest request) {
    log.info("Intentando crear usuario con email {}", request.getEmail());

    if (usuarioRepository.existsByEmail(request.getEmail())) {
        log.warn("Intento de registro duplicado para email {}", request.getEmail());

        throw new DuplicateResourceException(
                "Usuario", "email", request.getEmail(), "usuario registrado");
    }

    Usuario usuario = usuarioMapper.toEntity(request);

    Usuario guardado = usuarioRepository.save(Objects.requireNonNull(usuario));

    log.info("Usuario creado correctamente con ID {}", guardado.getId());

    return usuarioMapper.toResponse(guardado);
}

public UsuarioResponse update(Integer id, UsuarioRequest request) {
    log.info("Intentando actualizar usuario con ID {}", id);

    Usuario existente = usuarioRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new EntityNotFoundException("Usuario", "id", id));

    if (!existente.getEmail().equalsIgnoreCase(request.getEmail())) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn(
                    "No se puede actualizar usuario {} porque el email {} ya existe",
                    id,
                    request.getEmail());

            throw new DuplicateResourceException(
                    "Usuario", "email", request.getEmail(), "usuario registrado");
        }
    }

    existente.setEmail(request.getEmail());
    existente.setNombre(request.getNombre());
    existente.setApellido(request.getApellido());
    existente.setTelefono(request.getTelefono());
    existente.setEstado(request.getEstado());
    existente.setPersonajeFavorito(request.getPersonajeFavorito());
    existente.setNotificacionesEmail(request.getNotificacionesEmail());
    existente.setNotificacionesPush(request.getNotificacionesPush());
    existente.setUpdatedBy(request.getUpdatedBy());

    Usuario actualizado = usuarioRepository.save(existente);

    log.info("Usuario actualizado correctamente con ID {}", id);

    return usuarioMapper.toResponse(actualizado);
}

public void deleteById(Integer id) {
    log.info("Intentando eliminar usuario con ID {}", id);

    Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new EntityNotFoundException("Usuario", "id", id));

    usuarioRepository.delete(usuario);

    log.info("Usuario eliminado correctamente con ID {}", id);
}

}

