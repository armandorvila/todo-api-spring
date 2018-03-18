package com.armandorv.poc.tasks;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import org.junit.Test;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.resource.dto.UserCredentialsDTO;

public class UsersApplicationTests extends ApplicationTests {
	
	@Test
	public void should_GetCurrentUser_When_Authorized() throws Exception {		
		webClient.get().uri("/users/me")
						.accept(APPLICATION_JSON)
						.header("Authorization", authorization())
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.id").isNotEmpty()
						.jsonPath("$.createdAt").isNotEmpty()
						.jsonPath("$.password").doesNotExist()
						.jsonPath("$.email").isEqualTo(loggedUser().getEmail());
	}
	
	@Test
	public void should_GetUnauthorized_When_TokenIsNotProvided() throws Exception {
		webClient.get().uri("/users/me")
					.accept(APPLICATION_JSON)
					.exchange()
					.expectStatus().isUnauthorized();
	}
	
	@Test
	public void should_GetBadRequest_When_UserIsInvalid() throws Exception {
		final User user = new User("notavalidemail", "New", "User", "secret");
		
		webClient.post().uri("/users/signup")
								.accept(APPLICATION_JSON)
								.syncBody(user)
								.exchange()
								.expectStatus().isBadRequest()
								.expectHeader().contentType(APPLICATION_JSON_UTF8)
								.expectBody(User.class)
								.returnResult()
								.getResponseBody();
	}
	
	@Test
	public void should_GetToken_When_ValidCredentials() throws Exception {
		webClient.post().uri("/authenticate")
					.accept(APPLICATION_JSON)
					.syncBody(userCredentials())
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(APPLICATION_JSON_UTF8)
					.expectBody()
					.jsonPath("$.token").isNotEmpty()
					.jsonPath("$.message").isNotEmpty();
	}
	
	@Test
	public void should_GetUnauthorized_When_NotValidCredentials() throws Exception {
		final UserCredentialsDTO credentials = new UserCredentialsDTO("someinvalidemail@gmail.com", "secret");
		
		webClient.post().uri("/authenticate")
					.accept(APPLICATION_JSON)
					.syncBody(credentials)
					.exchange()
					.expectStatus().isUnauthorized()
					.expectHeader().contentType(APPLICATION_JSON_UTF8);
	}
	
	@Test
	public void should_RegisterUser_When_UserIsValid() throws Exception {
		final User user = new User("some.user@gmail.com", "Some", "User", "secret");
		
		webClient.post().uri("/users/signup")
								.accept(APPLICATION_JSON)
								.syncBody(user)
								.exchange()
								.expectStatus().isCreated()
								.expectHeader().contentType(APPLICATION_JSON_UTF8)
								.expectBody()
								.jsonPath("$.id").isNotEmpty()
								.jsonPath("$.createdAt").isNotEmpty()
								.jsonPath("$.password").doesNotExist()
								.jsonPath("$.email").isEqualTo(user.getEmail());
	}
}
