package com.armandorv.poc.tasks.resource.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
	
	private HttpStatus status;

	private String message;
	
	private String developerMessage;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;
	
	public ErrorDTO(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
	
	public ErrorDTO(HttpStatus status, String message, String developerMessage) {
		this.status = status;
		this.message = message;
		this.developerMessage = developerMessage;
		this.timestamp = LocalDateTime.now();
	}
}
