package com.microservices.springboot.app.gateway.filters.factory;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class EjemploGatewayFilterFactory extends AbstractGatewayFilterFactory<EjemploGatewayFilterFactory.ConfigurationCookie>{

	private final Logger logger = LoggerFactory.getLogger(EjemploGatewayFilterFactory.class);

	public EjemploGatewayFilterFactory() {
		super(ConfigurationCookie.class);
	}

	/**
	 * Aplica el filtro definido en esta fábrica de filtros de puerta de enlace. El filtro realiza dos acciones:
	 * 1. En la fase PRE, registra un mensaje de log que se obtiene desde la configuración y ejecuta código de filtrado.
	 * 2. En la fase POST, agrega una cookie a la respuesta si se encuentra configurada en la clase {@link ConfigurationCookie}, y registra otro mensaje de log.
	 *
	 * @param config La configuración {@link ConfigurationCookie} que proporciona los valores de mensaje y cookie.
	 * @return Un {@link GatewayFilter} que aplica el filtro PRE y POST según lo configurado.
	 */
	@Override
	public GatewayFilter apply(ConfigurationCookie config) {
		return (exchange,chain)->{			
			logger.info("Ejecutando Gateway Filter Factory PRE: "+ config.mensajePRE);
            // Gateway Filter Factory PRE Code
			return chain.filter(exchange).then(Mono.fromRunnable(()->{
				Optional.ofNullable(config.cookieValor).ifPresent(cookie -> exchange.getResponse().addCookie(ResponseCookie.from(config.cookieNombre, cookie).build()));
				logger.info("Ejecutando Gateway Filter Factory POST: " + config.mensajePOST);
			}));
		};
	}

	/**
	 * Clase interna que define los parámetros de configuración utilizados por el filtro de puerta de enlace.
	 * Los valores configurados aquí son utilizados en la fase PRE (mensajes de log) y en la fase POST (valores de cookies).
	 */
	public static class ConfigurationCookie {
		private String mensajePRE;
		private String mensajePOST;
		private String cookieValor;
		private String cookieNombre;
		
		public String getMensajePRE() {
			return mensajePRE;
		}
		public void setMensajePRE(String mensajePRE) {
			this.mensajePRE = mensajePRE;
		}
		public String getCookieValor() {
			return cookieValor;
		}
		public void setCookieValor(String cookieValor) {
			this.cookieValor = cookieValor;
		}
		public String getCookieNombre() {
			return cookieNombre;
		}
		public void setCookieNombre(String cookieNombre) {
			this.cookieNombre = cookieNombre;
		}
		public String getMensajePOST() {
			return mensajePOST;
		}
		public void setMensajePOST(String mensajePOST) {
			this.mensajePOST = mensajePOST;
		}
	}
}
