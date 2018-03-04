package com.armandorv.poc.tasks.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.repository.TaskRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskRepositoryTests {

	@Autowired
	private TaskRepository taskRepository;

	private Task task = new Task("Research blokchain applications.");
	
	@Before
	public void setUp() {
		this.taskRepository.deleteAll().block();
		this.task = taskRepository.save(task).block();
	}
	
	@Test
	public void testFindOne() {
		Task task = taskRepository.findById(this.task.getId()).block();
		
		assertThat(task).isNotNull();
		assertThat(task).hasFieldOrPropertyWithValue("id", this.task.getId());
		assertThat(task).hasFieldOrPropertyWithValue("summary", this.task.getSummary());
	}
	
	@Test
	public void testFindAll() {
		Iterable<Task> tasks = taskRepository.findAll().toIterable();
		
		assertThat(tasks).hasSize(1);
		assertThat(tasks).contains(task);
	}

}
