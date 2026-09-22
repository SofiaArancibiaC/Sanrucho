package cl.sanrucho.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.inventario.model.Inventario;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByProductoId(Integer productoId);

    Optional<Inventario> findBySku(String sku);

    boolean existsByProductoId(Integer productoId);

    boolean existsBySku(String sku);
}