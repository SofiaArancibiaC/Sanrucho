package cl.sanrucho.catalogo.dto;

import java.math.BigDecimal;

import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    @NotBlank(message = "El codigo es obligatorio")
    @Size(max = 50, message = "El codigo no puede superar los 50 caracteres")
    private String codigo;

    @NotNull(message = "La categoria es obligatoria")
    @Positive(message = "El ID de la categoria debe ser positivo")
    private Integer idCategoria;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 2000, message = "La descripcion no puede superar los 2000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Digits(integer = 8, fraction = 0, message = "El precio debe tener hasta 8 digitos enteros")
    private BigDecimal precio;

    private Boolean oferta;

    @DecimalMin(value = "0.01", message = "El precio de oferta debe ser mayor que cero")
    @Digits(integer = 8, fraction = 0, message = "El precio de oferta debe tener hasta 8 digitos enteros")
    private BigDecimal precioOferta;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede superar 100")
    private Integer descuento;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El stock critico es obligatorio")
    @Min(value = 0, message = "El stock critico no puede ser negativo")
    private Integer stockCritico;

    @Size(max = 255, message = "La URL de imagen no puede superar los 255 caracteres")
    private String imagenUrl;

    private EstadoProducto estado;
}
