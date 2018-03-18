package com.armandorv.poc.tasks.security.jwt;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class JWTAuthenticationWebFilterTests {

	private static final String VALID_TOKEN = "someValidToken";

	private static final String INVALID_TOKEN = "invalidToken";

	private Authentication authentication;
	
	private JWTAuthenticationWebFilter webFilter;

	private JWTAuthenticationProvider authenticationProvider;
	
	private WebFilterChain filterChain;

	@Before
	public void setUp() {
		UserDetails principal = User.builder().username("username@mail.com").password("").roles("USER").build();
		
		this.authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
		
		this.authenticationProvider = mock(JWTAuthenticationProvider.class);
		
		this.filterChain =  mock(WebFilterChain.class);

		this.webFilter = new JWTAuthenticationWebFilter(this.authenticationProvider);
	}
	
	@Test
	public void should_NotCreateSecurityContext_WhenHeaderIsNotPresent() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/anyendpoint"));
			
		given(filterChain.filter(exchange)).willReturn(Mono.empty());

		StepVerifier.create(webFilter.filter(exchange, filterChain)).verifyComplete();
		
		then(authenticationProvider).should(never()).validateToken(anyString());
		then(authenticationProvider).should(never()).getAuthentication(anyString());
		
		then(filterChain).should(times(1)).filter(exchange);
	}
	
	@Test
	public void should_NotCreateSecurityContext_WhenTokenIsNotPresent() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/anyendpoint")
				.header("Authorization", ""));
			
		given(filterChain.filter(exchange)).willReturn(Mono.empty());
		
		StepVerifier.create(webFilter.filter(exchange, filterChain)).verifyComplete();

		then(authenticationProvider).should(never()).validateToken(anyString());
		then(authenticationProvider).should(never()).getAuthentication(anyString());
		
		then(filterChain).should(times(1)).filter(exchange);
	}

	@Test
	public void should_NotCreateSecurityContext_WhenTokenIsNotValid() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/anyendpoint")
				.header("Authorization", "Bearer " + INVALID_TOKEN));
			
		given(filterChain.filter(exchange)).willReturn(Mono.empty());
		
		given(authenticationProvider.validateToken(INVALID_TOKEN)).willReturn(false);

		StepVerifier.create(webFilter.filter(exchange, filterChain)).verifyComplete();
		
		then(authenticationProvider).should(times(1)).validateToken(INVALID_TOKEN);
		then(authenticationProvider).should(never()).getAuthentication(anyString());
		
		then(filterChain).should(times(1)).filter(exchange);
	}
	
	@Test
	public void should_CreateSecurityContext_WhenTokenIsValid() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/anyendpoint")
				.header("Authorization", "Bearer " + VALID_TOKEN));
			
		given(filterChain.filter(exchange)).willReturn(Mono.empty());
		
		given(authenticationProvider.validateToken(VALID_TOKEN)).willReturn(true);
		given(authenticationProvider.getAuthentication(VALID_TOKEN)).willReturn(Mono.just(authentication));
		
		StepVerifier.create(webFilter.filter(exchange, filterChain)).verifyComplete();
		
		then(authenticationProvider).should(times(1)).validateToken(VALID_TOKEN);
		then(authenticationProvider).should(times(1)).getAuthentication(VALID_TOKEN);
		
		then(filterChain).should(times(1)).filter(exchange);
	}
}
