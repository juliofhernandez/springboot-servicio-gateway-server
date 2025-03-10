package com.microservices.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

    /**
     * El orden en que defines los matchers es crucial, ya que las reglas se evalúan secuencialmente.
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(auth -> auth
                        .pathMatchers("/logout", "/login/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/api/**").hasRole("ADMIN")
                        .anyExchange().authenticated())
                .cors(ServerHttpSecurity.CorsSpec::disable)// Desactiva la configuración CORS en este contexto de seguridad
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Evitamos el uso de sesiones y obligamos a que cada request sea validada de forma independiente con el token (hace la aplicación stateless)
                .oauth2Login(Customizer.withDefaults()) // Habilita el flujo interactivo de login OAuth2 (redirección a la pantalla de login)
                .oauth2Client(Customizer.withDefaults()) // Permite que el gateway actúe como cliente OAuth2 (útil para intercambiar códigos por tokens)
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))) // Configura el gateway para que valide tokens JWT en las peticiones a recursos protegidos
                .build();
    }

    /**
     * JwtGrantedAuthoritiesConverter: Extrae la claim "roles" del JWT y la convierte en GrantedAuthorities sin agregar prefijos.
     * JwtAuthenticationConverter: Utiliza el authoritiesConverter para construir un AbstractAuthenticationToken a partir del JWT.
     * ReactiveJwtAuthenticationConverterAdapter: Adapta el JwtAuthenticationConverter para que devuelva un Mono<AbstractAuthenticationToken>,
     * que es lo que requiere el entorno reactivo.
     */
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
