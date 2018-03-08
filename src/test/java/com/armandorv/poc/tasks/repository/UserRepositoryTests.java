package com.armandorv.poc.tasks.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.armandorv.poc.tasks.domain.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	private User user = new User("user@gmail.com", "Some", "User");
	
	@Before
	public void setUp() {
		this.userRepository.deleteAll().block();
		this.user = userRepository.save(user).block();
	}
	
	@Test
	public void testFindOne() {
		User task = userRepository.findById(this.user.getId()).block();
		
		assertThat(task).isNotNull();
		assertThat(task).hasFieldOrPropertyWithValue("id", this.user.getId());
		assertThat(task).hasFieldOrPropertyWithValue("email", this.user.getEmail());
		assertThat(task).hasFieldOrPropertyWithValue("firstName", this.user.getFirstName());
		assertThat(task).hasFieldOrPropertyWithValue("lastName", this.user.getLastName());
	}
	
	@Test
	public void testFindAll() {
		Iterable<User> users = userRepository.findAll().toIterable();
		
		assertThat(users).hasSize(1);
		assertThat(users).contains(this.user);
	}

}
