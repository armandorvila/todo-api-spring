package com.armandorv.poc.tasks.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TasksUserDetailsService implements ReactiveUserDetailsService {

	private static final String ROLE_USER = "USER";
	@Autowired
	private UserRepository userRepository;

	@Override
	public Mono<UserDetails> findByUsername(String email) {
		return userRepository.findByEmailIgnoreCase(email).doOnSuccess(user -> {
			if(user == null) {
				log.debug("User not found for login {}", email);
			}
			else {
				log.debug("Found user {}", user);
				
			}
		}).map(user -> toUserDetails(user));
	}

	private UserDetails toUserDetails(User user) {
		
		return org.springframework.security.core.userdetails.User.builder()
		.username(user.getEmail())
		.password(user.getPassword())
		.roles(ROLE_USER)
		.build();
	}

}
