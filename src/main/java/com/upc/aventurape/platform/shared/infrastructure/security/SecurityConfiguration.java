package com.upc.aventurape.platform.shared.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Bean("apiFilterChain")
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // deshabilita CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                        //  redireccion al swagger
                                "/swagger-ui/**",          // Swagger UI
                                "/v3/api-docs/**",         // Docs de OpenAPI
                                "/actuator/**",           // por si se se usa Spring Actuator
                                "/swagger-ui/index.html#/",
                                "/api/v1/authentication/sign-up",   // <- permitir sign-up
                                "/api/v1/authentication/sign-in"    // <- permitir sign-in
                        ).permitAll()                // permite el acceso sin autenticacion
                        .anyRequest().authenticated() // el resto de rutas requieren autenticación
                );

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("https://aventurape-web-app.web.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
// Si vas a enviar cookies o tokens tipo Bearer en headers personalizados
        configuration.setAllowCredentials(true); // Permitir cookies o credenciales
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

