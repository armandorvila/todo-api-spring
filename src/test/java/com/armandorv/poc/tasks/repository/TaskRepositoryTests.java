package com.armandorv.poc.tasks.repository;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.armandorv.poc.tasks.domain.Task;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class TaskRepositoryTests {

	@Autowired
	private TaskRepository taskRepository;
	
	private Flux<Task> tasks;
	
	@Before
	public void setUp() {
		tasks = this.taskRepository.deleteAll()
				.thenMany(taskRepository.saveAll(asList(
						new Task("Research blokchain applications."),
						new Task("Implemnet simple trading system."))));
		
		StepVerifier.create(tasks).expectNextCount(2).verifyComplete();
	}
	
	@Test
	public void shouldGetAllTheTasks() {
		StepVerifier.create(taskRepository.findAll())
		.expectNextCount(2)
		.verifyComplete();
	}
	
	@Test
	public void shouldGetTaskById() {
		final Task task = tasks.blockFirst();
		
		StepVerifier.create(taskRepository.findById(task.getId()))
		.expectNext(task)
		.verifyComplete();
	}
	
	@Test
	public void shouldCreateTask() {
		final Task task = new Task("Some pointless task");
		
		StepVerifier.create(taskRepository.save(task))
		
		.assertNext(result -> assertThat(result).isNotNull())
		.assertNext(result -> assertThat(result.getId()).isNotNull())
		.assertNext(result -> assertThat(result.getCreatedAt()).isNotNull())
		.assertNext(result -> assertThat(result.getLastModifiedAt()).isNotNull())
		
		.assertNext(result -> assertThat(result.getSummary()).isEqualTo(task.getSummary()));
	}

}
