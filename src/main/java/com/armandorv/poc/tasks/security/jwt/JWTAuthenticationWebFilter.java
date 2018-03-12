package com.armandorv.poc.tasks.security.jwt;

import java.util.List;
import java.util.Optional;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class JWTAuthenticationWebFilter implements WebFilter {

	public static final String AUTHORIZATION_HEADER = "Authorization";

	private JWTAuthenticationProvider jwtAuthenticationProvider;

	public JWTAuthenticationWebFilter(JWTAuthenticationProvider jwtAuthenticationProvider) {
		this.jwtAuthenticationProvider = jwtAuthenticationProvider;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		
		Optional<String> jwToken = getTokenFromHeader(exchange.getRequest());
		
		if(!jwToken.isPresent()) {
			return chain.filter(exchange);
		}
		
		return Mono.just(jwToken.get())
				   .publishOn(Schedulers.elastic()).flatMap(t -> {
				
			if(jwtAuthenticationProvider.validateToken(t)) {
					
				return jwtAuthenticationProvider.getAuthentication(t)
						.map(ReactiveSecurityContextHolder::withAuthentication)
						.flatMap(c -> chain.filter(exchange).subscriberContext(c));
				}
				else {
					return chain.filter(exchange);
				}
			});
	}

	private Optional<String> getTokenFromHeader(ServerHttpRequest request) {
		List<String> authorizationHeader = request.getHeaders().get(AUTHORIZATION_HEADER);

		if (authorizationHeader == null || authorizationHeader.isEmpty()) {
			return  Optional.empty();
		}

		final String bearerToken = authorizationHeader.get(0);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return Optional.of(bearerToken.substring(7, bearerToken.length()));
		}
		return Optional.empty();
	}
}