package com.armandorv.poc.tasks.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.armandorv.poc.tasks.domain.Task;

import reactor.core.publisher.Flux;

@Repository
public interface TaskRepository extends ReactiveMongoRepository<Task, String> {

    @Query("{ id: { $exists: true }}")
    Flux<Task> findAll(final Pageable page);
}
