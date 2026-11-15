package cl.sanrucho.catalogo.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import cl.sanrucho.catalogo.dto.CategoriaRequest;
import cl.sanrucho.catalogo.dto.CategoriaResponse;
import cl.sanrucho.catalogo.mapper.CategoriaMapper;
import cl.sanrucho.catalogo.model.entity.categorias.Categoria;
import cl.sanrucho.catalogo.repository.categorias.CategoriaRepository;
import cl.sanrucho.catalogo.repository.productos.ProductoRepository;
import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.ReferentialIntegrityException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        when(categoriaMapper.toEntity(any(CategoriaRequest.class))).thenAnswer(invocation -> {
            CategoriaRequest r = invocation.getArgument(0);
            return Categoria.builder().nombre(r.getNombre()).descripcion(r.getDescripcion()).build();
        });

        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria c = invocation.getArgument(0);
            if (c.getId() == null) {
                c.setId(1);
            }
            return c;
        });

        when(categoriaMapper.toResponse(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria c = invocation.getArgument(0);
            return CategoriaResponse.builder().id(c.getId()).nombre(c.getNombre()).build();
        });

        when(productoRepository.countByIdCategoria(any())).thenReturn(0L);
    }

    @Test
    void crear_cuandoNombreDuplicado_lanzaDuplicateResourceException() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Peluches").build();
        when(categoriaRepository.existsByNombreIgnoreCase("Peluches")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoriaService.crear(request));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoTieneProductos_lanzaReferentialIntegrityException() {
        Categoria categoria = Categoria.builder().id(2).nombre("Peluches").build();
        when(categoriaRepository.findById(2)).thenReturn(Optional.of(categoria));
        when(productoRepository.countByIdCategoria(2)).thenReturn(3L);

        assertThrows(ReferentialIntegrityException.class, () -> categoriaService.eliminar(2));
        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    void eliminar_cuandoNoTieneProductos_eliminaCategoria() {
        Categoria categoria = Categoria.builder().id(2).nombre("Peluches").build();
        when(categoriaRepository.findById(2)).thenReturn(Optional.of(categoria));
        when(productoRepository.countByIdCategoria(2)).thenReturn(0L);

        categoriaService.eliminar(2);

        verify(categoriaRepository).delete(categoria);
    }
}
