package cl.sanrucho.pedidos.repository.detallepedidos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;

/**
 * Repositorio JPA de la base de datos "detalle_pedidos" (datasource secundario).
 */
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByIdPedido(Integer idPedido);

    @Modifying
    @Query("delete from DetallePedido d where d.idPedido = :idPedido")
    int eliminarPorPedido(@Param("idPedido") Integer idPedido);
}
