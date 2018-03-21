package com.armandorv.poc.tasks.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.exception.UserAlreadyExistsException;
import com.armandorv.poc.tasks.service.UserService;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(UserResource.class)
@AutoConfigureWebTestClient
public class UserResourceTests{

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private UserService userService;
	
	private User testUser = new User("user.some@gmail.com", "User", "Some");
	
	@Test  
	@WithMockUser(username = "user.some@gmail.com")
	public void should_GetCurrentUser_When_UserExists() throws Exception {
		given(userService.getUserByEmail(testUser.getEmail()))
		  .willReturn(Mono.just(testUser));
	    
		final User result =  webClient.get().uri("/users/me")
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBody(User.class)
				        .returnResult()
				        .getResponseBody();
		
		assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
		assertThat(result.getFirstName()).isEqualTo(testUser.getFirstName());
		assertThat(result.getLastName()).isEqualTo(testUser.getLastName());
		
		then(userService).should(times(1)).getUserByEmail(testUser.getEmail());
	}
	
	@Test  
	public void should_SignUpUser_When_UserIsValid_And_IsNotAuthenticated() throws Exception {
		final User validUser = new User("test@mail.com", "Test", "User", "secret");
		
		given(userService.createUser(validUser))
		  .willReturn(Mono.just(validUser));
	    
		 webClient.post().uri("/users/signup")
						.accept(APPLICATION_JSON)
						.syncBody(validUser)
						.exchange()
						.expectStatus().isCreated()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBody(User.class)
				        .isEqualTo(validUser);
	}	
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_UserEmailIsNotValid() throws Exception {		
		webClient.post().uri("/users/signup")
						.accept(APPLICATION_JSON)
						.syncBody(new User())
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(APPLICATION_JSON_UTF8);
	}
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_UserEmailIsNotPresent() throws Exception {		
		webClient.post().uri("/users/signup")
						.accept(APPLICATION_JSON)
						.syncBody(new User())
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(APPLICATION_JSON_UTF8);
	}
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_BodyNotPresent() throws Exception {		
		webClient.post().uri("/users/signup")
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(APPLICATION_JSON_UTF8);
	}
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_UserExists() throws Exception {
		final User validUser = new User("test@mail.com", "Test", "User", "secret");
		
		given(userService.createUser(validUser))
		.willReturn(Mono.error(new UserAlreadyExistsException()));
		
		webClient.post().uri("/users/signup")
						.accept(APPLICATION_JSON)
						.syncBody(validUser)
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(APPLICATION_JSON_UTF8);
		
		then(userService).should(times(1)).createUser(validUser);
	}
	
	@Test  
	public void should_GetInternalServerError_When_NullPointerIsThrown() throws Exception {
		final User validUser = new User("test@mail.com", "Test", "User", "secret");
		
		given(userService.createUser(validUser))
		.willReturn(Mono.error(new NullPointerException()));
		
		webClient.post().uri("/users/signup")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(validUser)
						.exchange()
						.expectStatus().is5xxServerError();
		
		then(userService).should(times(1)).createUser(validUser);
	}
}