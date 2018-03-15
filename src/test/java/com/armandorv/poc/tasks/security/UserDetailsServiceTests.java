package com.armandorv.poc.tasks.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class UserDetailsServiceTests {
	
	private User testUser = new User("some.email@gmail.com", "Some", "user", "secret");

	private UserRepository userRepository;
	
	private UserDetailsService userDetailsService;
	
	@Before
	public void setUp() {
		this.userRepository = mock(UserRepository.class);
		this.userDetailsService = new UserDetailsService(userRepository);
	}
	
	@Test
	public void should_GetUserDetails_When_UserIsFound() {		
		given(userRepository.findByEmailIgnoreCase(testUser.getEmail()))
		  .willReturn(Mono.just(testUser));
		
		Mono<UserDetails> userDetails = userDetailsService.findByUsername(testUser.getEmail());
		
		StepVerifier.create(userDetails).assertNext(u -> {
			assertThat(u).isNotNull();
			assertThat(u.getUsername()).isEqualTo(testUser.getEmail());
			assertThat(u.getPassword()).isEqualTo(testUser.getPassword());
		}).verifyComplete();
	}
	
	@Test
	public void should_GetEmptyMono_When_UserIsNotFound() {
		given(userRepository.findByEmailIgnoreCase("not_existent_username"))
		  .willReturn(Mono.empty());
		
		Mono<UserDetails> userDetails = userDetailsService.findByUsername("not_existent_username");
		
		StepVerifier.create(userDetails)
		.expectNextCount(0)
		.verifyComplete();
	}
}
