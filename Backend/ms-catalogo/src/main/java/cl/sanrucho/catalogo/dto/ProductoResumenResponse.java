package cl.sanrucho.catalogo.dto;

import java.time.LocalDateTime;

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

public class ProductoResumenResponse {
    
    private Integer id;
    private String sku;
    private String nombre;
    private Integer precio;
    private String personaje;
    private String categoria;
    private String imagenUrl;
    private EstadoProducto estado;
    private LocalDateTime createdAt;
}
