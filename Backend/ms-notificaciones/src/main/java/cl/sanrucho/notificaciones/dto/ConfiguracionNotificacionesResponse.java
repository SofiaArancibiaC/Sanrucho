package cl.sanrucho.notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionNotificacionesResponse {
    
    private Integer id;
    private Integer usuarioId;
    private Boolean emailPedidos;
    private Boolean emailPromociones;
    private Boolean emailNewsletter;
    private Boolean pushPedidos;
    private Boolean pushPromociones;
    private Boolean smsPedidos;
}
