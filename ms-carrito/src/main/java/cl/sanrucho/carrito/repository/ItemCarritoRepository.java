package cl.sanrucho.carrito.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.carrito.model.entity.ItemCarrito;


public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Integer>{

    Optional<ItemCarrito> findByProductoId(Integer id);

    Optional<ItemCarrito> findByCarritoIdAndProductoId(Integer carritoId, Integer productoId);

    void deleteByCarritoId(Integer carritoId);


}
