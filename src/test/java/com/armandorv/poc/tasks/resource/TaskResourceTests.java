package com.armandorv.poc.tasks.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.repository.TaskRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskResourceTests {

	@Autowired
	private WebTestClient webClient;

	@Autowired
	private TaskRepository taskRepository;

	private Task task = new Task("Research blokchain applications.");
	
	@Before
	public void setUp() {
		this.taskRepository.deleteAll().block();
		this.task = taskRepository.save(task).block();
	}

	@Test
	public void userGetsAListOfTasks() throws Exception {
		webClient.get().uri("/tasks").accept(MediaType.APPLICATION_JSON)
				 .exchange()
				 .expectStatus().isOk()
				 .expectBodyList(Task.class)
				 .hasSize(1)
				 .contains(task);
	}

	@Test
	public void userGetsATasksWithTheGivenId() throws Exception {
		webClient.get().uri("/tasks/{id}", task.getId()).accept(MediaType.APPLICATION_JSON)
		               .exchange()
				       .expectStatus().isOk()
				       .expectBody(Task.class)
				       .isEqualTo(task);
	}
}
