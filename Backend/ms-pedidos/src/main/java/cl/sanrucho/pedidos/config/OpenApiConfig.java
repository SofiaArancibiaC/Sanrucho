package cl.sanrucho.pedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuracion de Swagger / OpenAPI para ms-pedidos.
 *
 * Define el esquema de seguridad JWT para que los endpoints puedan probarse
 * desde la propia interfaz de Swagger usando el boton "Authorize".
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_SEGURIDAD = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sanrucho - MS Pedidos")
                        .version("v1")
                        .description("Gestion de pedidos. Valida usuarios y descuenta stock "
                                + "de ms-catalogo via Feign; ms-pagos actualiza el estado del pedido."))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURIDAD))
                .components(new Components().addSecuritySchemes(ESQUEMA_SEGURIDAD,
                        new SecurityScheme()
                                .name(ESQUEMA_SEGURIDAD)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido en POST /api/v1/auth/login")));
    }
}
