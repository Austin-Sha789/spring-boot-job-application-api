package com.austin.jobtracker.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.austin.jobtracker.exception.JobApplicationNotFoundException;
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
        return repository.findById(id)
                    .orElseThrow(() -> new JobApplicationNotFoundException(id));
    }

    public JobApplication updateApplication(
        Integer id,
        JobApplication updatedApplication) {
            
            JobApplication existingApplication = getApplicationById(id);

            existingApplication.setCompany(updatedApplication.getCompany());
            existingApplication.setPosition(updatedApplication.getPosition());
            existingApplication.setStatus(updatedApplication.getStatus());

            return repository.save(existingApplication);
        }

    public void deleteApplication(Integer id) {
        getApplicationById(id);
        repository.deleteById(id);
    }
}
