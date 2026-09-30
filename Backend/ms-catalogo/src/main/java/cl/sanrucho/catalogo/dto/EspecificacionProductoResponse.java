package cl.sanrucho.catalogo.dto;

import java.time.LocalDateTime;

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
public class EspecificacionProductoResponse {
    private Integer id;
    private String atributo;
    private String valor;
    private LocalDateTime createdAt;
}
