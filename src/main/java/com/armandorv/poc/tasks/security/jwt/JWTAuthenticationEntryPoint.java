package com.armandorv.poc.tasks.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JWTAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private static final String CONTENT_TYPE = "Content-Type";

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
		return Mono.fromRunnable(() -> {
			ServerHttpResponse response = exchange.getResponse();
		
			response.getHeaders().add(CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
		});
	}
}
