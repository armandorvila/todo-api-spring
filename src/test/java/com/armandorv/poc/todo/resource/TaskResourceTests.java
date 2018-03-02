package com.armandorv.poc.todo.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorv.poc.todo.domain.Task;


@RunWith(SpringRunner.class)
@WebFluxTest(TaskResource.class)
public class TaskResourceTests {

	@Autowired
    private WebTestClient webClient;
	
	@Test
    public void userGetsAListOfTasks() throws Exception {
    	
       webClient.get().uri("/tasks").accept(MediaType.APPLICATION_JSON)
        		.exchange()
		        .expectStatus().isOk()
		        .expectBodyList(Task.class)
		        .hasSize(2)
                .contains(new Task("1- Call my mom."));
        
    }
	
	@Test
    public void userGetsATasksWithTheGivenId() throws Exception {
    	
        webClient.get().uri("/tasks/{id}", "507f1f77bcf86cd799439011").accept(MediaType.APPLICATION_JSON)
        		.exchange()
		        .expectStatus().isOk()
		        .expectBody(Task.class)
		        .isEqualTo(new Task("1- Call my mom."));
    }
}
