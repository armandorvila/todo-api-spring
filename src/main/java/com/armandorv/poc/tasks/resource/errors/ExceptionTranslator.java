package com.armandorv.poc.tasks.resource.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.armandorv.poc.tasks.resource.dto.ErrorDTO;

@ControllerAdvice
public class ExceptionTranslator {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorDTO> handleBadRequestAlertException(BadCredentialsException e) {
		ErrorDTO errorDTO = new ErrorDTO("Invalid credentials", e.getMessage());
		return new ResponseEntity<>(errorDTO, HttpStatus.UNAUTHORIZED);
	}
}
