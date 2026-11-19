package cl.sanrucho.usuarios.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.sanrucho.usuarios.dto.UsuarioRequest;
import cl.sanrucho.usuarios.dto.UsuarioResponse;
import cl.sanrucho.usuarios.mapper.UsuarioMapper;
import cl.sanrucho.usuarios.model.Usuario;
import cl.sanrucho.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public UsuarioResponse findById(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapper.toResponse(usuario);
    }

    public UsuarioResponse findByRun(String run) {
        Usuario usuario = repository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapper.toResponse(usuario);
    }

    public UsuarioResponse create(UsuarioRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (request.getRun() != null && repository.existsByRun(request.getRun())) {
            throw new RuntimeException("El RUN ya está registrado");
        }
        Usuario usuario = mapper.toEntity(request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (usuario.getEstado() == null) {
            usuario.setEstado("Activo");
        }
        return mapper.toResponse(repository.save(usuario));
    }

    public UsuarioResponse update(Integer id, UsuarioRequest request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setRun(request.getRun());
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setRegionId(request.getRegionId());
        usuario.setComunaId(request.getComunaId());
        usuario.setDireccion(request.getDireccion());
        usuario.setTelefono(request.getTelefono());
        usuario.setEstado(request.getEstado());
        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }
        return mapper.toResponse(repository.save(usuario));
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        repository.deleteById(id);
    }

    public void deleteByRun(String run) {
        Usuario usuario = repository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        repository.delete(usuario);
    }
}
