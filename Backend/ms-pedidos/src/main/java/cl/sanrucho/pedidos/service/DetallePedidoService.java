package cl.sanrucho.pedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;
import cl.sanrucho.pedidos.repository.detallepedidos.DetallePedidoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    @Transactional(readOnly = true)
    public List<DetallePedido> listarPorPedido(Integer idPedido) {
        return detallePedidoRepository.findByIdPedido(idPedido);
    }

    public List<DetallePedido> guardarTodos(List<DetallePedido> detalles) {
        return detallePedidoRepository.saveAll(detalles);
    }

    public void eliminarPorPedido(Integer idPedido) {
        detallePedidoRepository.deleteByIdPedido(idPedido);
    }
}
