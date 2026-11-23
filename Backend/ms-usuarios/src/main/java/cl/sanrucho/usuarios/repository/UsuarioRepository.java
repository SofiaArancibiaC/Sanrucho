package cl.sanrucho.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.usuarios.model.Usuario;
import java.util.List;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    public List<Usuario> findByEmail(String email);

    public Boolean existsByEmail(String email);

    public java.util.Optional<Usuario> findByRun(String run);

    public Boolean existsByRun(String run);
}
