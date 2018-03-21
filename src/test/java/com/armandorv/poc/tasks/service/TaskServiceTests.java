package com.armandorv.poc.tasks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.repository.TaskRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TaskServiceTests {

	private static final String EMAIL = "user.some@gmail.com";
	private static final String USER_ID = "57f4dadc6d138cf005711f4d";
	
	private Task TEST_TASK = new Task("Some task");
	
	private TaskService taskService;
	
	private TaskRepository taskRepository;
	
	private UserService userService;
	
	@Before
	public void setUp() {
		userService = mock(UserService.class);
		taskRepository = mock(TaskRepository.class);
		
		taskService = new TaskService(userService, taskRepository);
	}
	
	@Test
	public void should_CreateTaskWhenGivenValidTask() {
		given(userService.getUserByEmail(EMAIL))
			.willReturn(Mono.just(new User(USER_ID, EMAIL, "", "", "", null, null)));	
		
		given(taskRepository.save(TEST_TASK))
			.willReturn(Mono.just(new Task(USER_ID, TEST_TASK.getSummary())));
		
		StepVerifier.create(taskService.createTask(EMAIL, TEST_TASK))
		.assertNext(task-> {
			assertThat(task.getUserId()).isEqualTo(USER_ID);
		})
		.verifyComplete();
		
		then(userService).should(times(1)).getUserByEmail(EMAIL);
		then(taskRepository).should(times(1)).save(TEST_TASK);
	}
}
