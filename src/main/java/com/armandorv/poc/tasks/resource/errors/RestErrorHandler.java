package com.armandorv.poc.tasks.resource.errors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import com.armandorv.poc.tasks.config.ApplicationProperties;
import org.springframework.dao.DuplicateKeyException;

@ControllerAdvice
public class RestErrorHandler {

	private Boolean isDebugEnabeld;
	
	public RestErrorHandler(ApplicationProperties properties) {
		this.isDebugEnabeld = properties.getErrorHandler().getDebug();
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorDTO> handleBadRequestAlertException(BadCredentialsException ex) {
		final ErrorDTO error = new ErrorDTO(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase());
		
		if(isDebugEnabeld) {
			error.setDebugMessage(ex.getLocalizedMessage());
		}
		return new ResponseEntity<>(error, UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
		final ErrorDTO error = new ErrorDTO(INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR.getReasonPhrase());
		
		if(isDebugEnabeld) {
			error.setDebugMessage(ex.getLocalizedMessage());
		}
		return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(DuplicateKeyException.class)
	public ResponseEntity<ErrorDTO> handleDuplicateKeyException(DuplicateKeyException ex) {
		final ErrorDTO error = new ErrorDTO(BAD_REQUEST.value(), "The resource that you are trying to create already exists.");
		
		if(isDebugEnabeld) {
			error.setDebugMessage(ex.getLocalizedMessage());
		}
		return new ResponseEntity<>(error, BAD_REQUEST);
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<ErrorDTO> handleWebExchangeBindException(WebExchangeBindException ex) {
		final HttpStatus status = ex.getStatus();
		final ErrorDTO error = new ErrorDTO(status.value(), status.getReasonPhrase());
		
		ex.getFieldErrors().forEach(e -> error.addValidationError(e.getField(), e.getCode(), e.getDefaultMessage()));
		
		if(isDebugEnabeld) {
			error.setDebugMessage(ex.getLocalizedMessage());
		}
		return new ResponseEntity<>(error, status);
	}
	
	@ExceptionHandler(ServerWebInputException.class)
	public ResponseEntity<ErrorDTO> handleServerWebInputException(ServerWebInputException ex) {
		final HttpStatus status = ex.getStatus();
		final ErrorDTO error = new ErrorDTO(status.value(), status.getReasonPhrase());
		
		if(isDebugEnabeld) {
			error.setDebugMessage(ex.getLocalizedMessage());
		}
		return new ResponseEntity<>(error, status);
	} 
}
