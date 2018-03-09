package com.armandorv.poc.tasks.resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.AbstractResourceTest;
import com.armandorv.poc.tasks.domain.User;

public class UserTokenResourceTests extends AbstractResourceTest { 

	@Autowired
	private WebTestClient webClient;
	
	@Test
	public void clientGetsToken_ValidCredentials() throws Exception {
		final User user = getUsers().get(0);
		
		webClient.post().uri("/authenticate")
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(user)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
					.expectBody()
					.jsonPath("$.token").isNotEmpty()
					.jsonPath("$.message").isNotEmpty();
	}
	
	@Test
	public void clientGetsToken_InvalidCredentials() throws Exception {
		final User user = new User("someinvalidemail@gmail.com", "some", "user");
		
		webClient.post().uri("/authenticate")
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(user)
					.exchange()
					.expectStatus().isUnauthorized()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
}
