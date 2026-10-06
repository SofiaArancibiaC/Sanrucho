package cl.sanrucho.catalogo.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import cl.sanrucho.catalogo.model.entity.Producto;
import cl.sanrucho.catalogo.model.enums.EstadoProducto;

public interface ProductoRepository  extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto>{
        Optional<Producto> findBySku(String sku);

        boolean existsBySku(String sku);

        Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);

        Page<Producto> findByPersonajeIgnoreCase(String personaje, Pageable pageable);

        Page<Producto> findByCategoriaIgnoreCase(String categoria, Pageable pageable);

        @Query("SELECT p FROM Producto p WHERE " +
        "(:personaje IS NULL OR LOWER(p.personaje) = LOWER(:personaje)) AND " +
        "(:categoria IS NULL OR LOWER(p.categoria) = LOWER(:categoria)) AND " +
        "(:estado IS NULL OR p.estado = :estado) AND " +
        "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))")
        Page<Producto> buscarConFiltros(@Param("personaje") String personaje,
                                @Param("categoria") String categoria,
                                @Param("estado") EstadoProducto estado,
                                @Param("nombre") String nombre,
                                Pageable pageable);

        
        List<Producto> findByEstadoNot(EstadoProducto estado);

}
