package com.microservices.springboot.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class EjemploGlobalFilter implements GlobalFilter {

	private final Logger logger = LoggerFactory.getLogger(EjemploGlobalFilter.class);

	/**
	 * Este metodo define un filtro global en Spring Cloud Gateway que se ejecuta antes y después de que la solicitud sea procesada por el servicio backend.
	 * En la fase PRE, se añade un encabezado `tokenRequest` a la solicitud.
	 * En la fase POST, se agrega el encabezado `tokenResponse` a la respuesta y se añade una cookie `colorResponse`.
	 *
	 * @param exchange El objeto {@link ServerWebExchange} que contiene la información de la solicitud y la respuesta. Representa el contexto de la solicitud HTTP actual.
	 * @param chain La cadena de filtros {@link GatewayFilterChain} que permite pasar la solicitud a través del siguiente filtro o servicio backend.
	 * @return Un objeto {@link Mono<Void>} que representa el flujo asincrónico de la operación.
	 * El filtro se ejecutará de forma no bloqueante. El metodo `then()` asegura que el código en la fase POST se ejecuta después de procesar la solicitud.
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("Ejecutando Filtro Global PRE");
		exchange.getRequest().mutate().headers(headers -> headers.add("tokenRequestGlobal", "123123123"));
		
		return chain.filter(exchange).then(Mono.fromRunnable(()->{
			logger.info("Ejecutando Filtro Global POST");
			
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("tokenRequestGlobal")).ifPresent(valor -> {
				exchange.getResponse().getHeaders().add("tokenResponseGlobal", valor);
			});
			
			exchange.getResponse().getCookies().add("colorResponse", ResponseCookie.from("colorResponseGlobal", "rojo").build());
		}));
	}

}
