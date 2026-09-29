package com.austin.jobtracker.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.austin.jobtracker.model.JobApplication;
import com.austin.jobtracker.service.JobApplicationService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;

@RestController
public class ApplicationController {

    private final JobApplicationService service;
    
    public ApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping("/applications")
    public List<JobApplication> getApplications(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String status) {
        return service.getApplications(company, status);
    }

    @PostMapping("/applications")
    public ResponseEntity<JobApplication> createApplication(@Valid @RequestBody JobApplication application) {
        
        JobApplication created = service.createApplication(application);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping ("/applications/{id}")
    public ResponseEntity<JobApplication> getApplicationById(
        @PathVariable Integer id) {

        JobApplication application = service.getApplicationById(id);

        return ResponseEntity.ok(application);
    }

    @PutMapping("/applications/{id}")
        public ResponseEntity<JobApplication> updateApplication(
            @PathVariable Integer id,
            @Valid @RequestBody JobApplication application) {

        JobApplication updated = service.updateApplication(id, application);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Integer id) {
        service.deleteApplication(id);

        return ResponseEntity.noContent().build();
    }
}
