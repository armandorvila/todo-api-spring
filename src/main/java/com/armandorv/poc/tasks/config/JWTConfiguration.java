package com.armandorv.poc.tasks.config;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
public class JWTConfiguration {
	
	private final String secretKey;
	
	private final Long tokenValidityInMilliseconds;
	
	public JWTConfiguration(ApplicationProperties properties) {
		this.secretKey = properties.getSecurity().getJwt().getSecret();
		this.tokenValidityInMilliseconds = 1000 * properties.getSecurity().getJwt().getTokenValidityInSeconds();
	}

	@Bean
	public JwtParser jwtParser() {
		return Jwts.parser().setSigningKey(secretKey);
	}

	@Bean
	public JwtBuilder jwtBuilder() {
		final Date validity = new Date(new Date().getTime() + tokenValidityInMilliseconds);
		return Jwts.builder().signWith(SignatureAlgorithm.HS512, secretKey).setExpiration(validity);
	}
}
