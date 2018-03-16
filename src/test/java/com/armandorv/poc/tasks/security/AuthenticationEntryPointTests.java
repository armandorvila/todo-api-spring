package com.armandorv.poc.tasks.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class AuthenticationEntryPointTests {

	private AuthenticationEntryPoint authenticationEntryPoint;

	private MockServerWebExchange exchange;

	@Before
	public void setUp() {
		this.authenticationEntryPoint = new AuthenticationEntryPoint(new ObjectMapper());
		this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/tasks"));
	}

	@Test
	public void should_SendUnauthorized_When_AuthenticationException() throws JsonProcessingException {
		Mono<Void> response = authenticationEntryPoint.commence(exchange,
				new AuthenticationCredentialsNotFoundException("credentials not found"));
		
		StepVerifier.create(response).verifyComplete();
	}
}
