package com.armandorv.poc.tasks.resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.AbstractResourceTest;
import com.armandorv.poc.tasks.resource.dto.UserCredentialsDTO;

public class UserTokenResourceTests extends AbstractResourceTest { 

	@Autowired
	private WebTestClient webClient;
	
	@Test
	public void clientGetsToken_ValidCredentials() throws Exception {
		
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
	public void clientGetsToken_InvalidCredentials() throws Exception {
		final UserCredentialsDTO credentials = new UserCredentialsDTO("someinvalidemail@gmail.com", "secret");
		
		webClient.post().uri("/authenticate")
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(credentials)
					.exchange()
					.expectStatus().isUnauthorized()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
}
