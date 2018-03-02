package com.armandorv.poc.todo.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.armandorv.poc.todo.domain.Task;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TaskResource {

	@GetMapping("/tasks")
	Flux<Task> listTasks() {
		return Flux.just(
				new Task("1- Call my mom."),
				new Task("2- Research blokchain applications for the media market."));
	}
	
	@GetMapping("/tasks/{id}")
	Mono<Task> getTask(String id) {
		return Mono.just(new Task("1- Call my mom."));
	}
}
