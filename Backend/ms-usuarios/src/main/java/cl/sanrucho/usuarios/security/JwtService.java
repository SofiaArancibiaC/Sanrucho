package cl.sanrucho.usuarios.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera el token con el claim "rol" para que los demas microservicios
     * puedan autorizar por rol (antes solo viajaba el email).
     */
    public String generateToken(String subject, String rol, Integer idUsuario) {
        return Jwts.builder()
                .subject(subject)
                .claim("rol", rol)
                .claim("idUsuario", idUsuario)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key())
                .compact();
    }

    public String generateToken(String subject, String rol) {
        return generateToken(subject, rol, null);
    }

    /** Sobrecarga retrocompatible: por defecto rol de cliente. */
    public String generateToken(String subject) {
        return generateToken(subject, "CLIENTE", null);
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public String extractRol(String token) {
        return parse(token).get("rol", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }
}
