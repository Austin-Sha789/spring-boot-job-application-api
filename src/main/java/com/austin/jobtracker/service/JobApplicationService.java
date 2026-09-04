package com.austin.jobtracker.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.austin.jobtracker.model.JobApplication;
import com.austin.jobtracker.repository.JobApplicationRepository;

@Service
public class JobApplicationService {
    
    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository){
        this.repository = repository;
    }

    public List<JobApplication> getApplications() {
        return repository.findAll();
    }

    public JobApplication createApplication(JobApplication application) {
        return repository.save(application);
    }
}
