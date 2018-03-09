package com.armandorv.poc.tasks;

import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.repository.TaskRepository;
import com.armandorv.poc.tasks.repository.UserRepository;
import com.armandorv.poc.tasks.resource.dto.UserTokenDTO;

import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractResourceTest {
	
	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Getter
	@Autowired
	private WebTestClient webClient;
	
	private Flux<Task> tasks;
	
	private Flux<User> users;
	
	private UserTokenDTO token;

	@Before
	public void loadTasks() {
		tasks = this.taskRepository.deleteAll()
				.thenMany(taskRepository.saveAll(asList(
						new Task("Research blokchain applications."),
						new Task("Implemnet simple trading system."),
						new Task("Prepare PoC"))));
		
		StepVerifier.create(tasks).expectNextCount(3).verifyComplete();
	}
	
	@Before
	public void loadUsers() {		
		users = this.userRepository.deleteAll()
				.thenMany(userRepository.saveAll(asList(
						   new User("user.some@gmail.com", "User", "Some", "secret"),
						   new User("user.other@gmail.com", "User", "Other", "secret"))));
		
		StepVerifier.create(users).expectNextCount(2).verifyComplete();
	}
	
	public String authorization() throws Exception {
		if(token == null) {
			token = webClient.post().uri("/authenticate")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(loggedUser())
					.exchange()
					.expectStatus().isOk()
					.expectBody(UserTokenDTO.class)
					.returnResult().getResponseBody();
		}
		return String.format("Bearer %s", token.getToken());
	}
	
	public User loggedUser() {
		return this.users.blockFirst();
	}
	
	public List<User> getUsers() {
		return this.users.collectList().block();
	}
	
	public List<Task> getTasks() {
		return this.tasks.collectList().block();
	}
}
