package com.armandorv.poc.tasks.service;

import org.springframework.stereotype.Service;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.repository.TaskRepository;

import reactor.core.publisher.Mono;

@Service
public class TaskService {

	private UserService userService;
	private TaskRepository taskRepository;

	public TaskService(UserService userService, TaskRepository taskRepository) {
		this.userService = userService;
		this.taskRepository = taskRepository;
	}

	public Mono<Task> createTask(String email, Task task) {
		return userService.getUserByEmail(email).map(task::withUser).flatMap(taskRepository::save);
	}

}
