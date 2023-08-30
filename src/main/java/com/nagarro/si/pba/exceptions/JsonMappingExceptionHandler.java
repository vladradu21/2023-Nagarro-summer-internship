package com.nagarro.si.pba.exceptions;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Order(1)
public class JsonMappingExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiExceptionFormat> handleJsonMappingException(InvalidInputException ex) {
        ApiExceptionFormat apiException = new ApiExceptionFormat();
        apiException.setStatus(HttpStatus.BAD_REQUEST.value());
        apiException.setMessage("Invalid JSON format: " + ex.getMessage());
        apiException.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(apiException);
    }
}