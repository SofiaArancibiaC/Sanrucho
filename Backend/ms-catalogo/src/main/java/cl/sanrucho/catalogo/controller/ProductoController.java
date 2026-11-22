package cl.sanrucho.catalogo.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.catalogo.dto.ProductoRequest;
import cl.sanrucho.catalogo.dto.ProductoResponse;
import cl.sanrucho.catalogo.dto.StockUpdateRequest;
import cl.sanrucho.catalogo.service.ProductoService;
import cl.sanrucho.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public PageResponse<ProductoResponse> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idCategoria,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return productoService.listar(nombre, idCategoria, pageable);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Integer id) {
        return productoService.obtener(id);
    }

    @GetMapping("/codigo/{codigo}")
    public ProductoResponse obtenerPorCodigo(@PathVariable String codigo) {
        return productoService.obtenerPorCodigo(codigo);
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/productos/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequest request) {

        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
    }

    /**
     * Suma o resta unidades al stock. Cantidad negativa descuenta (checkout de
     * ms-pedidos) y cantidad positiva repone inventario.
     */
    @PatchMapping("/{id}/stock")
    public ProductoResponse ajustarStock(
            @PathVariable Integer id,
            @Valid @RequestBody StockUpdateRequest request) {

        return productoService.ajustarStock(id, request.getCantidad());
    }
}
