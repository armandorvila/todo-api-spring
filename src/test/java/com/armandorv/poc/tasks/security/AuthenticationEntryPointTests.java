package com.armandorv.poc.tasks.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import com.armandorv.poc.tasks.resource.errors.ErrorDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class AuthenticationEntryPointTests {

	private AuthenticationEntryPoint authenticationEntryPoint;
	
	private ObjectMapper objectMapper;

	private MockServerWebExchange exchange;

	@Before
	public void setUp() {
		this.objectMapper = mock(ObjectMapper.class);
		this.authenticationEntryPoint = new AuthenticationEntryPoint(objectMapper);
		this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/anyendpoint"));
	}

	@Test
	public void should_SendUnauthorized_When_AuthenticationException() throws JsonProcessingException {
		given(objectMapper.writeValueAsBytes(any(ErrorDTO.class)))
		  .willReturn("some error message".getBytes());
		
		Mono<Void> result = authenticationEntryPoint.commence(exchange,
				new AuthenticationCredentialsNotFoundException("credentials not found"));
		
		StepVerifier.create(result).verifyComplete();
		
		then(objectMapper).should(times(1)).writeValueAsBytes(any(ErrorDTO.class));
		
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(UNAUTHORIZED);
	}
	
	@Test
	public void should_SendNoMessage_When_ErrorSerizlizingJson() throws JsonProcessingException {
		given(objectMapper.writeValueAsBytes(any(ErrorDTO.class)))
		  .willThrow(JsonProcessingException.class);
		
		Mono<Void> result = authenticationEntryPoint.commence(exchange,
				new AuthenticationCredentialsNotFoundException("credentials not found"));
		
		StepVerifier.create(result).verifyComplete();
		
		then(objectMapper).should(times(1)).writeValueAsBytes(any(ErrorDTO.class));
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(UNAUTHORIZED);
	}
}
