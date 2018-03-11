package com.armandorv.poc.tasks.security.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JWTAuthenticationProvider {

	private static final String AUTHORITIES_KEY = "auth";

	private ApplicationProperties properties;
	
    private String secretKey;

    private long tokenValidityInMilliseconds;

	public JWTAuthenticationProvider(ApplicationProperties properties) {
		this.properties = properties;
	}
	
    @PostConstruct
    public void init() {
        this.secretKey = properties.getSecurity().getJwt().getSecret();
        this.tokenValidityInMilliseconds = 1000 * properties.getSecurity().getJwt().getTokenValidityInSeconds();
    }

	public String toToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
				.map(a -> a.getAuthority()
				.replaceAll("ROLE_", ""))
				.collect(Collectors.joining(","));
		
		 Date validity = new Date(new Date().getTime() + this.tokenValidityInMilliseconds);

		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY, authorities)
				.signWith(SignatureAlgorithm.HS512, this.secretKey)
				.setExpiration(validity).compact();
	}

	public Mono<Authentication> getAuthentication(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(this.secretKey)
				.parseClaimsJws(token)
				.getBody();

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
			Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(authToken);
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
