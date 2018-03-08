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
	
	@Getter
	private List<Task> tasks;
	
	@Getter
	private List<User> users;
	
	private UserTokenDTO token;

	@Before
	public void setUp() {
		this.taskRepository.deleteAll().block();
		this.userRepository.deleteAll().block();

		this.tasks = taskRepository.saveAll(
				asList(new Task("Research blokchain applications."), 
						new Task("Call mom...."),
						new Task("Prepare PoC"))).collectList().block();
		
		this.users = userRepository.saveAll(
				asList(new User("user.some@gmail.com", "User", "Some", "secret"),
					   new User("user.other@gmail.com", "User", "Other", "secret"))).collectList().block();
	}
	
	public String authorization() throws Exception {
		if(token == null) {
			token = webClient.post().uri("/authenticate")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.syncBody(users.get(0))
					.exchange()
					.expectStatus().isOk()
					.expectBody(UserTokenDTO.class)
					.returnResult().getResponseBody();
		}

		return String.format("Bearer %s", token.getToken());
	}
}
