package cl.sanrucho.catalogo.controller;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/catalogo/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "CRUD de productos y ajuste de stock")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Listar productos",
            description = "Devuelve una pagina de productos. Permite filtrar por nombre y por categoria.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public PageResponse<ProductoResponse> listar(
            @Parameter(description = "Filtro opcional por nombre", example = "Hello Kitty")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtro opcional por ID de categoria", example = "2")
            @RequestParam(required = false) Integer idCategoria,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return productoService.listar(nombre, idCategoria, pageable);
    }

    @Operation(summary = "Obtener un producto por ID",
            description = "Incluye la categoria resuelta desde la base de datos categorias y el precio final.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Integer id) {
        return productoService.obtener(id);
    }

    @Operation(summary = "Obtener un producto por codigo",
            description = "Busqueda por codigo de negocio (por ejemplo HK-PLUSH-001).")
    @GetMapping("/codigo/{codigo}")
    public ProductoResponse obtenerPorCodigo(@PathVariable String codigo) {
        return productoService.obtenerPorCodigo(codigo);
    }

    @Operation(summary = "Crear un producto",
            description = "Requiere rol Dueno o Admin. Valida codigo unico, categoria existente, precios y oferta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio incumplida",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Codigo de producto duplicado",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/catalogo/productos/" + creado.getId()))
                .body(creado);
    }

    @Operation(summary = "Actualizar un producto",
            description = "Requiere rol Dueno o Admin. Recalcula el precio final y el descuento.")
    @PutMapping("/{id}")
    public ProductoResponse actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequest request) {

        return productoService.actualizar(id, request);
    }

    @Operation(summary = "Eliminar un producto",
            description = "Requiere rol Dueno o Admin. El historial de pedidos conserva el precio unitario de compra.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
    }

    @Operation(summary = "Ajustar stock",
            description = """
                    Suma o resta unidades al stock. Cantidad negativa descuenta (checkout de ms-pedidos)
                    y cantidad positiva repone inventario. Nunca permite stock negativo.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o cantidad invalida",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{id}/stock")
    public ProductoResponse ajustarStock(
            @PathVariable Integer id,
            @Valid @RequestBody StockUpdateRequest request) {

        return productoService.ajustarStock(id, request.getCantidad());
    }
}
