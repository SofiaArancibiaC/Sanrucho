package cl.sanrucho.catalogo.repository.productos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.sanrucho.catalogo.model.entity.productos.Producto;
import jakarta.persistence.LockModeType;

/**
 * Repositorio JPA de la base de datos "productos".
 * Es el datasource primario del microservicio.
 */
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Integer id);

    Optional<Producto> findByCodigoIgnoreCase(String codigo);

    /**
     * Recupera el producto aplicando un bloqueo pesimista de escritura.
     * Se usa en el ajuste de stock para evitar sobreventa por concurrencia.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Integer id);

    boolean existsByIdCategoria(Integer idCategoria);

    long countByIdCategoria(Integer idCategoria);

    List<Producto> findByIdCategoria(Integer idCategoria);

    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Producto> findByIdCategoria(Integer idCategoria, Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCaseAndIdCategoria(String nombre, Integer idCategoria, Pageable pageable);
}
