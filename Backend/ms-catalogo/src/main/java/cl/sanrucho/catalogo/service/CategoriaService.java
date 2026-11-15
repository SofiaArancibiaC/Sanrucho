package cl.sanrucho.catalogo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cl.sanrucho.catalogo.dto.CategoriaRequest;
import cl.sanrucho.catalogo.dto.CategoriaResponse;
import cl.sanrucho.catalogo.mapper.CategoriaMapper;
import cl.sanrucho.catalogo.model.entity.categorias.Categoria;
import cl.sanrucho.catalogo.repository.categorias.CategoriaRepository;
import cl.sanrucho.catalogo.repository.productos.ProductoRepository;
import cl.sanrucho.common.dto.PageResponse;
import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.common.exception.ReferentialIntegrityException;
import lombok.RequiredArgsConstructor;

/**
 * Reglas de negocio de categorias.
 *
 * - El nombre es unico (sin distinguir mayusculas).
 * - No se puede eliminar una categoria que tenga productos asociados.
 *   La validacion es a nivel de servicio porque productos y categorias
 *   viven en bases de datos distintas (no hay FK fisica).
 */
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "categoriasTransactionManager")
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional(transactionManager = "categoriasTransactionManager", readOnly = true)
    public PageResponse<CategoriaResponse> listar(String nombre, Pageable pageable) {
        Page<Categoria> page = StringUtils.hasText(nombre)
                ? categoriaRepository.findByNombreContainingIgnoreCase(nombre.trim(), pageable)
                : categoriaRepository.findAll(pageable);

        return PageResponse.from(page.map(categoriaMapper::toResponse));
    }

    @Transactional(transactionManager = "categoriasTransactionManager", readOnly = true)
    public CategoriaResponse obtener(Integer id) {
        return toResponse(buscar(id));
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        String nombre = request.getNombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new DuplicateResourceException("Categoria", "nombre", nombre, request.getDescripcion());
        }

        Categoria categoria = categoriaMapper.toEntity(request);
        categoria.setNombre(nombre);
        categoria.setDescripcion(normalizar(request.getDescripcion()));

        Categoria guardada = categoriaRepository.save(categoria);
        return toResponse(guardada);
    }

    public CategoriaResponse actualizar(Integer id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        String nombre = request.getNombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new DuplicateResourceException("Categoria", "nombre", nombre, request.getDescripcion());
        }

        categoriaMapper.updateEntity(request, categoria);
        categoria.setNombre(nombre);
        categoria.setDescripcion(normalizar(request.getDescripcion()));

        return toResponse(categoriaRepository.save(categoria));
    }

    public void eliminar(Integer id) {
        Categoria categoria = buscar(id);

        long productos = productoRepository.countByIdCategoria(id);
        if (productos > 0) {
            throw new ReferentialIntegrityException("Categoria", id,
                    productos + " producto(s) de la base de datos productos");
        }

        categoriaRepository.delete(categoria);
    }

    private Categoria buscar(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria", "ID", id));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        CategoriaResponse response = categoriaMapper.toResponse(categoria);
        response.setCantidadProductos(productoRepository.countByIdCategoria(categoria.getId()));
        return response;
    }

    private String normalizar(String texto) {
        return StringUtils.hasText(texto) ? texto.trim() : null;
    }
}
