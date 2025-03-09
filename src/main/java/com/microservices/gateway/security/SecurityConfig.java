package com.microservices.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityConfig {

    /**
     * El orden en que defines los matchers es crucial, ya que las reglas se evalúan secuencialmente.
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(auth -> auth
                        .pathMatchers("/logout","/login/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasAnyAuthority("SCOPE_read", "SCOPE_write")
                        .pathMatchers("/api/**").hasAnyAuthority("SCOPE_write")
                        .anyExchange().authenticated())
                .cors(ServerHttpSecurity.CorsSpec::disable)// Desactiva la configuración CORS en este contexto de seguridad
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Evitamos el uso de sesiones y obligamos a que cada request sea validada de forma independiente con el token (hace la aplicación stateless)
                .oauth2Login(Customizer.withDefaults()) // Habilita el flujo interactivo de login OAuth2 (redirección a la pantalla de login)
                .oauth2Client(Customizer.withDefaults()) // Permite que el gateway actúe como cliente OAuth2 (útil para intercambiar códigos por tokens)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Configura el gateway para que valide tokens JWT en las peticiones a recursos protegidos
                .build();
    }

}
