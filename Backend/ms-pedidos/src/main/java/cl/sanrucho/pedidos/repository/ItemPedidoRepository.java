package cl.sanrucho.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pedidos.model.entity.ItemPedido;
import java.util.List;


public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer>{

    List<ItemPedido> findByProductoId(Integer productoId);

}
