package cl.sanrucho.catalogo.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

public class ProductoRequest {
    @NotBlank(message = "El SKU es obligatorio")
    @Size(min = 3, max = 50, message = "El SKU debe tener entre 3 y 50 caracteres")
    private String sku;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Min(value = 100, message = "El precio mínimo es 100")
    private Integer precio;

    @NotBlank(message = "El personaje es obligatorio")
    @Size(max = 50, message = "El personaje no puede superar los 50 caracteres")
    private String personaje;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no puede superar los 100 caracteres")
    private String categoria;

    @Size(max = 500, message = "La URL de imagen no puede superar los 500 caracteres")
    @Pattern(regexp = "^(https?://.*|/.*)?$", message = "Formato de URL de imagen inválido")
    private String imagenUrl;

    @Builder.Default
    @Valid
    private List<EspecificacionRequest> especificaciones = new ArrayList<>();

}
