package com.armandorv.poc.tasks.resource.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.armandorv.poc.tasks.resource.dto.ErrorDTO;

@ControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorDTO> handleBadRequestAlertException(BadCredentialsException ex) {
		ErrorDTO errorDTO = new ErrorDTO(UNAUTHORIZED, "Invalid user credentials.", ex.getMessage());
		return new ResponseEntity<>(errorDTO, UNAUTHORIZED);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDTO> handleBadRequestAlertException(Exception ex) {
		ErrorDTO errorDTO = new ErrorDTO(INTERNAL_SERVER_ERROR, "Internal Server Error.", ex.getMessage());
		return new ResponseEntity<>(errorDTO, INTERNAL_SERVER_ERROR);
	}
}
