package com.armandorv.poc.tasks.security.jwt;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JWTAuthenticationWebFilter implements WebFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
	
    @Autowired
    private JWTAuthenticationProvider jwtAuthenticationProvider;

    private String getTokenFromHeader(ServerHttpRequest request){
    	List<String> authHeader = request.getHeaders().get(AUTHORIZATION_HEADER);
    	
    	if(authHeader == null || authHeader.isEmpty()) {
    		return null;
    	}
    	
        String bearerToken = authHeader.get(0);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String jwToken = getTokenFromHeader(exchange.getRequest());
        
		Optional<Authentication> authentication = jwtAuthenticationProvider.toAuthentication(jwToken);
		
		if (authentication.isPresent()) { 
            return chain.filter(exchange).subscriberContext(ReactiveSecurityContextHolder
            		.withAuthentication(authentication.get()));
        }
		
		return chain.filter(exchange);
	}
}