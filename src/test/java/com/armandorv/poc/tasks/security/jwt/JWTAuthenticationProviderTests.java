package com.armandorv.poc.tasks.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class JWTAuthenticationProviderTests {	
	
	private JWTAuthenticationProvider jwtAuthenticationProvider;
	
	private Authentication authentication;
	
	private JwtBuilder jwtBuilder;
	
	private JwtParser jwtParser;
	
	@Before
	public void setUp() {
		final UserDetails principal = User.builder()
		.username("username@mail.com")
		.password("").roles("USER")
		.build();
		
		this.authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
				
		this.jwtBuilder = Jwts.builder()
				.signWith(SignatureAlgorithm.HS512, "secret")
				.setExpiration(new Date(Long.MAX_VALUE));
		
		this.jwtParser = Jwts.parser().setSigningKey("secret");
				
		this.jwtAuthenticationProvider = new JWTAuthenticationProvider(jwtBuilder, jwtParser);
	}
	
	@Test
	public void should_GetAuthentication_When_ValidToken() {
		final String testToken =  this.jwtBuilder
				.setSubject("username@mail.com")
				.claim("auth", "USER")
				.compact();

		Mono<Authentication> authentication = jwtAuthenticationProvider.getAuthentication(testToken);
		
		StepVerifier.create(authentication)
		.assertNext(auth -> {
			assertThat(auth).isNotNull();
			assertThat(auth.getName()).isEqualTo("username@mail.com");
			assertThat(auth.getAuthorities()).isNotEmpty();
		})
		.verifyComplete();
	}
	
	@Test
	public void should_GetToken_When_ValidAuthentication() {
		final String token = jwtAuthenticationProvider.getToken(this.authentication);
		
		assertThat(token).isNotNull();
		assertThat(jwtAuthenticationProvider.validateToken(token)).isTrue();
	}
	
	@Test
	public void should_NotValidateToken_When_Empty() {
		assertThat(jwtAuthenticationProvider.validateToken("")).isFalse();
	}
	
	@Test
	public void should_NotValidateToken_When_Null() {
		assertThat(jwtAuthenticationProvider.validateToken(null)).isFalse();
	}
	
	@Test
	public void should_NotValidateToken_When_Malformed() {
		assertThat(jwtAuthenticationProvider.validateToken("somemmalformedtoken")).isFalse();
	}
	
	@Test
	public void should_NotValidateToken_When_Unsigned() {
	final 	String token = Jwts.builder().setSubject("username").compact();
		assertThat(jwtAuthenticationProvider.validateToken(token)).isFalse();
	}
	
	@Test
	public void should_NotValidateToken_When_BadSignature() {
		final String testToken =  this.jwtBuilder
				.signWith(SignatureAlgorithm.HS512, "anothersecret")
				.compact();

		boolean authentication = jwtAuthenticationProvider.validateToken(testToken);
		assertThat(authentication).isFalse();
	}
	
	@Test
	public void should_NotValidateToken_When_Expired() {
		final String testToken =  this.jwtBuilder
				.setExpiration(new Date(0))
				.compact();

		assertThat(jwtAuthenticationProvider.validateToken(testToken)).isFalse();
	}
}
