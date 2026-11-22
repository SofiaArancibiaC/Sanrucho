package cl.sanrucho.pedidos.repository.detallepedidos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByIdPedido(Integer idPedido);

    void deleteByIdPedido(Integer idPedido);

    long countByIdPedido(Integer idPedido);
}
