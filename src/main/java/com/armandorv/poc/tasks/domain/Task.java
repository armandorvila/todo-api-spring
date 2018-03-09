package com.armandorv.poc.tasks.domain;

import java.time.Instant;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tasks")
public class Task {
	
	@Id
	private String id;

	@NotEmpty(message = "This field is required")
	private String summary;
	
	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant lastModifiedAt;
	
	public Task(String summary) {
		this.summary = summary;
	}
}
