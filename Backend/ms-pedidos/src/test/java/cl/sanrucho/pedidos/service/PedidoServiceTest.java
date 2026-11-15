package cl.sanrucho.pedidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import cl.sanrucho.common.exception.BusinessRuleException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.pedidos.client.CatalogoClient;
import cl.sanrucho.pedidos.client.dto.ProductoCatalogoResponse;
import cl.sanrucho.pedidos.client.dto.StockUpdateRequest;
import cl.sanrucho.pedidos.dto.DetallePedidoResponse;
import cl.sanrucho.pedidos.dto.ItemPedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.mapper.DetallePedidoMapper;
import cl.sanrucho.pedidos.mapper.PedidoMapper;
import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;
import cl.sanrucho.pedidos.model.entity.pedidos.Pedido;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import cl.sanrucho.pedidos.repository.pedidos.PedidoRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoService detallePedidoService;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private DetallePedidoMapper detallePedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(100);
            }
            return p;
        });

        when(detallePedidoService.guardarTodos(any())).thenAnswer(invocation -> {
            List<DetallePedido> detalles = invocation.getArgument(0);
            int id = 1;
            for (DetallePedido detalle : detalles) {
                detalle.setId(id++);
            }
            return detalles;
        });

        when(pedidoMapper.toResponse(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            return PedidoResponse.builder()
                    .id(p.getId())
                    .idUsuario(p.getIdUsuario())
                    .fechaCompra(p.getFechaCompra())
                    .total(p.getTotal())
                    .estado(p.getEstado())
                    .build();
        });

        when(catalogoClient.obtenerProducto(1))
                .thenReturn(producto(1, "Peluche Hello Kitty", "100", 10, null));
        when(catalogoClient.obtenerProducto(5))
                .thenReturn(producto(5, "Cuaderno Hello Kitty", "50", 10, null));

        when(detallePedidoMapper.toResponseList(any())).thenAnswer(invocation -> {
            List<DetallePedido> detalles = invocation.getArgument(0);
            List<DetallePedidoResponse> responses = new ArrayList<>();
            for (DetallePedido d : detalles) {
                responses.add(DetallePedidoResponse.builder()
                        .idDetalle(d.getId())
                        .idProducto(d.getIdProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build());
            }
            return responses;
        });
    }

    // ─── Creacion ─────────────────────────────────────────────────────────────

    @Test
    void crear_cuandoNoHayItems_lanzaBusinessRuleException() {
        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of())
                .build();

        assertThrows(BusinessRuleException.class, () -> pedidoService.crear(request));

        verify(catalogoClient, never()).obtenerProducto(anyInt());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crear_cuandoProductoEstaRepetido_lanzaBusinessRuleException() {
        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of(
                        ItemPedidoRequest.builder().idProducto(1).cantidad(1).build(),
                        ItemPedidoRequest.builder().idProducto(1).cantidad(2).build()))
                .build();

        assertThrows(BusinessRuleException.class, () -> pedidoService.crear(request));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crear_cuandoStockEsInsuficiente_lanzaBusinessRuleException() {
        when(catalogoClient.obtenerProducto(1)).thenReturn(producto(1, "Peluche", "100", 2, null));

        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of(ItemPedidoRequest.builder().idProducto(1).cantidad(5).build()))
                .build();

        assertThrows(BusinessRuleException.class, () -> pedidoService.crear(request));

        verify(pedidoRepository, never()).save(any());
        verify(catalogoClient, never()).ajustarStock(anyInt(), any());
    }

    @Test
    void crear_cuandoProductoEstaInactivo_lanzaBusinessRuleException() {
        ProductoCatalogoResponse inactivo = producto(1, "Peluche", "100", 10, null);
        inactivo.setEstado("Inactivo");
        when(catalogoClient.obtenerProducto(1)).thenReturn(inactivo);

        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of(ItemPedidoRequest.builder().idProducto(1).cantidad(1).build()))
                .build();

        assertThrows(BusinessRuleException.class, () -> pedidoService.crear(request));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crear_cuandoProductoNoTienePrecio_lanzaBusinessRuleException() {
        when(catalogoClient.obtenerProducto(1)).thenReturn(producto(1, "Peluche", null, 10, null));

        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of(ItemPedidoRequest.builder().idProducto(1).cantidad(1).build()))
                .build();

        assertThrows(BusinessRuleException.class, () -> pedidoService.crear(request));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crear_conOfertaYProductoNormal_calculaTotalYDescuentaStock() {
        when(catalogoClient.obtenerProducto(1))
                .thenReturn(producto(1, "Peluche Hello Kitty", "24990", 10, "19990"));
        when(catalogoClient.obtenerProducto(5))
                .thenReturn(producto(5, "Cuaderno Hello Kitty", "5990", 10, null));

        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of(
                        ItemPedidoRequest.builder().idProducto(1).cantidad(2).build(),
                        ItemPedidoRequest.builder().idProducto(5).cantidad(1).build()))
                .build();

        PedidoResponse response = pedidoService.crear(request);

        // 2 * 19990 + 1 * 5990 = 45970
        assertEquals(new BigDecimal("45970"), response.getTotal());
        assertEquals(EstadoPedido.Pendiente, response.getEstado());
        assertEquals(2, response.getDetalles().size());

        ArgumentCaptor<StockUpdateRequest> captor = ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(catalogoClient, times(2)).ajustarStock(anyInt(), captor.capture());
        List<StockUpdateRequest> ajustes = captor.getAllValues();
        assertEquals(-2, ajustes.get(0).getCantidad());
        assertEquals(-1, ajustes.get(1).getCantidad());
    }

    @Test
    void crear_cuandoFallaElDescuentoDeStock_compensaYRevierteElPedido() {
        when(catalogoClient.obtenerProducto(1))
                .thenReturn(producto(1, "Peluche Hello Kitty", "100", 10, null));
        when(catalogoClient.obtenerProducto(5))
                .thenReturn(producto(5, "Cuaderno Hello Kitty", "50", 10, null));

        // El primer descuento funciona, el segundo falla.
        when(catalogoClient.ajustarStock(eq(1), any()))
                .thenReturn(producto(1, "Peluche Hello Kitty", "100", 8, null));
        doThrow(new RuntimeException("catalogo caido"))
                .when(catalogoClient).ajustarStock(eq(5), any());

        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of(
                        ItemPedidoRequest.builder().idProducto(1).cantidad(2).build(),
                        ItemPedidoRequest.builder().idProducto(5).cantidad(1).build()))
                .build();

        assertThrows(RuntimeException.class, () -> pedidoService.crear(request));

        // El descuento (-2) y la compensacion posterior (+2) del producto 1.
        ArgumentCaptor<StockUpdateRequest> captor = ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(catalogoClient, times(2)).ajustarStock(eq(1), captor.capture());
        assertEquals(-2, captor.getAllValues().get(0).getCantidad());
        assertEquals(2, captor.getAllValues().get(1).getCantidad());

        // Limpieza del pedido y su detalle.
        verify(detallePedidoService).eliminarPorPedido(100);
        verify(pedidoRepository).deleteById(100);
    }

    @Test
    void crear_cuandoFallaElGuardadoDelDetalle_eliminaLaCabecera() {
        when(catalogoClient.obtenerProducto(1))
                .thenReturn(producto(1, "Peluche Hello Kitty", "100", 10, null));
        doThrow(new RuntimeException("detalle_pedidos caida"))
                .when(detallePedidoService).guardarTodos(any());

        PedidoRequest request = PedidoRequest.builder()
                .idUsuario(3)
                .items(List.of(ItemPedidoRequest.builder().idProducto(1).cantidad(1).build()))
                .build();

        assertThrows(RuntimeException.class, () -> pedidoService.crear(request));

        verify(catalogoClient, never()).ajustarStock(anyInt(), any());
        verify(pedidoRepository).deleteById(100);
    }

    // ─── Transiciones de estado ───────────────────────────────────────────────

    @Test
    void actualizarEstado_cuandoTransicionNoEsPermitida_lanzaBusinessRuleException() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido(1, EstadoPedido.Pendiente)));

        assertThrows(BusinessRuleException.class,
                () -> pedidoService.actualizarEstado(1, EstadoPedido.Enviado));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void actualizarEstado_cuandoEsElMismoEstado_lanzaBusinessRuleException() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido(1, EstadoPedido.Pagado)));

        assertThrows(BusinessRuleException.class,
                () -> pedidoService.actualizarEstado(1, EstadoPedido.Pagado));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void actualizarEstado_cuandoSeCancela_reponeStock() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido(1, EstadoPedido.Pendiente)));
        when(detallePedidoService.listarPorPedido(1)).thenReturn(List.of(
                detalle(1, 1, 2, "100"),
                detalle(2, 5, 1, "50")));

        PedidoResponse response = pedidoService.actualizarEstado(1, EstadoPedido.Cancelado);

        assertEquals(EstadoPedido.Cancelado, response.getEstado());

        ArgumentCaptor<StockUpdateRequest> captor = ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(catalogoClient, times(2)).ajustarStock(anyInt(), captor.capture());
        assertEquals(2, captor.getAllValues().get(0).getCantidad());
        assertEquals(1, captor.getAllValues().get(1).getCantidad());
    }

    @Test
    void actualizarEstado_cuandoSePaga_noReponeStock() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido(1, EstadoPedido.Pendiente)));
        when(detallePedidoService.listarPorPedido(1)).thenReturn(List.of(detalle(1, 1, 2, "100")));

        PedidoResponse response = pedidoService.actualizarEstado(1, EstadoPedido.Pagado);

        assertEquals(EstadoPedido.Pagado, response.getEstado());
        verify(catalogoClient, never()).ajustarStock(anyInt(), any());
    }

    @Test
    void actualizarEstado_cuandoElPedidoNoExiste_lanzaEntityNotFoundException() {
        when(pedidoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.actualizarEstado(99, EstadoPedido.Pagado));
    }

    // ─── Eliminacion ──────────────────────────────────────────────────────────

    @Test
    void eliminar_cuandoPedidoEstaEnviado_lanzaBusinessRuleException() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido(1, EstadoPedido.Enviado)));

        assertThrows(BusinessRuleException.class, () -> pedidoService.eliminar(1));

        verify(pedidoRepository, never()).delete(any());
        verify(detallePedidoService, never()).eliminarPorPedido(anyInt());
    }

    @Test
    void eliminar_cuandoPedidoEstaPendiente_reponeStockYBorraDetalle() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido(1, EstadoPedido.Pendiente)));
        when(detallePedidoService.listarPorPedido(1)).thenReturn(List.of(detalle(1, 1, 2, "100")));

        pedidoService.eliminar(1);

        ArgumentCaptor<StockUpdateRequest> captor = ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(catalogoClient).ajustarStock(eq(1), captor.capture());
        assertEquals(2, captor.getValue().getCantidad());

        verify(detallePedidoService).eliminarPorPedido(1);
        verify(pedidoRepository).delete(any(Pedido.class));
    }

    @Test
    void eliminar_cuandoPedidoEstaCancelado_noReponeStock() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido(1, EstadoPedido.Cancelado)));
        when(detallePedidoService.listarPorPedido(1)).thenReturn(List.of(detalle(1, 1, 2, "100")));

        pedidoService.eliminar(1);

        verify(catalogoClient, never()).ajustarStock(anyInt(), any());
        verify(detallePedidoService).eliminarPorPedido(1);
        verify(pedidoRepository).delete(any(Pedido.class));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Pedido pedido(Integer id, EstadoPedido estado) {
        return Pedido.builder()
                .id(id)
                .idUsuario(3)
                .total(new BigDecimal("100"))
                .estado(estado)
                .build();
    }

    private DetallePedido detalle(Integer id, Integer idProducto, Integer cantidad, String precio) {
        return DetallePedido.builder()
                .id(id)
                .idPedido(1)
                .idProducto(idProducto)
                .cantidad(cantidad)
                .precioUnitario(new BigDecimal(precio))
                .build();
    }

    private ProductoCatalogoResponse producto(
            Integer id, String nombre, String precio, Integer stock, String precioOferta) {

        BigDecimal precioNormal = precio != null ? new BigDecimal(precio) : null;
        BigDecimal oferta = precioOferta != null ? new BigDecimal(precioOferta) : null;

        return ProductoCatalogoResponse.builder()
                .id(id)
                .nombre(nombre)
                .precio(precioNormal)
                .oferta(oferta != null)
                .precioOferta(oferta)
                .precioFinal(oferta != null ? oferta : precioNormal)
                .stock(stock)
                .estado("Activo")
                .build();
    }
}
