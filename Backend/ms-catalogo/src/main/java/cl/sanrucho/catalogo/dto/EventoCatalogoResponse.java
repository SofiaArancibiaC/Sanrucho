package cl.sanrucho.catalogo.dto;

import java.time.LocalDateTime;
import java.util.Map;

import cl.sanrucho.catalogo.model.entity.EventoCatalogo;
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
public class EventoCatalogoResponse {
    private Integer id;
    private String eventId;
    private Integer productoId;
    private EventoCatalogo tipoEvento;
    private Map<String, Object> payload;
    private Boolean publicado;
    private LocalDateTime fechaEvento;
    private LocalDateTime fechaPublicacion;

}
