package com.austin.jobtracker.exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JobApplicationNotFoundException.class)
    public ResponseEntity<String> handleJobApplicationNotFound(JobApplicationNotFoundException ex){

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ex.getMessage());
        }
}
