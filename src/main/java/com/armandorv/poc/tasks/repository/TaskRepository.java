package com.armandorv.poc.tasks.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.armandorv.poc.tasks.domain.Task;

@Repository
public interface TaskRepository extends ReactiveCrudRepository<Task, String> {

}
