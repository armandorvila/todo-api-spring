package com.armandorv.poc.tasks.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(UserResource.class)
@AutoConfigureWebTestClient
public class UserResourceTests{

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private PasswordEncoder passwordEncoder;
	
	private User testUser = new User("user.some@gmail.com", "User", "Some");
	
	@Test  
	@WithMockUser(username = "user.some@gmail.com")
	public void should_GetCurrentUser_When_UserExists() throws Exception {
		given(userRepository.findByEmailIgnoreCase(testUser.getEmail()))
		  .willReturn(Mono.just(testUser));
	    
		final User result =  webClient.get().uri("/users/me")
						.accept(MediaType.APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody(User.class)
				        .returnResult()
				        .getResponseBody();
		
		assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
		assertThat(result.getFirstName()).isEqualTo(testUser.getFirstName());
		assertThat(result.getLastName()).isEqualTo(testUser.getLastName());
	}
	
	@Test  
	public void should_SignUpUser_When_UserISValid_And_IsNotAuthenticated() throws Exception {
		final User validUser = new User("test@mail.com", "Test", "User", "secret");
		
		given(passwordEncoder.encode(validUser.getPassword()))
		  .willReturn(validUser.getPassword());
		
		given(userRepository.save(validUser))
		  .willReturn(Mono.just(validUser));
	    
		 webClient.post().uri("/users/signup")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(validUser)
						.exchange()
						.expectStatus().isCreated()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody(User.class)
				        .isEqualTo(validUser);
		 
		 then(passwordEncoder).should().encode(validUser.getPassword());
		 then(userRepository).should().save(validUser);
	}	
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_UserEmailIsNotValid() throws Exception {
		final User invalidUser = new User("test", "Test", "User", "secret");
		
		webClient.post().uri("/users/signup")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(new User())
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
		
		then(userRepository).should(never()).save(invalidUser);
	}
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_UserEmailIsNotPresent() throws Exception {
		final User invalidUser = new User(null, "Test", "User", "secret");
		
		webClient.post().uri("/users/signup")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(new User())
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
		
		then(userRepository).should(never()).save(invalidUser);
	}
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_BodyNotPresent() throws Exception {		
		webClient.post().uri("/users/signup")
						.accept(MediaType.APPLICATION_JSON)
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
	@Test  
	public void should_GetBadRequest_When_Singup_And_UserExists() throws Exception {
		final User validUser = new User("test@mail.com", "Test", "User", "secret");
		
		given(passwordEncoder.encode(validUser.getPassword()))
		  .willReturn(validUser.getPassword());
	
		given(userRepository.save(validUser))
		  .willThrow(DuplicateKeyException.class);
		
		webClient.post().uri("/users/signup")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(validUser)
						.exchange()
						.expectStatus().isBadRequest()
						.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
	@Test  
	public void should_GetInternalServerError_When_NullPointerIsThrown() throws Exception {
		final User validUser = new User("test@mail.com", "Test", "User", "secret");
		
		given(passwordEncoder.encode(validUser.getPassword()))
		  .willThrow(NullPointerException.class);
		
		webClient.post().uri("/users/signup")
						.accept(MediaType.APPLICATION_JSON)
						.syncBody(validUser)
						.exchange()
						.expectStatus().is5xxServerError();
	}
}