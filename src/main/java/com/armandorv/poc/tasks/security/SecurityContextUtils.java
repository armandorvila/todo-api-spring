package com.armandorv.poc.tasks.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import reactor.core.publisher.Mono;

public class SecurityContextUtils {

	private SecurityContextUtils() {}
	
	public static Mono<String> getPrincipal() {
		return ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication().getName());
	}
}
