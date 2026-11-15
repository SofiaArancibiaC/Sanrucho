package cl.sanrucho.pedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;
import cl.sanrucho.pedidos.repository.detallepedidos.DetallePedidoRepository;
import lombok.RequiredArgsConstructor;

/**
 * Operaciones sobre la base de datos "detalle_pedidos".
 *
 * Se aislaron en un servicio propio para que cada operacion use su propio
 * transaction manager. Como detalle_pedidos vive en otra base de datos que
 * pedidos, no es posible una transaccion ACID unica entre ambas: la
 * consistencia se logra con compensaciones en PedidoService.
 */
@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    @Transactional(transactionManager = "detallePedidosTransactionManager", readOnly = true)
    public List<DetallePedido> listarPorPedido(Integer idPedido) {
        return detallePedidoRepository.findByIdPedido(idPedido);
    }

    @Transactional(transactionManager = "detallePedidosTransactionManager")
    public List<DetallePedido> guardarTodos(List<DetallePedido> detalles) {
        return detallePedidoRepository.saveAll(detalles);
    }

    @Transactional(transactionManager = "detallePedidosTransactionManager")
    public void eliminarPorPedido(Integer idPedido) {
        detallePedidoRepository.eliminarPorPedido(idPedido);
    }
}
