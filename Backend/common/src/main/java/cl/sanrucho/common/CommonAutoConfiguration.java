package cl.sanrucho.common;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import cl.sanrucho.common.security.JwtProperties;

/**
 * Auto-configuracion de la libreria common.
 *
 * Spring Boot la descubre automaticamente al leer el archivo:
 *   META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 *
 * Cualquier microservicio que declare common como dependencia Maven recibe
 * automaticamente sus beans: GlobalExceptionHandler, JwtTokenProvider,
 * JwtAuthenticationFilter, FeignClientInterceptor, OpenApiConfig, etc.
 */
@AutoConfiguration
@ComponentScan(basePackages = "cl.sanrucho.common")
@EnableConfigurationProperties(JwtProperties.class)
public class CommonAutoConfiguration {
    // Configuracion pura: no necesita cuerpo.
}
