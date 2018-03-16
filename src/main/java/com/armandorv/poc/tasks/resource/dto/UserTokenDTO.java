package com.armandorv.poc.tasks.resource.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenDTO {
	
	@NotEmpty
	private String message;
	
	@NotEmpty
	private String token;
}
