package cl.sanrucho.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro que valida el JWT de cada peticion y construye el SecurityContext.
 *
 * El token se guarda como "credentials" del Authentication. Esto es clave:
 * el FeignClientInterceptor lo recupera desde ahi para propagarlo en las
 * llamadas entre microservicios (ms-pedidos -> ms-catalogo).
 *
 * Si el token es invalido o esta en la blacklist simplemente no se autentica;
 * Spring Security respondera 401 mediante RestAuthenticationEntryPoint.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extraerToken(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validarToken(token)) {

                if (tokenBlacklistService.isBlacklisted(token)) {
                    log.debug("Token rechazado: se encuentra en la blacklist (logout)");
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                String email = jwtTokenProvider.getEmailFromToken(token);
                String rol = jwtTokenProvider.getRolFromToken(token);

                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + rol));

                // El token viaja como credential para que Feign lo propague.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, token, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Usuario autenticado: {} con rol: {}", email, rol);
            }

        } catch (Exception e) {
            log.error("Error al procesar el token JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extraerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/favicon.ico")
                || path.startsWith("/actuator");
    }
}
