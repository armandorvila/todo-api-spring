package com.armandorv.poc.todo.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.armandorv.poc.todo.domain.Task;
import com.armandorv.poc.todo.repository.TaskRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TaskResource {

	@Autowired
	private TaskRepository taskRepository;
	
	@GetMapping("/tasks")
	Flux<Task> listTasks() {
		return taskRepository.findAll();
	}
	
	@GetMapping("/tasks/{id}")
	Mono<Task> getTask(@PathVariable("id") String id) {
		return taskRepository.findById(id);
	}
}
