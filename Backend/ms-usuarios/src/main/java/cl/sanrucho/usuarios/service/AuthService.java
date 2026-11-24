package cl.sanrucho.usuarios.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.sanrucho.usuarios.dto.AuthResponse;
import cl.sanrucho.usuarios.dto.LoginRequest;
import cl.sanrucho.usuarios.dto.UsuarioRequest;
import cl.sanrucho.usuarios.dto.UsuarioResponse;
import cl.sanrucho.usuarios.model.Usuario;
import cl.sanrucho.usuarios.repository.UsuarioRepository;
import cl.sanrucho.usuarios.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final cl.sanrucho.usuarios.mapper.UsuarioMapper mapper;

    public AuthService(UsuarioRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,
            UsuarioService usuarioService, cl.sanrucho.usuarios.mapper.UsuarioMapper mapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.mapper = mapper;
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = repository.findByEmail(request.getEmail()).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        String token = jwtService.generateToken(
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().name() : "CLIENTE",
                usuario.getIdUsuario());
        return AuthResponse.builder().token(token).usuario(mapper.toResponse(usuario)).build();
    }

    public AuthResponse register(UsuarioRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        Usuario usuario = mapper.toEntity(request);
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        if (usuario.getEstado() == null) {
            usuario.setEstado("Activo");
        }
        Usuario saved = repository.save(usuario);
        String token = jwtService.generateToken(
                saved.getEmail(),
                saved.getRol() != null ? saved.getRol().name() : "CLIENTE",
                saved.getIdUsuario());
        return AuthResponse.builder().token(token).usuario(mapper.toResponse(saved)).build();
    }
}
