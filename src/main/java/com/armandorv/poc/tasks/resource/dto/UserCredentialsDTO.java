package com.armandorv.poc.tasks.resource.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {

	@Email
	@NotEmpty
	private String email;
	
	@NotEmpty
	private String password;
}
