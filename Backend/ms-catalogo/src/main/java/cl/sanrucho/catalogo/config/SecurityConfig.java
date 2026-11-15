package cl.sanrucho.catalogo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cl.sanrucho.common.security.JwtAuthenticationFilter;
import cl.sanrucho.common.security.RestAccessDeniedHandler;
import cl.sanrucho.common.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;

/**
 * Configuracion de Spring Security para ms-catalogo.
 *
 * Este microservicio actua como RESOURCE SERVER:
 * - NO emite tokens (los emite el servicio de autenticacion).
 * - Valida el JWT en cada peticion y aplica reglas por rol.
 *
 * Matriz de autorizacion:
 * ┌─────────────────────────────────────────────┬────────┬──────────────────────────┐
 * │ Endpoint                                    │ Metodo │ Acceso                   │
 * ├─────────────────────────────────────────────┼────────┼──────────────────────────┤
 * │ /swagger-ui/**, /v3/api-docs/**, /actuator  │ ALL    │ Publico                  │
 * │ /api/v1/catalogo/**                         │ GET    │ Dueno, Admin, Cliente    │
 * │ /api/v1/catalogo/productos/{id}/stock       │ PATCH  │ Cualquier autenticado    │
 * │ /api/v1/catalogo/**                         │ POST   │ Dueno, Admin             │
 * │ /api/v1/catalogo/**                         │ PUT    │ Dueno, Admin             │
 * │ /api/v1/catalogo/**                         │ DELETE │ Dueno, Admin             │
 * └─────────────────────────────────────────────┴────────┴──────────────────────────┘
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // Lectura: cualquier rol autenticado
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalogo/**")
                            .hasAnyRole("Dueño", "Admin", "Cliente")

                        // Ajuste de stock: usado por ms-pedidos durante el checkout
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/catalogo/productos/*/stock")
                            .authenticated()

                        // Escritura: solo Dueno y Admin
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalogo/**")
                            .hasAnyRole("Dueño", "Admin")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/catalogo/**")
                            .hasAnyRole("Dueño", "Admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/catalogo/**")
                            .hasAnyRole("Dueño", "Admin")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Location"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
