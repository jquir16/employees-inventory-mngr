package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.dto.exception.ApiError;
import com.katabdb.employee.onboarding.mngr.exception.InvalidCredentialsException;
import com.katabdb.employee.onboarding.mngr.exception.UserAlreadyExistsException;
import org.hibernate.PropertyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ApiError> handleUnauthorizedExceptions(RuntimeException ex) {
        logAndGetMessage(HttpStatus.UNAUTHORIZED, ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserExists(UserAlreadyExistsException ex) {
        logAndGetMessage(HttpStatus.CONFLICT, ex);
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .orElse("Validation error");

        logAndGetMessage(HttpStatus.BAD_REQUEST, errorMessage);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable() {
        String message = "Bad formatted body or request";
        logAndGetMessage(HttpStatus.BAD_REQUEST, message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("The method %s is not implemented on this endpoint", ex.getMethod());
        logAndGetMessage(HttpStatus.METHOD_NOT_ALLOWED, message);
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = extractDataIntegrityErrorMessage(ex);
        logAndGetMessage(HttpStatus.BAD_REQUEST, message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralError(Exception ex) {
        logger.error("500 Internal Server Error: ", ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error"
        );
    }

    private String extractDataIntegrityErrorMessage(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof PropertyValueException) {
            PropertyValueException pve = (PropertyValueException) ex.getCause();
            return String.format("Campo requerido no proporcionado: %s", pve.getPropertyName());
        }
        return "Error de integridad de datos";
    }

    private void logAndGetMessage(HttpStatus status, Exception ex) {
        logger.warn("{} {}: {}", status.value(), status.getReasonPhrase(), ex.getMessage());
    }

    private void logAndGetMessage(HttpStatus status, String message) {
        logger.warn("{} {}: {}", status.value(), status.getReasonPhrase(), message);
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), message));
    }
}