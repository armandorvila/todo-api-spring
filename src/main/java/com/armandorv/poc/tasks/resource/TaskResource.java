package com.armandorv.poc.tasks.resource;

import static org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.repository.TaskRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TaskResource {

	private TaskRepository taskRepository;

	public TaskResource(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	@GetMapping("/tasks")
	public Flux<Task> listTasks() {
		return taskRepository.findAll();
	}

	@GetMapping("/tasks/{id}")
	public Mono<ResponseEntity<Task>> getTask(@PathVariable("id") String id) {
		return taskRepository.findById(id)
				.map(task -> new ResponseEntity<>(task, OK))
				.defaultIfEmpty(new ResponseEntity<>(NOT_FOUND));
	}
}
