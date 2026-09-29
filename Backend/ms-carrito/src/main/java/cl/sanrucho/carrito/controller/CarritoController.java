package cl.sanrucho.carrito.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.*;

import cl.sanrucho.carrito.dto.AgregarItemRequest;
import cl.sanrucho.carrito.dto.CarritoRequest;
import cl.sanrucho.carrito.dto.CarritoResponse;
import cl.sanrucho.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Carrito", description = "Operaciones del carrito de compras")
@RestController
@RequestMapping("/api/v1/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    // ─── Método auxiliar HATEOAS ──────────────────────────────────────────────

    /**
     * links de navegación a un CarritoResponse:
     * - self         → GET    /api/v1/carritos/{clienteId}
     * - agregar-item → POST   /api/v1/carritos/{clienteId}/items
     * - vaciar       → DELETE /api/v1/carritos/{clienteId}
     * - crear        → POST   /api/v1/carritos
     */
    private CarritoResponse addLinks(CarritoResponse carrito) {
        Integer clienteId = carrito.getUsuarioId();

        carrito.add(linkTo(methodOn(CarritoController.class)
                .obtenerCarrito(clienteId))
                .withSelfRel());

        carrito.add(linkTo(methodOn(CarritoController.class)
                .agregarItem(clienteId, null))
                .withRel("agregar-item").withTitle("POST - Agregar ítem al carrito"));

        carrito.add(linkTo(methodOn(CarritoController.class)
                .vaciarCarrito(clienteId))
                .withRel("vaciar").withTitle("DELETE - Vaciar carrito"));

        carrito.add(linkTo(methodOn(CarritoController.class)
                .crearCarrito(null))
                .withRel("crear").withTitle("POST - Crear nuevo carrito"));

        return carrito;
    }
// ─── Endpoints ────────────────────────────────────────────────────────────

    @Operation(summary = "Crear carrito", description = "Crea un nuevo carrito para un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carrito creado exitosamente",
            content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CarritoResponse> crearCarrito(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del carrito a crear", required = true,
                content = @Content(schema = @Schema(implementation = CarritoRequest.class)))
            @RequestBody @Valid CarritoRequest request) {
        CarritoResponse response = carritoService.crearCarrito(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(addLinks(response));
    }

    @Operation(summary = "Obtener carrito", description = "Obtiene el carrito de un cliente por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito encontrado",
            content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content)
    })
    @GetMapping("/{clienteId}")
    public ResponseEntity<CarritoResponse> obtenerCarrito(
            @Parameter(description = "ID del cliente", required = true, example = "1")
            @PathVariable Integer clienteId) {
        CarritoResponse response = carritoService.obtenerCarritoPorCliente(clienteId);
        return ResponseEntity.ok(addLinks(response));
    }

    @Operation(summary = "Agregar item", description = "Agrega un producto al carrito del cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item agregado exitosamente",
            content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping("/{clienteId}/items")
    public ResponseEntity<CarritoResponse> agregarItem(
            @Parameter(description = "ID del cliente", required = true, example = "1")
            @PathVariable Integer clienteId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Item a agregar al carrito", required = true,
                content = @Content(schema = @Schema(implementation = AgregarItemRequest.class)))
            @RequestBody @Valid AgregarItemRequest request) {
        CarritoResponse response = carritoService.agregarItem(clienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(addLinks(response));
    }

    @Operation(summary = "Quitar item", description = "Elimina un item específico del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item eliminado exitosamente",
            content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Item no encontrado", content = @Content)
    })
    @DeleteMapping("/{clienteId}/items/{itemId}")
    public ResponseEntity<CarritoResponse> quitarItem(
            @Parameter(description = "ID del cliente", required = true, example = "1")
            @PathVariable Integer clienteId,
            @Parameter(description = "ID del item a eliminar", required = true, example = "5")
            @PathVariable Integer itemId) {
        CarritoResponse response = carritoService.quitarItem(clienteId, itemId);
        return ResponseEntity.ok(addLinks(response));
    }

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito vaciado exitosamente",
            content = @Content(schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content)
    })
    @DeleteMapping("/{clienteId}")
    public ResponseEntity<CarritoResponse> vaciarCarrito(
            @Parameter(description = "ID del cliente", required = true, example = "1")
            @PathVariable Integer clienteId) {
        CarritoResponse response = carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.ok(addLinks(response));
    }

}