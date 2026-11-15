package cl.sanrucho.common.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Proveedor central de tokens JWT.
 *
 * Responsabilidades:
 * 1. GENERAR tokens firmados con HMAC-SHA256 incluyendo email, rol y nombre.
 * 2. VALIDAR tokens verificando firma, expiracion y estructura.
 * 3. EXTRAER claims (email, rol, nombre) desde un token valido.
 *
 * En este proyecto los microservicios de negocio solo VALIDAN tokens.
 * La generacion queda disponible para el servicio de autenticacion que
 * emita los tokens (o para la herramienta de pruebas incluida en /scripts).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    // ─── Generacion ──────────────────────────────────────────────────────────

    /**
     * Genera un token JWT firmado.
     *
     * @param email          email del usuario (subject del token)
     * @param rol            rol del usuario: Dueno, Admin o Cliente
     * @param nombreCompleto nombre para incluir como claim adicional
     */
    public String generarToken(String email, String rol, String nombreCompleto) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .claim("nombre", nombreCompleto)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    // ─── Validacion ──────────────────────────────────────────────────────────

    /**
     * Valida firma y expiracion del token.
     *
     * @return true si el token es valido, false en caso contrario
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT invalido: {}", e.getMessage());
            return false;
        }
    }

    // ─── Extraccion de claims ────────────────────────────────────────────────

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRolFromToken(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public String getNombreFromToken(String token) {
        return getClaims(token).get("nombre", String.class);
    }

    /** Fecha de expiracion, usada por la blacklist para limpiar tokens viejos. */
    public Date getExpirationFromToken(String token) {
        return getClaims(token).getExpiration();
    }

    // ─── Metodos privados ────────────────────────────────────────────────────

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
