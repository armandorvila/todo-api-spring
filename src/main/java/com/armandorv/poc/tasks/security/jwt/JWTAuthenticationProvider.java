package com.armandorv.poc.tasks.security.jwt;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.armandorv.poc.tasks.config.ApplicationProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTAuthenticationProvider {

	private static final String AUTHORITIES_KEY = "auth";

	private ApplicationProperties properties;

	public JWTAuthenticationProvider(ApplicationProperties properties) {
		this.properties = properties;
	}
	
	private String getTokenSecret() {
		return properties.getSecurity().getJwt().getSecret();
	}

	private Date getTokenValidity() {
		final long tokenValidityInSeconds = properties.getSecurity().getJwt().getTokenValidityInSeconds();
		final Instant validity = Instant.now().plus(tokenValidityInSeconds, MILLIS);

		return Date.from(validity);
	}

	public String toToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
				.map(a -> a.getAuthority()
				.replaceAll("ROLE_", ""))
				.collect(Collectors.joining(","));

		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY, authorities)
				.signWith(SignatureAlgorithm.HS512, getTokenSecret())
				.setExpiration(getTokenValidity()).compact();
	}

	public Optional<Authentication> toAuthentication(String token) {
		if (!validateToken(token)) {
			return Optional.empty();
		}

		Claims claims = Jwts.parser()
				.setSigningKey(getTokenSecret())
				.parseClaimsJws(token)
				.getBody();

		String[] authorities = claims.get(AUTHORITIES_KEY).toString().split(",");

		UserDetails principal = org.springframework.security.core.userdetails.User.builder()
				.username(claims.getSubject())
				.password("").roles(authorities)
				.build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, token,
				principal.getAuthorities());

		return Optional.of(authentication);
	}

	private boolean validateToken(String authToken) {
		if (!StringUtils.hasText(authToken)) {
			return false;
		}
		try {
			Jwts.parser().setSigningKey(getTokenSecret()).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.info("Invalid JWT signature.");
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
		} catch (IllegalArgumentException e) {
			log.info("JWT token compact of handler are invalid.");
		}
		return false;
	}
}
