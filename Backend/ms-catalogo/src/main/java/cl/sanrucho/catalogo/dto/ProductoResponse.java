package cl.sanrucho.catalogo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {
    private Integer id;
    private String sku;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private String personaje;
    private String categoria;
    private String imagenUrl;
    private EstadoProducto estado;
    private LocalDateTime createdAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;

    @Builder.Default
    private List<EspecificacionProductoResponse> especificaciones = new ArrayList<>();
}
