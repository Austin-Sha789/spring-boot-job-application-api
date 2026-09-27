package com.austin.jobtracker.exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JobApplicationNotFoundException.class)
    public ResponseEntity<String> handleJobApplicationNotFound(JobApplicationNotFoundException ex){
        return ResponseEntity.status(404)
                             .body(ex.getMessage());
        }
}
