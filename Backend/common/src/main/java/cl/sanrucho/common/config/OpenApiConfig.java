package cl.sanrucho.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuracion global de OpenAPI / Swagger para todos los microservicios.
 *
 * Habilita el boton "Authorize" (candado) para enviar el JWT en Swagger UI
 * y completa el titulo con el nombre del microservicio (spring.application.name).
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Value("${spring.application.name:microservicio}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sanrucho - " + applicationName)
                        .version("1.0.0")
                        .description("""
                                API REST del microservicio %s de la tienda Sanrucho.

                                **Como autenticarse:** pulse el boton Authorize e ingrese el JWT
                                (sin la palabra Bearer). El token debe incluir el claim `rol`
                                con uno de los valores: Dueno, Admin o Cliente.
                                """.formatted(applicationName))
                        .contact(new Contact()
                                .name("Equipo Sanrucho")
                                .email("equipo@sanrucho.cl")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
