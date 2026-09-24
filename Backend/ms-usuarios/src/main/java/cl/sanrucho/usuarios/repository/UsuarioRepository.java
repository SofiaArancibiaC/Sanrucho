package cl.sanrucho.usuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.usuarios.model.Usuario;

/**
 * Repositorio JPA de la entidad Usuario.
 *
 * Al extender de JpaRepository ya no usamos una lista en memoria:
 * Spring genera el SQL y los datos persisten de forma real en PostgreSQL.
 *
 * Los metodos findByEmail / existsByEmail son "Query Methods":
 * Spring lee el nombre del metodo y construye la consulta por nosotros,
 * sin escribir una linea de SQL manual.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
