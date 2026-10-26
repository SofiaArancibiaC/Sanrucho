package cl.sanrucho.pedidos.service;

import java.time.Year;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.pedidos.dto.EstadoUpdateRequest;
import cl.sanrucho.pedidos.dto.ItemPedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.dto.ResumenPedidoResponse;
import cl.sanrucho.pedidos.mapper.ItemPedidoMapper;
import cl.sanrucho.pedidos.mapper.PedidoMapper;
import cl.sanrucho.pedidos.model.entity.ItemPedido;
import cl.sanrucho.pedidos.model.entity.Pedido;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import cl.sanrucho.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final ItemPedidoMapper itemPedidoMapper;
    private final EventoPedidoService eventoPedidoService;

    public PedidoResponse crear(PedidoRequest request){
        Pedido pedido = pedidoMapper.toEntity(request);

        int subtotal = 0;
        for (ItemPedidoRequest itemRequest : request.getItems()) {
            ItemPedido item = itemPedidoMapper.toEntity(itemRequest);
            int itemSubtotal = item.getPrecioUnitario() * item.getCantidad();
            item.setSubtotal(itemSubtotal);
            subtotal += itemSubtotal;
            pedido.addItem(item);
        }

        int descuento = request.getDescuento() != null ? request.getDescuento() : 0;

        pedido.setSubtotal(subtotal);
        pedido.setTotal(subtotal - descuento + request.getCostoEnvio());
        pedido.setDescuento(descuento);
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setCreatedBy(request.getNombreUsuario());

        Pedido guardado = pedidoRepository.save(pedido);

        eventoPedidoService.registrarEvento(guardado, EstadoPedido.PENDIENTE);

        return pedidoMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Integer id){
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(()-> new EntityNotFoundException("Pedido", "ID", id));
        return pedidoMapper.toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorNumero(String numeroPedido){
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
            .orElseThrow(()-> new EntityNotFoundException("Pedido", "numeroPedido", numeroPedido));
        return pedidoMapper.toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public Page<ResumenPedidoResponse> listarPorUsuario(Integer usuarioId, Pageable pageable) {
        return pedidoRepository.findByUsuarioId(usuarioId, pageable)
                .map(pedidoMapper::toSummaryResponse);
    }

    public PedidoResponse actualizarEstado(Integer id, EstadoUpdateRequest request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", "ID", request));

        validarTransicionEstado(pedido.getEstado(), request.getEstado());

        pedido.setEstado(request.getEstado());
        pedido.setUpdatedBy(request.getUpdatedBy());
        if (request.getNotas() != null) {
            pedido.setNotas(request.getNotas());
        }

        Pedido actualizado = pedidoRepository.save(pedido);

        eventoPedidoService.registrarEvento(actualizado, request.getEstado());

        return pedidoMapper.toResponse(actualizado);
    }

    private String generarNumeroPedido() {
        String anio = String.valueOf(Year.now().getValue());
        long siguiente = pedidoRepository.countByAnio(anio) + 1;
        return "ORD-%s-%04d".formatted(anio, siguiente);
    }

    private void validarTransicionEstado(EstadoPedido actual, EstadoPedido nuevo) {
        if (actual == EstadoPedido.CANCELADO || actual == EstadoPedido.DEVUELTO) {
            throw new IllegalStateException(
                "No se puede cambiar el estado de un pedido " + actual);
        }
        // agregar más reglas de transición según el flujo de negocio
    }

}
