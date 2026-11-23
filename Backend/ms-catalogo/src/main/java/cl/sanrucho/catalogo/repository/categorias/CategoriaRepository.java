package cl.sanrucho.catalogo.repository.categorias;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.catalogo.model.entity.categorias.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);

    Page<Categoria> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
