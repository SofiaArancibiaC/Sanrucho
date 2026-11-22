package cl.sanrucho.catalogo.controller;

import java.net.URI;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public PageResponse<CategoriaResponse> listar(
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return categoriaService.listar(nombre, pageable);
    }

    @GetMapping("/{id}")
    public CategoriaResponse obtener(@PathVariable Integer id) {
        return categoriaService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse creada = categoriaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/categorias/" + creada.getId()))
                .body(creada);
    }

    @PutMapping("/{id}")
    public CategoriaResponse actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequest request) {

        return categoriaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        categoriaService.eliminar(id);
    }
}
