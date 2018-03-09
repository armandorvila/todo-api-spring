package com.armandorv.poc.tasks.resource.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {

	@Email
	@NotNull
	private String email;
	
	@NotNull
	private String password;
}
