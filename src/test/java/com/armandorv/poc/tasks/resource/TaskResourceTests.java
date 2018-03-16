package com.armandorv.poc.tasks.resource;

import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.repository.TaskRepository;
import com.armandorv.poc.tasks.resource.TaskResource;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(TaskResource.class)
public class TaskResourceTests {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private TaskRepository taskRepository;
	
	private Task testTask = new Task("57f4dadc6d138cf005711f4d", "Some task");
	
	@Test
	public void should_GetAllTasks_When_NotSpecifiedPagination() throws Exception {		
		given(taskRepository.findAll())
		  .willReturn(Flux.just(testTask));
		
		webClient.get().uri("/tasks")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
					.expectBodyList(Task.class)
					.hasSize(1)
					.contains(testTask);
	}
	
	@Test
	public void should_GetTask_When_GivenExistentId() throws Exception {
		given(taskRepository.findById(testTask.getId()))
		  .willReturn(Mono.just(testTask));
		
		webClient.get().uri("/tasks/{id}", testTask.getId())
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
					.expectBody(Task.class)
					.isEqualTo(testTask);
	}

	@Test
	public void should_Get404_When_GivenNonExistentId() throws Exception {
		given(taskRepository.findById("someid"))
		  .willReturn(Mono.empty());
		
		webClient.get().uri("/tasks/{id}", "someid")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.isEmpty();
	}
}
