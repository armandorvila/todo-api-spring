package com.armandorv.poc.tasks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.armandorv.poc.tasks.security.jwt.JWTAuthenticationEntryPoint;
import com.armandorv.poc.tasks.security.jwt.JWTAuthenticationWebFilter;

@SuppressWarnings("deprecation")
@Configuration
public class SecurityConfiguration {

	private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	private JWTAuthenticationWebFilter jwtWebFilter;

	public SecurityConfiguration(JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint,
			JWTAuthenticationWebFilter jwtWebFilter) {
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtWebFilter = jwtWebFilter;
	}

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		http.csrf().disable();
		http.logout().disable();
		http.httpBasic().disable();
		http.formLogin().disable();

		http.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);
		http.addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

		http.authorizeExchange().pathMatchers("/authenticate/**").permitAll();
		http.authorizeExchange().pathMatchers("/users/signup/**").permitAll();
		http.authorizeExchange().anyExchange().authenticated();

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager(
			ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

		UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(
				userDetailsService);

		authenticationManager.setPasswordEncoder(passwordEncoder);

		return authenticationManager;
	}
}