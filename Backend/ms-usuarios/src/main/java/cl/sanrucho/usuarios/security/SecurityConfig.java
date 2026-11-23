package cl.sanrucho.usuarios.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .httpBasic(hb -> hb.disable())
            .formLogin(fl -> fl.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Necesario para el healthcheck /actuator/health de Docker.
                .requestMatchers("/actuator/**", "/error").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                // Tienda (cliente, admin y dueño pueden ver productos)
                .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v1/categorias/**")
                    .hasAnyRole("DUENO", "ADMIN", "CLIENTE")

                // Órdenes: el admin solo lectura
                .requestMatchers(HttpMethod.GET, "/api/v1/boletas/**")
                    .hasAnyRole("DUENO", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/boletas/**")
                    .hasAnyRole("DUENO", "CLIENTE")

                // Todo lo demás (crear/editar/eliminar, usuarios, etc.) solo DUEÑO
                .anyRequest().hasRole("DUENO")
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
