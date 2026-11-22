package cl.sanrucho.pagos.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
 * Configuracion de Spring Security para ms-pagos.
 *
 * Actua como RESOURCE SERVER: valida el JWT emitido por ms-usuarios.
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

                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // Lectura: cualquier rol autenticado
                        .requestMatchers(HttpMethod.GET, "/api/v1/pagos/**")
                            .authenticated()

                        // Iniciar pago: clientes y administracion
                        .requestMatchers(HttpMethod.POST, "/api/v1/pagos/**")
                            .hasAnyRole("Cliente", "Admin", "Dueño")

                        // Aprobar/rechazar/anular: administracion
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pagos/*/estado")
                            .hasAnyRole("Admin", "Dueño")

                        // Eliminacion: administracion
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/pagos/**")
                            .hasAnyRole("Admin", "Dueño")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
