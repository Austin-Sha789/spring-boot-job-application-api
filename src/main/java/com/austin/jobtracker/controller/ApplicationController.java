package com.austin.jobtracker.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.austin.jobtracker.model.JobApplication;
import com.austin.jobtracker.service.JobApplicationService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ApplicationController {

    private final JobApplicationService service;
    
    public ApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping("/applications")
    public List<JobApplication> getApplications() {
        return service.getApplications();
    }

    @PostMapping("/applications")
    public JobApplication createApplication(@RequestBody JobApplication application) {
        return service.createApplication(application);
    }
}
