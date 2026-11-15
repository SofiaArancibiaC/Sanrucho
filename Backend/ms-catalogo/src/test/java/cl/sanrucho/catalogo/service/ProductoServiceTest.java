package cl.sanrucho.catalogo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import cl.sanrucho.catalogo.dto.CategoriaResponse;
import cl.sanrucho.catalogo.dto.ProductoRequest;
import cl.sanrucho.catalogo.dto.ProductoResponse;
import cl.sanrucho.catalogo.mapper.CategoriaMapper;
import cl.sanrucho.catalogo.mapper.ProductoMapper;
import cl.sanrucho.catalogo.model.entity.categorias.Categoria;
import cl.sanrucho.catalogo.model.entity.productos.Producto;
import cl.sanrucho.catalogo.repository.categorias.CategoriaRepository;
import cl.sanrucho.catalogo.repository.productos.ProductoRepository;
import cl.sanrucho.common.exception.BusinessRuleException;
import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoMapper productoMapper;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        when(productoMapper.toEntity(any(ProductoRequest.class))).thenAnswer(invocation -> {
            ProductoRequest r = invocation.getArgument(0);
            return Producto.builder()
                    .codigo(r.getCodigo())
                    .idCategoria(r.getIdCategoria())
                    .nombre(r.getNombre())
                    .descripcion(r.getDescripcion())
                    .precio(r.getPrecio())
                    .oferta(r.getOferta())
                    .precioOferta(r.getPrecioOferta())
                    .descuento(r.getDescuento())
                    .stock(r.getStock())
                    .stockCritico(r.getStockCritico())
                    .imagenUrl(r.getImagenUrl())
                    .estado(r.getEstado())
                    .build();
        });

        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(1);
            }
            return p;
        });

        when(productoMapper.toResponse(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            return ProductoResponse.builder()
                    .id(p.getId())
                    .codigo(p.getCodigo())
                    .nombre(p.getNombre())
                    .precio(p.getPrecio())
                    .precioFinal(p.getPrecioFinal())
                    .stock(p.getStock())
                    .estado(p.getEstado())
                    .build();
        });

        when(categoriaMapper.toResponse(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria c = invocation.getArgument(0);
            return CategoriaResponse.builder().id(c.getId()).nombre(c.getNombre()).build();
        });

        when(productoRepository.countByIdCategoria(any())).thenReturn(0L);
    }

    @Test
    void crear_cuandoCodigoYaExiste_lanzaDuplicateResourceException() {
        ProductoRequest request = requestBase();
        when(productoRepository.existsByCodigo("HK-PLUSH-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productoService.crear(request));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void crear_cuandoCategoriaNoExiste_lanzaEntityNotFoundException() {
        ProductoRequest request = requestBase();
        request.setIdCategoria(99);
        when(productoRepository.existsByCodigo(anyString())).thenReturn(false);
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productoService.crear(request));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void crear_cuandoOfertaNoTienePrecioOferta_lanzaBusinessRuleException() {
        ProductoRequest request = requestBase();
        request.setOferta(true);
        request.setPrecioOferta(null);
        when(productoRepository.existsByCodigo(anyString())).thenReturn(false);
        when(categoriaRepository.findById(2)).thenReturn(Optional.of(categoria()));

        assertThrows(BusinessRuleException.class, () -> productoService.crear(request));
    }

    @Test
    void crear_cuandoPrecioOfertaEsMayorAlPrecio_lanzaBusinessRuleException() {
        ProductoRequest request = requestBase();
        request.setOferta(true);
        request.setPrecio(new BigDecimal("100"));
        request.setPrecioOferta(new BigDecimal("120"));
        when(productoRepository.existsByCodigo(anyString())).thenReturn(false);
        when(categoriaRepository.findById(2)).thenReturn(Optional.of(categoria()));

        assertThrows(BusinessRuleException.class, () -> productoService.crear(request));
    }

    @Test
    void crear_cuandoOfertaValida_calculaDescuentoYPrecioFinal() {
        ProductoRequest request = requestBase();
        request.setOferta(true);
        request.setPrecio(new BigDecimal("100"));
        request.setPrecioOferta(new BigDecimal("75"));
        request.setDescuento(null);

        when(productoRepository.existsByCodigo(anyString())).thenReturn(false);
        when(categoriaRepository.findById(2)).thenReturn(Optional.of(categoria()));

        productoService.crear(request);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());

        Producto guardado = captor.getValue();
        assertEquals(25, guardado.getDescuento());
        assertEquals(new BigDecimal("75"), guardado.getPrecioFinal());
    }

    @Test
    void ajustarStock_cuandoDejaStockNegativo_lanzaBusinessRuleException() {
        Producto producto = Producto.builder()
                .id(1)
                .nombre("Peluche")
                .precio(new BigDecimal("100"))
                .stock(2)
                .stockCritico(1)
                .idCategoria(2)
                .build();

        when(productoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(producto));

        assertThrows(BusinessRuleException.class, () -> productoService.ajustarStock(1, -5));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void ajustarStock_cuandoEsValido_actualizaStock() {
        Producto producto = Producto.builder()
                .id(1)
                .nombre("Peluche")
                .precio(new BigDecimal("100"))
                .stock(10)
                .stockCritico(1)
                .idCategoria(2)
                .build();

        when(productoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(2)).thenReturn(Optional.of(categoria()));

        ProductoResponse response = productoService.ajustarStock(1, -3);

        assertEquals(7, response.getStock());
    }

    private ProductoRequest requestBase() {
        return ProductoRequest.builder()
                .codigo("HK-PLUSH-001")
                .idCategoria(2)
                .nombre("Peluche Hello Kitty")
                .precio(new BigDecimal("100"))
                .oferta(false)
                .stock(10)
                .stockCritico(2)
                .build();
    }

    private Categoria categoria() {
        return Categoria.builder().id(2).nombre("Peluches").build();
    }
}
