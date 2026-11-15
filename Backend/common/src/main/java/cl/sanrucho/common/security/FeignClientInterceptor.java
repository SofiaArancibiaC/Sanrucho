package cl.sanrucho.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor de Feign que propaga el token JWT en llamadas entre microservicios.
 *
 * Toma el token guardado como credential del Authentication actual y lo agrega
 * como header "Authorization: Bearer <token>" en la peticion saliente.
 * Sin esto, ms-catalogo rechazaria con 401 las llamadas de ms-pedidos.
 */
@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() instanceof String token
                && !token.isBlank()) {
            template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
            log.debug("Token JWT propagado en llamada Feign a {}", template.url());
        }
    }
}
