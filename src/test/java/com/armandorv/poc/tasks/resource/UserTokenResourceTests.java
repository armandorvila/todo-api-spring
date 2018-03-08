package com.armandorv.poc.tasks.resource;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.AbstractResourceTest;
import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.resource.dto.UserTokenDTO;

public class UserTokenResourceTests extends AbstractResourceTest { 

	@Autowired
	private WebTestClient webClient;
	
	@Test
	public void clientGetsToken_ValidCredentials() throws Exception {
		final User user = getUsers().get(0);
		
		UserTokenDTO token = webClient.post().uri("/authenticate")
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(user)
					.exchange()
					.expectStatus().isOk()
					.expectBody(UserTokenDTO.class)
			        .returnResult()
			        .getResponseBody();
		
		assertThat(token).isNotNull();
		assertThat(token.getToken()).isNotNull();
		assertThat(token.getMessage()).isNotNull();
	}
	
	@Test
	public void clientGetsToken_InvalidCredentials() throws Exception {
		final User user = new User("someinvalidemail@gmail.com", "some", "user");
		
		webClient.post().uri("/authenticate")
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(user)
					.exchange()
					.expectStatus().isUnauthorized();
	}
}
