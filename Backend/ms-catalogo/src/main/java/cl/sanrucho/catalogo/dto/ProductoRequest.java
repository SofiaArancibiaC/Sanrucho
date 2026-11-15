package cl.sanrucho.catalogo.dto;

import java.math.BigDecimal;

import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "ProductoRequest", description = "Datos para crear o actualizar un producto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    @Schema(description = "Codigo unico del producto", example = "HK-PLUSH-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El codigo es obligatorio")
    @Size(max = 50, message = "El codigo no puede superar los 50 caracteres")
    private String codigo;

    @Schema(description = "ID de la categoria (referencia logica a la BD categorias)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La categoria es obligatoria")
    @Positive(message = "El ID de la categoria debe ser positivo")
    private Integer idCategoria;

    @Schema(example = "Peluche Hello Kitty Clasico 30cm", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Schema(example = "Peluche suave de Hello Kitty con su iconico mono rojo.")
    @Size(max = 2000, message = "La descripcion no puede superar los 2000 caracteres")
    private String descripcion;

    @Schema(example = "24990", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Digits(integer = 8, fraction = 0, message = "El precio debe tener hasta 8 digitos enteros")
    private BigDecimal precio;

    @Schema(description = "Indica si el producto tiene oferta vigente", example = "true")
    private Boolean oferta;

    @Schema(description = "Precio de oferta (obligatorio si oferta=true y debe ser menor al precio)", example = "19990")
    @DecimalMin(value = "0.01", message = "El precio de oferta debe ser mayor que cero")
    @Digits(integer = 8, fraction = 0, message = "El precio de oferta debe tener hasta 8 digitos enteros")
    private BigDecimal precioOferta;

    @Schema(description = "Porcentaje de descuento (0-100). Si se omite, se calcula a partir de los precios", example = "20")
    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede superar 100")
    private Integer descuento;

    @Schema(example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Umbral de stock critico para alertas", example = "3")
    @NotNull(message = "El stock critico es obligatorio")
    @Min(value = 0, message = "El stock critico no puede ser negativo")
    private Integer stockCritico;

    @Schema(example = "/static/img/productos/hello-kitty1.webp")
    @Size(max = 255, message = "La URL de imagen no puede superar los 255 caracteres")
    private String imagenUrl;

    @Schema(description = "Estado comercial. Por defecto Activo", example = "Activo")
    private EstadoProducto estado;
}
