package com.armandorv.poc.tasks.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.armandorv.poc.tasks.domain.Task;
import com.armandorv.poc.tasks.domain.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

	Mono<User> findByEmailIgnoreCase(String email);
	
    @Query("{ id: { $exists: true }}")
    Flux<Task> findAll(final Pageable page);
}
