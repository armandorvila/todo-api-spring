package com.armandorv.poc.tasks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.exception.UserAlreadyExistsException;
import com.armandorv.poc.tasks.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class UserServiceTests {

	private static final String ID = "57f4dadc6d138cf005711f4d";
	private static final String EMAIL = "user.some@gmail.com";
	private static final String PASS = "secret";
	
	private final User TEST_USER = new User(EMAIL, "", "");
	
	private UserService userService;
	
	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;
	
	@Before
	public void setUp() {
		passwordEncoder = mock(PasswordEncoder.class);
		userRepository = mock(UserRepository.class);
		userService = new UserService(userRepository, passwordEncoder);
	}
	
	@Test
	public void should_GetUser_When_GivenExistentEmail() {		
		given(userRepository.findByEmailIgnoreCase(EMAIL))
			.willReturn(Mono.just(TEST_USER));	
		
		StepVerifier.create( userService.getUserByEmail(EMAIL))
		.assertNext(user -> {
			assertThat(user).isNotNull();
			assertThat(user.getEmail()).isEqualTo(EMAIL);
			assertThat(user.getPassword()).isNull();
		})
		.verifyComplete();
						
		then(userRepository).should(times(1)).findByEmailIgnoreCase(EMAIL);
	}
	
	@Test
	public void should_GetEmpty_When_GivenNonExistentEmail() {		
		given(userRepository.findByEmailIgnoreCase(EMAIL))
			.willReturn(Mono.empty());	
		
		StepVerifier.create( userService.getUserByEmail(EMAIL))
		.expectNextCount(0)
		.verifyComplete();
						
		then(userRepository).should(times(1)).findByEmailIgnoreCase(EMAIL);
	}
	
	@Test
	public void should_CreateUser_When_EmailDoesNotExist() {		
		given(userRepository.findByEmailIgnoreCase(EMAIL))
			.willReturn(Mono.empty());
		
		given(passwordEncoder.encode(PASS))
			.willReturn(PASS);
		
		given(userRepository.save(TEST_USER))
			.willReturn(Mono.just(new User(ID, EMAIL, "", "", "", null, null)));
		
		StepVerifier.create(userService.createUser(TEST_USER))
		.assertNext(user -> {
			assertThat(user).isNotNull();
			assertThat(user.getId()).isEqualTo(ID);
			assertThat(user.getEmail()).isEqualTo(EMAIL);
			assertThat(user.getPassword()).isNull();
		})
		.verifyComplete();
						
		then(userRepository).should(times(1)).findByEmailIgnoreCase(EMAIL);
		then(userRepository).should(times(1)).save(TEST_USER);
	}
	
	@Test
	public void should_ThrowAnError_When_EmailDoesExist() {
		given(userRepository.findByEmailIgnoreCase(EMAIL))
			.willReturn(Mono.just(TEST_USER));
		
		given(passwordEncoder.encode(PASS))
			.willReturn(PASS);
		
		given(userRepository.save(TEST_USER))
			.willReturn(Mono.just(new User(ID, EMAIL, null, null, null, null, null)));
		
		StepVerifier.create(userService.createUser(TEST_USER))
		.expectNextCount(0)
		.expectError(UserAlreadyExistsException.class)
		.verify();
		
		then(userRepository).should(times(1)).findByEmailIgnoreCase(EMAIL);
		
		then(passwordEncoder).should(never()).encode(PASS);
		then(userRepository).should(never()).save(TEST_USER);
	}
}
