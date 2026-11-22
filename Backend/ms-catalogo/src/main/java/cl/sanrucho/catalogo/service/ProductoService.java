package cl.sanrucho.catalogo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cl.sanrucho.catalogo.dto.CategoriaResponse;
import cl.sanrucho.catalogo.dto.ProductoRequest;
import cl.sanrucho.catalogo.dto.ProductoResponse;
import cl.sanrucho.catalogo.mapper.CategoriaMapper;
import cl.sanrucho.catalogo.mapper.ProductoMapper;
import cl.sanrucho.catalogo.model.entity.categorias.Categoria;
import cl.sanrucho.catalogo.model.entity.productos.Producto;
import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import cl.sanrucho.catalogo.repository.categorias.CategoriaRepository;
import cl.sanrucho.catalogo.repository.productos.ProductoRepository;
import cl.sanrucho.common.dto.PageResponse;
import cl.sanrucho.common.exception.BusinessRuleException;
import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Reglas de negocio de productos.
 *
 * - codigo unico (se normaliza a mayusculas).
 * - la categoria debe existir.
 * - precio > 0, stock >= 0.
 * - si oferta=true, precioOferta es obligatorio y debe ser menor al precio.
 * - el descuento se calcula automaticamente si no se envia (0-100).
 * - el ajuste de stock nunca puede dejar existencias negativas.
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaMapper categoriaMapper;

    @Transactional(readOnly = true)
    public PageResponse<ProductoResponse> listar(String nombre, Integer idCategoria, Pageable pageable) {
        Page<Producto> page;

        if (StringUtils.hasText(nombre) && idCategoria != null) {
            page = productoRepository.findByNombreContainingIgnoreCaseAndIdCategoria(nombre.trim(), idCategoria, pageable);
        } else if (StringUtils.hasText(nombre)) {
            page = productoRepository.findByNombreContainingIgnoreCase(nombre.trim(), pageable);
        } else if (idCategoria != null) {
            page = productoRepository.findByIdCategoria(idCategoria, pageable);
        } else {
            page = productoRepository.findAll(pageable);
        }

        Map<Integer, Categoria> categorias = cargarCategorias(page.getContent());
        return PageResponse.from(page.map(p -> toResponse(p, categorias.get(p.getIdCategoria()))));
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Integer id) {
        Producto producto = buscar(id);
        return toResponse(producto, buscarCategoriaOpcional(producto.getIdCategoria()));
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorCodigo(String codigo) {
        Producto producto = productoRepository.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Producto", "codigo", codigo));

        return toResponse(producto, buscarCategoriaOpcional(producto.getIdCategoria()));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        String codigo = normalizarCodigo(request.getCodigo());

        if (productoRepository.existsByCodigo(codigo)) {
            throw new DuplicateResourceException("Producto", "codigo", codigo, request.getNombre());
        }

        Categoria categoria = validarCategoria(request.getIdCategoria());

        Producto producto = productoMapper.toEntity(request);
        aplicarReglas(producto, request);
        producto.setCodigo(codigo);

        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado, categoria);
    }

    @Transactional
    public ProductoResponse actualizar(Integer id, ProductoRequest request) {
        Producto producto = buscar(id);
        String codigo = normalizarCodigo(request.getCodigo());

        if (productoRepository.existsByCodigoAndIdNot(codigo, id)) {
            throw new DuplicateResourceException("Producto", "codigo", codigo, request.getNombre());
        }

        Categoria categoria = validarCategoria(request.getIdCategoria());

        productoMapper.updateEntity(request, producto);
        aplicarReglas(producto, request);
        producto.setCodigo(codigo);

        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado, categoria);
    }

    @Transactional
    public void eliminar(Integer id) {
        Producto producto = buscar(id);
        productoRepository.delete(producto);
    }

    /**
     * Ajusta el stock de forma atomica. Cantidad positiva suma, negativa resta.
     * Usa bloqueo pesimista para evitar condiciones de carrera en el checkout.
     */
    @Transactional
    public ProductoResponse ajustarStock(Integer id, Integer cantidad) {
        Producto producto = productoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto", "ID", id));

        if (cantidad == null || cantidad == 0) {
            throw new BusinessRuleException("La cantidad de ajuste debe ser distinta de cero.");
        }

        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new BusinessRuleException(String.format(
                    "Stock insuficiente para el producto '%s'. Disponible: %d, solicitado: %d.",
                    producto.getNombre(), producto.getStock(), Math.abs(cantidad)));
        }

        producto.setStock(nuevoStock);
        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado, buscarCategoriaOpcional(guardado.getIdCategoria()));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Producto buscar(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto", "ID", id));
    }

    private Categoria validarCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException("Categoria", "ID", idCategoria));
    }

    private Categoria buscarCategoriaOpcional(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElse(null);
    }

    private Map<Integer, Categoria> cargarCategorias(List<Producto> productos) {
        Set<Integer> ids = productos.stream()
                .map(Producto::getIdCategoria)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Integer, Categoria> mapa = new HashMap<>();
        categoriaRepository.findAllById(ids).forEach(c -> mapa.put(c.getId(), c));
        return mapa;
    }

    private void aplicarReglas(Producto producto, ProductoRequest request) {
        producto.setNombre(request.getNombre().trim());
        producto.setDescripcion(normalizar(request.getDescripcion()));
        producto.setImagenUrl(normalizar(request.getImagenUrl()));
        producto.setIdCategoria(request.getIdCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setStockCritico(request.getStockCritico());
        producto.setEstado(request.getEstado() != null ? request.getEstado() : EstadoProducto.Activo);

        boolean enOferta = Boolean.TRUE.equals(request.getOferta());
        producto.setOferta(enOferta);

        if (!enOferta) {
            producto.setPrecioOferta(null);
            producto.setDescuento(null);
            return;
        }

        if (request.getPrecioOferta() == null) {
            throw new BusinessRuleException(
                    "El precio de oferta es obligatorio cuando el producto esta en oferta.");
        }

        if (request.getPrecioOferta().compareTo(request.getPrecio()) >= 0) {
            throw new BusinessRuleException(
                    "El precio de oferta debe ser menor que el precio normal del producto.");
        }

        int descuento = request.getDescuento() != null
                ? request.getDescuento()
                : calcularDescuento(request.getPrecio(), request.getPrecioOferta());

        if (descuento < 0 || descuento > 100) {
            throw new BusinessRuleException("El descuento debe estar entre 0 y 100.");
        }

        producto.setPrecioOferta(request.getPrecioOferta());
        producto.setDescuento(descuento);
    }

    private int calcularDescuento(BigDecimal precio, BigDecimal precioOferta) {
        return precio.subtract(precioOferta)
                .multiply(BigDecimal.valueOf(100))
                .divide(precio, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase();
    }

    private String normalizar(String texto) {
        return StringUtils.hasText(texto) ? texto.trim() : null;
    }

    private ProductoResponse toResponse(Producto producto, Categoria categoria) {
        ProductoResponse response = productoMapper.toResponse(producto);
        response.setIdCategoria(producto.getIdCategoria());
        response.setPrecioFinal(producto.getPrecioFinal());
        response.setStockBajo(producto.isStockBajo());

        if (categoria != null) {
            CategoriaResponse categoriaResponse = categoriaMapper.toResponse(categoria);
            categoriaResponse.setCantidadProductos(productoRepository.countByIdCategoria(categoria.getId()));
            response.setNombreCategoria(categoria.getNombre());
            response.setCategoria(categoriaResponse);
        }

        return response;
    }
}
