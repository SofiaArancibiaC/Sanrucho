package cl.sanrucho.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.sanrucho.inventario.model.MovimientoInventario;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByInventarioId(Integer inventarioId);
}