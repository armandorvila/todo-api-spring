package com.armandorv.poc.tasks.security.jwt;

import java.util.List;
import java.util.Optional;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
		String jwToken = getTokenFromHeader(exchange.getRequest());
		
		if(jwToken == null) {
			return chain.filter(exchange);
		}
		
		return Mono.just(jwToken).publishOn(Schedulers.parallel())
		   .map(jwtAuthenticationProvider::toAuthentication)
		   .filter(Optional::isPresent).map(Optional::get)
		   .flatMap(auth -> chain.filter(exchange)
					.subscriberContext(ReactiveSecurityContextHolder.withAuthentication(auth)));
		
	}

	private String getTokenFromHeader(ServerHttpRequest request) {
		List<String> authorizationHeader = request.getHeaders().get(AUTHORIZATION_HEADER);

		if (CollectionUtils.isEmpty(authorizationHeader)) {
			return null;
		}

		String bearerToken = authorizationHeader.get(0);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
}