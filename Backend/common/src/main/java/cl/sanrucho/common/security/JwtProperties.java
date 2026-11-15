package cl.sanrucho.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Propiedades de configuracion JWT.
 *
 * Se mapean desde el application.yml de cada microservicio:
 *
 *   jwt:
 *     secret: <clave-base64>
 *     expiration-ms: 3600000
 */
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Clave secreta en Base64 para firmar y verificar tokens.
     * Debe tener al menos 256 bits (32 bytes) para HMAC-SHA256.
     */
    private String secret;

    /** Tiempo de expiracion del token en milisegundos (1 hora por defecto). */
    private long expirationMs = 3_600_000L;
}
