package cl.sanrucho.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "CategoriaRequest", description = "Datos para crear o actualizar una categoria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequest {

    @Schema(description = "Nombre unico de la categoria", example = "Peluches", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Schema(description = "Descripcion de la categoria", example = "Los peluches mas tiernos del universo.")
    @Size(max = 2000, message = "La descripcion no puede superar los 2000 caracteres")
    private String descripcion;
}
