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

	@Bean
	public JwtParser jwtParser(ApplicationProperties properties) {
		final String secretKey = properties.getSecurity().getJwt().getSecret();
		return Jwts.parser().setSigningKey(secretKey);
	}

	@Bean
	public JwtBuilder jwtBuilder(ApplicationProperties properties) {
		final long tokenValidityInMilliseconds = 1000 * properties.getSecurity().getJwt().getTokenValidityInSeconds();

		final Date validity = new Date(new Date().getTime() + tokenValidityInMilliseconds);
		final String secretKey = properties.getSecurity().getJwt().getSecret();

		return Jwts.builder().signWith(SignatureAlgorithm.HS512, secretKey).setExpiration(validity);
	}
}
