package com.armandorv.poc.tasks.domain;

import java.time.Instant;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"createdAt", "lastModifiedAt"})
@Document(collection = "tasks")
public class Task {
	
	@Id
	private String id;

	@NotEmpty(message = "This field is required")
	private String summary;
	
	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant createdAt;

	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant lastModifiedAt;
	
	public Task(String summary) {
		this.summary = summary;
	}
	
	public Task(String id, String summary) {
		this.id = id;
		this.summary = summary;
	}
}
