package com.armandorv.poc.tasks.repository;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.armandorv.poc.tasks.domain.User;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	private Flux<User> users;
	
	@Before
	public void setUp() {
		users = this.userRepository.deleteAll()
				.thenMany(userRepository.saveAll(asList(
						   new User("user.some@gmail.com", "User", "Some", "secret"),
						   new User("user.other@gmail.com", "User", "Other", "secret"))));
		
		StepVerifier.create(users).expectNextCount(2).verifyComplete();
	}
	
	@Test
	public void should_GetAllTasks_When_NotGivenPageable() {
		StepVerifier.create(userRepository.findAll())
		.expectNextCount(2)
		.verifyComplete();
	}
	
	@Test
	public void should_GetFirstPage_When_GivenPageable() {
		StepVerifier.create(userRepository.findAll(PageRequest.of(1, 1)))
		.expectNextCount(1)
		.verifyComplete();
	}
	
	@Test
	public void should_GetUser_When_GivenId() { 
		final User user = users.blockFirst();
		
		StepVerifier.create(userRepository.findById(user.getId()))
		.expectNext(user)
		.verifyComplete();
	}
	
	@Test
	public void should_GetUser_When_GivenEmail() {
		final User user = users.blockFirst();
		
		StepVerifier.create(userRepository.findByEmailIgnoreCase(user.getEmail().toUpperCase()))
		.expectNext(user)
		.verifyComplete();
	}

	@Test
	public void should_CreateUser_When_GivenValidUser() {
		final User user = new User("some.new.user@gmail.com", "Some", "New user", "secret");
		
		StepVerifier.create(userRepository.save(user))
		
		.assertNext(result -> assertThat(result).isNotNull())
		.assertNext(result -> assertThat(result.getId()).isNotNull())
		.assertNext(result -> assertThat(result.getCreatedAt()).isNotNull())
		.assertNext(result -> assertThat(result.getLastModifiedAt()).isNotNull())
		
		.assertNext(result -> assertThat(result.getEmail()).isEqualTo(user.getEmail()))
		.assertNext(result -> assertThat(result.getFirstName()).isEqualTo(user.getFirstName()))
		.assertNext(result -> assertThat(result.getLastName()).isEqualTo(user.getLastName()));
	}
}
