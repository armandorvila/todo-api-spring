package com.armandorv.poc.tasks.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.repository.UserRepository;

import reactor.core.publisher.Mono;

@Component
public class UserDetailsService implements ReactiveUserDetailsService {

	private UserRepository userRepository;
	
	public UserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Mono<UserDetails> findByUsername(String email) {
		return userRepository.findByEmailIgnoreCase(email).map(this::toUserDetails);
	}

	private UserDetails toUserDetails(User user) {
		return org.springframework.security.core.userdetails.User.builder()
		.username(user.getEmail())
		.password(user.getPassword())
		.roles(SecurityRoles.ROLE_USER)
		.build();
	}

}
