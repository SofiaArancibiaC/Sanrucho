package cl.sanrucho.pedidos.repository.pedidos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pedidos.model.entity.pedidos.Pedido;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;

/**
 * Repositorio JPA de la base de datos "pedidos" (datasource primario).
 */
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    Page<Pedido> findByIdUsuario(Integer idUsuario, Pageable pageable);

    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);

    Page<Pedido> findByIdUsuarioAndEstado(Integer idUsuario, EstadoPedido estado, Pageable pageable);
}
