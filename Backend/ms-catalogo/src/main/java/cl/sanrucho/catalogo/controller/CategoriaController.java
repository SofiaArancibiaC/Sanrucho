package cl.sanrucho.catalogo.controller;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.catalogo.dto.CategoriaRequest;
import cl.sanrucho.catalogo.dto.CategoriaResponse;
import cl.sanrucho.catalogo.service.CategoriaService;
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
@RequestMapping("/api/v1/catalogo/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "CRUD de categorias de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Operation(summary = "Listar categorias",
            description = "Devuelve una pagina de categorias. Permite filtrar por nombre (parcial, sin distinguir mayusculas).")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public PageResponse<CategoriaResponse> listar(
            @Parameter(description = "Filtro opcional por nombre", example = "Peluches")
            @RequestParam(required = false) String nombre,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return categoriaService.listar(nombre, pageable);
    }

    @Operation(summary = "Obtener una categoria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    public CategoriaResponse obtener(@PathVariable Integer id) {
        return categoriaService.obtener(id);
    }

    @Operation(summary = "Crear una categoria",
            description = "Requiere rol Dueno o Admin. El nombre debe ser unico.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria creada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una categoria con ese nombre",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse creada = categoriaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/catalogo/categorias/" + creada.getId()))
                .body(creada);
    }

    @Operation(summary = "Actualizar una categoria",
            description = "Requiere rol Dueno o Admin. El nombre debe seguir siendo unico.")
    @PutMapping("/{id}")
    public CategoriaResponse actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequest request) {

        return categoriaService.actualizar(id, request);
    }

    @Operation(summary = "Eliminar una categoria",
            description = "Requiere rol Dueno o Admin. No se permite si la categoria tiene productos asociados.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria eliminada"),
            @ApiResponse(responseCode = "409", description = "La categoria tiene productos asociados",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        categoriaService.eliminar(id);
    }
}
