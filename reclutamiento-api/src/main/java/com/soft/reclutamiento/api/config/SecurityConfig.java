package com.soft.reclutamiento.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration
 * <p>
 * IMPORTANTE: Esta configuración DESHABILITA temporalmente la seguridad
 * para permitir desarrollo y testing sin autenticación.
 * <p>
 * En producción, deberás:
 * 1. Implementar autenticación JWT
 * 2. Configurar roles y permisos
 * 3. Habilitar CORS apropiadamente
 * 4. Configurar CSRF según necesidades
 * <p>
 * .NET Equivalent: Startup.cs - ConfigureServices() con AddAuthentication()
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad
     * <p>
     * Configuración actual: PERMISIVO (permite todo sin autenticación)
     * <p>
     * Para producción, reemplazar con configuración de JWT/OAuth2
     *
     * @param http HttpSecurity builder
     * @return SecurityFilterChain configurado
     * @throws Exception si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (común para APIs REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // Permitir TODAS las peticiones sin autenticación (solo para desarrollo)
                        .anyRequest().permitAll()
                )

                // Deshabilitar formulario de login
                .formLogin(AbstractHttpConfigurer::disable)

                // Deshabilitar HTTP Basic
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /*
     * CONFIGURACIÓN PARA PRODUCCIÓN (comentada por ahora)
     *
     * Descomenta y configura esto cuando estés listo para implementar seguridad real:
     *
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Endpoints protegidos
                        .requestMatchers("/api/**").authenticated()

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    */
}
