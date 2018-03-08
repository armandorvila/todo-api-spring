package com.armandorv.poc.tasks.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.IntegrationTestSupport;
import com.armandorv.poc.tasks.domain.Task;

@RunWith(SpringRunner.class)
public class TaskResourceTests extends IntegrationTestSupport{

	@Autowired
	private WebTestClient webClient;

	@Test
	public void clientGetsListOfTasks_Authorized() throws Exception {
		webClient.get().uri("/tasks")
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", authorization())
					.exchange()
					.expectStatus().isOk()
					.expectBodyList(Task.class)
					.hasSize(getTasks().size())
					.contains(getTasks().get(0));
	}
	
	@Test
	public void clientGetsListOfTasks_Unauthorized() throws Exception {
		webClient.get().uri("/tasks")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isUnauthorized();
	}

	@Test
	public void clientGetsATasksWithTheGivenId_Authorized() throws Exception {
		final Task task = super.getTasks().get(0);
		
		webClient.get().uri("/tasks/{id}", task.getId())
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", authorization())
		                .exchange()
				        .expectStatus().isOk()
				        .expectBody(Task.class)
				        .isEqualTo(task);
	}
}
