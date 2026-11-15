package cl.sanrucho.catalogo.repository.categorias;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.catalogo.model.entity.categorias.Categoria;

/**
 * Repositorio JPA de la base de datos "categorias".
 * Es el datasource secundario del microservicio.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);

    Page<Categoria> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    java.util.Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
