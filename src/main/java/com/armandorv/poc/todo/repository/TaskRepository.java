package com.armandorv.poc.todo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.armandorv.poc.todo.domain.Task;

@Repository
public interface TaskRepository extends ReactiveCrudRepository<Task, String> {

}
