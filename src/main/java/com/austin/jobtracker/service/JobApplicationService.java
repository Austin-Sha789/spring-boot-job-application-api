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

    public JobApplication getApplicationById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public JobApplication updateApplication(
        Integer id,
        JobApplication updatedApplication) {
            
            JobApplication existingApplication =
                repository.findById(id).orElse(null);

            if (existingApplication == null) {
                return null;
            }

            existingApplication.setCompany(updatedApplication.getCompany());
            existingApplication.setPosition(updatedApplication.getPosition());
            existingApplication.setStatus(updatedApplication.getStatus());

            return repository.save(existingApplication);
        }

    public boolean deleteApplication(Integer id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}
