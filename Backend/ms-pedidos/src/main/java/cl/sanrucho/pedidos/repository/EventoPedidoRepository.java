package cl.sanrucho.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sanrucho.pedidos.model.entity.EventoPedido;

public interface EventoPedidoRepository extends JpaRepository<EventoPedido, Integer>{

    List<EventoPedido> findByPublicadoFalse();

    List<EventoPedido> findByPedidoIdOrderByFechaEventoAsc(Integer pedidoId);
}
