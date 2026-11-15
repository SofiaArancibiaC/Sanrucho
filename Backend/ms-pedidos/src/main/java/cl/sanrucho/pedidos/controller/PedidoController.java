package cl.sanrucho.pedidos.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.common.dto.PageResponse;
import cl.sanrucho.pedidos.dto.EstadoUpdateRequest;
import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.dto.PedidoResumenResponse;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import cl.sanrucho.pedidos.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "CRUD de pedidos y gestion de estados")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Listar pedidos",
            description = "Devuelve una pagina de pedidos. Permite filtrar por usuario y por estado.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public PageResponse<PedidoResumenResponse> listar(
            @Parameter(description = "Filtro opcional por ID de usuario", example = "3")
            @RequestParam(required = false) Integer idUsuario,
            @Parameter(description = "Filtro opcional por estado", example = "Pendiente")
            @RequestParam(required = false) EstadoPedido estado,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return pedidoService.listar(idUsuario, estado, pageable);
    }

    @Operation(summary = "Obtener un pedido por ID",
            description = "Incluye el detalle y el nombre de cada producto consultado a ms-catalogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Integer id) {
        return pedidoService.obtener(id);
    }

    @Operation(summary = "Crear un pedido",
            description = """
                    Valida stock y estado de cada producto en ms-catalogo, calcula el total
                    con el precio de oferta cuando corresponda y descuenta el stock.
                    Si algo falla, aplica compensaciones para no dejar datos inconsistentes.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente, producto inactivo o datos invalidos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Producto inexistente en ms-catalogo",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = pedidoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/pedidos/" + creado.getId()))
                .body(creado);
    }

    @Operation(summary = "Cambiar el estado de un pedido",
            description = """
                    Transiciones permitidas:
                    Pendiente -> Pagado | Cancelado;
                    Pagado -> Enviado | Cancelado;
                    Enviado -> Entregado.
                    Al cancelar se repone el stock en ms-catalogo.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Transicion de estado no permitida",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{id}/estado")
    public PedidoResponse actualizarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoUpdateRequest request) {

        return pedidoService.actualizarEstado(id, request.getEstado());
    }

    @Operation(summary = "Eliminar un pedido",
            description = "Solo permite eliminar pedidos Pendientes o Cancelados. Si esta Pendiente, repone el stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido eliminado"),
            @ApiResponse(responseCode = "400", description = "El pedido esta Enviado, Entregado o no existe",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        pedidoService.eliminar(id);
    }
}
