package com.armandorv.poc.tasks.resource;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.repository.UserRepository;

import reactor.core.publisher.Mono;

@RestController
public class UserResource {
	
	private UserRepository userRepository;
	
	private PasswordEncoder encoder;

	public UserResource(UserRepository userRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
	}
	
    @PostMapping("/users/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<@Valid User> createUser(@Valid @RequestBody User user) {
    	
    	return Mono.just(user)
				   .doOnNext(u -> u.setPassword(this.encoder.encode(u.getPassword())))
				   .flatMap(this.userRepository::save);
    }
    
    @GetMapping("/users/me")
    public Mono<User> me(Principal principal) {
    	return userRepository.findByEmailIgnoreCase(principal.getName());
    }
 
}