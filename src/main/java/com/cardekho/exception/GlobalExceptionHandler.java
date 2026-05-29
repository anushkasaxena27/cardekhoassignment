package com.cardekho.exception;

import com.cardekho.dto.common.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(GlobalExceptionHandler::formatFieldError)
				.collect(Collectors.toList());
		return ResponseEntity.badRequest()
				.body(new ApiError("Validation failed", HttpStatus.BAD_REQUEST.value(), Instant.now(), details));
	}

	private static String formatFieldError(FieldError fe) {
		return fe.getField() + ": " + fe.getDefaultMessage();
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value(), Instant.now(), List.of()));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
		return ResponseEntity.badRequest()
				.body(new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), Instant.now(), List.of()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCreds(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ApiError("Invalid credentials", HttpStatus.UNAUTHORIZED.value(), Instant.now(), List.of()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiError("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now(),
						List.of(ex.getMessage() == null ? "Unknown" : ex.getMessage())));
	}
}
