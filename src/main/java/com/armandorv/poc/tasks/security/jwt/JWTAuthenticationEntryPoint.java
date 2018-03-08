package com.armandorv.poc.tasks.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JWTAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private static final String ERROR_MESSAGE = "{"
			+ "\"developerMessage\":\"Token not provided\","
			+ "\"code\":\"authentication_error\","
			+ "\"userMessage\":\"This resource is only available after logging in.\""
			+ "}";

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
		return Mono.fromRunnable(() -> {
			ServerHttpResponse response = exchange.getResponse();
		
			response.writeWith(s -> Mono.just(ERROR_MESSAGE));
			response.getHeaders().add("Content-Type", "application/json");
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
		});
	}
}
