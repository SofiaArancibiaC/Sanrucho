package cl.sanrucho.pedidos.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.pedidos.model.entity.EventoPedido;
import cl.sanrucho.pedidos.model.entity.Pedido;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import cl.sanrucho.pedidos.model.enums.TipoEvento;
import cl.sanrucho.pedidos.repository.EventoPedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Transactional
@Service
public class EventoPedidoService {

    private final EventoPedidoRepository eventoPedidoRepository;

    // private final KafkaTemplate<String, String> kafkaTemplate; // cuando se integre Kafka

    @SneakyThrows
    public void registrarEvento(Pedido pedido, EstadoPedido estadoPedido){
        TipoEvento tipoEvento = mapearTipoEvento(estadoPedido);

        EventoPedido evento = EventoPedido.builder()
            .eventId("evt_ped_" + UUID.randomUUID())
            .pedidoId(pedido.getId())
            .numeroPedido(pedido.getNumeroPedido())
            .tipoEvento(tipoEvento)
            .publicado(false)
            .build();

        eventoPedidoRepository.save(evento);

        // publicarAKafka(evento); // marca publicado=true + fechaPublicacion al confirmar el envío
    }

    private TipoEvento mapearTipoEvento(EstadoPedido estadoPedido){
        return switch (estadoPedido) {
            case PENDIENTE -> TipoEvento.PEDIDO_CREADO;
            case CONFIRMADO -> TipoEvento.PEDIDO_CONFIRMADO;
            case PROCESANDO -> TipoEvento.PEDIDO_PROCESANDO;
            case ENVIADO -> TipoEvento.PEDIDO_ENVIADO;
            case ENTREGADO -> TipoEvento.PEDIDO_ENTREGADO;
            case CANCELADO -> TipoEvento.PEDIDO_CANCELADO;
            case DEVUELTO -> TipoEvento.PEDIDO_DEVUELTO;
        };
    }


}
