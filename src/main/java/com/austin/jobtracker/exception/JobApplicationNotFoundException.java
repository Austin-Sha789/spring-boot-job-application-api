package com.austin.jobtracker.exception;

public class JobApplicationNotFoundException extends RuntimeException{
    public JobApplicationNotFoundException(Integer id){
        super("Job application not found with id: " + id);
    }


}
