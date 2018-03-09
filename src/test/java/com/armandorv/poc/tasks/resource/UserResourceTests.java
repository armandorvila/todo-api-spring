package com.armandorv.poc.tasks.resource;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.AbstractResourceTest;
import com.armandorv.poc.tasks.domain.User;

@RunWith(SpringRunner.class)
public class UserResourceTests extends AbstractResourceTest {

	@Autowired
	private WebTestClient webClient;

	@Test
	public void clientGetsCurrentUser_Authorized() throws Exception {		
		final User result =  webClient.get().uri("/users/me")
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", authorization())
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody(User.class)
				        .returnResult()
				        .getResponseBody();
		
		assertThat(result.getId()).isEqualTo(loggedUser().getId());
		assertThat(result.getEmail()).isEqualTo(loggedUser().getEmail());
		assertThat(result.getFirstName()).isEqualTo(loggedUser().getFirstName());
		assertThat(result.getLastName()).isEqualTo(loggedUser().getLastName());
	}
	
	@Test
	public void clientGetsCurrentUser_Unauthorized() throws Exception {
		webClient.get().uri("/users/me")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isUnauthorized();
	}
	
	@Test
	public void clientSingsUp() throws Exception {
		final User user = new User("new.user@gmail.com", "New", "User", "secret");
		
		final User result = webClient.post().uri("/users/signup")
								.accept(MediaType.APPLICATION_JSON)
								.syncBody(user)
								.exchange()
								.expectStatus().isCreated()
								.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
								.expectBody(User.class)
								.returnResult()
								.getResponseBody();
		
		assertThat(result.getId()).isNotNull();
		assertThat(result.getCreatedAt()).isNotNull();
		assertThat(result.getLastModifiedAt()).isNotNull();
		assertThat(result.getEmail()).isEqualTo(user.getEmail());
		assertThat(result.getFirstName()).isEqualTo(user.getFirstName());
		assertThat(result.getLastName()).isEqualTo(user.getLastName());
	}
}
