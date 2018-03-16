package com.armandorv.poc.tasks.security;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.armandorv.poc.tasks.resource.errors.ErrorDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	public AuthenticationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
		final ServerHttpResponse response = exchange.getResponse();

		response.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
		response.setStatusCode(UNAUTHORIZED);

		return response.writeWith(errorMessage());
	}

	private Mono<DataBuffer> errorMessage() {
		ErrorDTO error = new ErrorDTO(UNAUTHORIZED.value(), "You have to provide a valid token to acess this resource.");

		byte[] message = new byte[0];

		try {
			message = objectMapper.writeValueAsBytes(error);
		} catch (JsonProcessingException e) {
			log.warn("Error rendering authentication entry point message.");
		}

		return Mono.just(new DefaultDataBufferFactory().wrap(message));
	}
}
