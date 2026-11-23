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
 * Este microservicio actua como RESOURCE SERVER: no emite tokens, solo valida
 * el JWT en cada peticion y aplica reglas por rol.
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
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // Documentacion OpenAPI / Swagger UI (publica, solo lectura)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/webjars/**").permitAll()

                        // Lectura pública: catálogo y categorías visibles sin login
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v1/categorias/**")
                            .permitAll()

                        // Ajuste de stock: usado por ms-pedidos durante el checkout
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/productos/*/stock")
                            .authenticated()

                        // Escritura: solo Dueno y Admin
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos/**", "/api/v1/categorias/**")
                            .hasAnyRole("Dueño", "Admin")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**", "/api/v1/categorias/**")
                            .hasAnyRole("Dueño", "Admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**", "/api/v1/categorias/**")
                            .hasAnyRole("Dueño", "Admin")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
