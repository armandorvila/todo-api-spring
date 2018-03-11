package com.armandorv.poc.tasks.resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.AbstractIntTest;
import com.armandorv.poc.tasks.resource.dto.UserCredentialsDTO;

public class UserTokenResourceIntTests extends AbstractIntTest { 

	@Autowired
	private WebTestClient webClient;
	
	@Test
	public void should_GetUserToken_When_ValidCredentials() throws Exception {
		
		webClient.post().uri("/authenticate")
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(userCredentials())
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
					.expectBody()
					.jsonPath("$.token").isNotEmpty()
					.jsonPath("$.message").isNotEmpty();
	}
	
	@Test
	public void should_Get401_When_NotValidCredentials() throws Exception {
		final UserCredentialsDTO credentials = new UserCredentialsDTO("someinvalidemail@gmail.com", "secret");
		
		webClient.post().uri("/authenticate")
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(credentials)
					.exchange()
					.expectStatus().isUnauthorized()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
}
