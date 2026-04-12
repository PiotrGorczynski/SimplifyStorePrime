package com.example.simplifyStorePrime.exception;

import com.example.simplifyStorePrime.commons.AppConstants;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(AppConstants.RESPONSE_TIMESTAMP, LocalDateTime.now());
        errorResponse.put(AppConstants.RESPONSE_STATUS, HttpStatus.NOT_FOUND.value());
        errorResponse.put(AppConstants.RESPONSE_ERROR, AppConstants.ERROR_NOT_FOUND);
        errorResponse.put(AppConstants.RESPONSE_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(AppConstants.RESPONSE_TIMESTAMP, LocalDateTime.now());
        errorResponse.put(AppConstants.RESPONSE_STATUS, HttpStatus.CONFLICT.value());
        errorResponse.put(AppConstants.RESPONSE_ERROR, AppConstants.ERROR_CONFLICT);
        errorResponse.put(AppConstants.RESPONSE_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(AppConstants.RESPONSE_TIMESTAMP, LocalDateTime.now());
        errorResponse.put(AppConstants.RESPONSE_STATUS, HttpStatus.BAD_REQUEST.value());
        errorResponse.put(AppConstants.RESPONSE_ERROR, AppConstants.ERROR_VALIDATION);

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        errorResponse.put(AppConstants.RESPONSE_MESSAGES, errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(AppConstants.RESPONSE_TIMESTAMP, LocalDateTime.now());
        errorResponse.put(AppConstants.RESPONSE_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put(AppConstants.RESPONSE_ERROR, AppConstants.ERROR_INTERNAL);
        errorResponse.put(AppConstants.RESPONSE_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
