package com.armandorv.poc.tasks.resource;

import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.resource.dto.UserCredentialsDTO;
import com.armandorv.poc.tasks.security.jwt.JWTAuthenticationProvider;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(UserTokenResource.class)
@AutoConfigureWebTestClient
public class UserTokenResourceTests {


	@Autowired
	private WebTestClient webClient;

	@MockBean
	private ReactiveAuthenticationManager authenticationManager;
	
	@MockBean
	private JWTAuthenticationProvider tokenProvider; 
	
	@Test  
	public void should_GetToken_When_ValidCredentialsPresent() throws Exception {
		UserCredentialsDTO credentials = new UserCredentialsDTO("test@mail", "pass");
		
		UsernamePasswordAuthenticationToken authentication =
	            new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
		
		given(authenticationManager.authenticate(authentication))
		  .willReturn(Mono.just(authentication));
		
		given(tokenProvider.getToken(authentication))
		  .willReturn("someToken");
		
		webClient.post().uri("/authenticate")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(credentials)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.token").isNotEmpty()
						.jsonPath("$.message").isNotEmpty()
						.jsonPath("$.token").isEqualTo("someToken");
	}
	
	@Test  
	public void should_GetBadRequest_When_CredentialsNotPresent() throws Exception {		
		webClient.post().uri("/authenticate")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(new UserCredentialsDTO())
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
	@Test  
	public void should_GetBadRequest_When_InvalidEmail() throws Exception {		
		webClient.post().uri("/authenticate")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(new UserCredentialsDTO("test", "test"))
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
	@Test  
	public void should_GetBadRequest_When_EmptyPassword() throws Exception {		
		webClient.post().uri("/authenticate")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(new UserCredentialsDTO("test", "test"))
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
}
