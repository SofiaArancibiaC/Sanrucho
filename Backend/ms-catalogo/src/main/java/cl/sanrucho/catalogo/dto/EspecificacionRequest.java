package cl.sanrucho.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
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
public class EspecificacionRequest {


    @NotBlank(message = "El atributo es obligatorio")
    @Size(max = 100, message = "El atributo no puede superar los 100 caracteres")
    private String atributo;

    @NotBlank(message = "El valor es obligatorio")
    @Size(max = 200, message = "El valor no puede superar los 200 caracteres")
    private String valor;
}
