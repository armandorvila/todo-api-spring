package com.armandorv.poc.tasks.resource.errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;
	
	private Integer status;

	private String message;

	@JsonInclude(Include.NON_EMPTY)
	private List<ValidationError> validationErrors = new ArrayList<>();
	
	@JsonInclude(Include.NON_NULL)
	private String debugMessage;

	public ErrorDTO(Integer status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
	
	public ErrorDTO(Integer status, String message, String debugMessage) {
		this.status = status;
		this.message = message;
		this.debugMessage = debugMessage;
		this.timestamp = LocalDateTime.now();
	}

	public void addValidationError(String path, String code, String message) {
		this.validationErrors.add(new ValidationError(path, code, message));
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	private static class ValidationError {
		private String path;
		private String code;
		private String message;
	}

}
