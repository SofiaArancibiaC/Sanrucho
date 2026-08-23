package cl.sanrucho.carrito.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.carrito.model.entity.Carrito;
import cl.sanrucho.carrito.model.entity.Carrito.EstadoCarrito;




public interface CarritoRepository extends JpaRepository<Carrito, Integer>{

    Optional<Carrito> findByUsuarioId(Integer usuarioId);


    Boolean existsByUsuarioId(Integer usuarioId);

    Optional<Carrito> findByEstado(EstadoCarrito estado);

}