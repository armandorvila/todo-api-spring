package com.armandorv.poc.tasks.resource;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.service.UserService;

import reactor.core.publisher.Mono;

@RestController
public class UserResource {

	private UserService userService;

	public UserResource(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/users/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<@Valid User> createUser(@Valid @RequestBody User user) {
		return userService.createUser(user);
	}

	@GetMapping("/users/me")
	public Mono<User> me() {
		return ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication().getName())
				.flatMap(this.userService::getUserByEmail);
	}
}