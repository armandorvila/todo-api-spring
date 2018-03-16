package com.armandorv.poc.tasks;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.armandorv.poc.tasks.domain.Task;

public class TasksApplicationTests extends ApplicationTests {
	
	@Test
	public void should_ListTasks_When_Authorized() throws Exception {
		webClient.get().uri("/tasks")
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", authorization())
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
					.expectBodyList(Task.class)
					.hasSize(getTasks().size())
					.contains(getTasks().get(0));
	}
	
	@Test  
	public void should_Get401_When_NotAuthorized() throws Exception {
		webClient.get().uri("/tasks")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isUnauthorized()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test    
	public void should_GetTask_When_GivenIdAndAuthorized() throws Exception {
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
