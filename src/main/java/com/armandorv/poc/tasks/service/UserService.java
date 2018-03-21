package com.armandorv.poc.tasks.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.exception.UserAlreadyExistsException;
import com.armandorv.poc.tasks.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UserService {
	
	private static final String USER_ALREADY_EXISTS_MSG = "There is already a user with email %s is.";

	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Mono<User> createUser(User user) {
		return userRepository.findByEmailIgnoreCase(user.getEmail())
			.flatMap(this::userAlreadyExistsError)
			.switchIfEmpty(this.saveUser(user));
	}
	
	private Mono<User> saveUser(User user){
		return Mono.just(user).subscribeOn(Schedulers.parallel())
				.map(u -> u.withPassword(passwordEncoder.encode(u.getPassword())))
				.flatMap(userRepository::save)
				.map(User::withoutPassword);
	}
	
	private Mono<User> userAlreadyExistsError(User next) {
		final String msg = String.format(USER_ALREADY_EXISTS_MSG, next.getEmail());
		return Mono.error(new UserAlreadyExistsException(msg));
	}

	public Mono<User> getUserByEmail(String email) {
		return userRepository.findByEmailIgnoreCase(email).map(User::withoutPassword);
	}

}
