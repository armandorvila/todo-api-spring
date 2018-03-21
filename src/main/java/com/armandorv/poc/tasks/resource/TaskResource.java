package com.armandorv.poc.tasks.resource;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.repository.TaskRepository;
import com.armandorv.poc.tasks.security.SecurityContextUtils;
import com.armandorv.poc.tasks.service.TaskService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TaskResource {

	private TaskService taskService;
	
	private TaskRepository taskRepository;

	public TaskResource(TaskService taskService, TaskRepository taskRepository) {
		this.taskService = taskService;
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
	
	@PostMapping("/tasks")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Task> createTask(@Valid @RequestBody Task task) {
		return SecurityContextUtils.getPrincipal()
				.flatMap(email -> taskService.createTask(email, task));
	}
}
