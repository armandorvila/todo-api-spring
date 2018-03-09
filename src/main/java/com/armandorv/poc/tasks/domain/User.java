package com.armandorv.poc.tasks.domain;

import java.time.Instant;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	
	@NotNull
	@Email
	@Indexed(unique = true)
	private String email;

	@NotNull
	private String firstName;

	@NotNull
	private String lastName;
	
	private String password;

	@CreatedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant createdAt;

	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
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
