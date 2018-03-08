package com.armandorv.poc.tasks.domain;

import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

	@Id
	private String id;

	@Indexed
	@NotNull
	private String email;

	@NotNull
	private String firstName;

	@NotNull
	private String lastName;
	
	private String password;

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant lastModifiedAt;

	public User(String email, String firstName, String lastName) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public User(String email, String firstName, String lastName, String password) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}
}
