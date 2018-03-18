package com.armandorv.poc.tasks.security.jwt;

import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JWTAuthenticationProvider {

	private static final String AUTHORITIES_KEY = "auth";

	private JwtBuilder jwtBuilder;
	
	private JwtParser jwtParser;
	
	public JWTAuthenticationProvider(JwtBuilder jwtBuilder, JwtParser jwtParser) {
		this.jwtParser = jwtParser;
		this.jwtBuilder = jwtBuilder;
	}

	public String getToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
				.map(a -> a.getAuthority()
				.replaceAll("ROLE_", ""))
				.collect(Collectors.joining(","));

		return jwtBuilder
				.setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY, authorities).compact();
	}

	public Mono<Authentication> getAuthentication(String token) {
		Claims claims = jwtParser.parseClaimsJws(token).getBody();

		String[] authorities = claims.get(AUTHORITIES_KEY).toString().split(",");

		UserDetails principal = org.springframework.security.core.userdetails.User.builder()
				.username(claims.getSubject())
				.password("").roles(authorities)
				.build();

		return Mono.just(new UsernamePasswordAuthenticationToken(principal, token, 
				principal.getAuthorities()));
	}

	public boolean validateToken(String authToken) {
		if (!StringUtils.hasText(authToken)) {
			return false;
		}
		try {
			jwtParser.parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.info("Invalid JWT signature.");
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
		}
		return false;
	}
}
