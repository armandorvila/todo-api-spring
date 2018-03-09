package com.armandorv.poc.tasks.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.AbstractResourceTest;
import com.armandorv.poc.tasks.domain.Task;

@RunWith(SpringRunner.class)
public class TaskResourceTests extends AbstractResourceTest{

	@Autowired
	private WebTestClient webClient;

	@Test
	public void clientGetsListOfTasks_Authorized() throws Exception {
		List<Task> tasks = webClient.get().uri("/tasks")
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", authorization())
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
					.expectBodyList(Task.class)
					.hasSize(getTasks().size())
					.returnResult()
					.getResponseBody();
		
		assertThat(tasks.get(0).getId()).isEqualTo(getTasks().get(0).getId());
		assertThat(tasks.get(0).getSummary()).isEqualTo(getTasks().get(0).getSummary());
		assertThat(tasks.get(0).getCreatedAt()).isEqualTo(getTasks().get(0).getCreatedAt());
	}
	
	@Test
	public void clientGetsListOfTasks_Unauthorized() throws Exception {
		webClient.get().uri("/tasks")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isUnauthorized()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test
	public void clientGetsATasksWithTheGivenId_Authorized() throws Exception {
		final Task task = super.getTasks().get(0);
		
		webClient.get().uri("/tasks/{id}", task.getId())
						.accept(MediaType.APPLICATION_JSON)
						.header("Authorization", authorization())
		                .exchange()
				        .expectStatus().isOk()
				        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				        .expectBody(Task.class)
				        .isEqualTo(task);
	}
}
