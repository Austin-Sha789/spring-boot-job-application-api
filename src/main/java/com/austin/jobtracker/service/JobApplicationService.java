package com.austin.jobtracker.service;

import org.springframework.stereotype.Service;

import com.austin.jobtracker.exception.JobApplicationNotFoundException;
import com.austin.jobtracker.model.JobApplication;
import com.austin.jobtracker.repository.JobApplicationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
public class JobApplicationService {
    
    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository){
        this.repository = repository;
    }

    public Page<JobApplication> getApplications(
            String company, 
            String status, 
            int page, 
            int size
        ) {
        
        Pageable pageable = PageRequest.of(page, size);

        boolean hasCompany = company != null && !company.isBlank();
        boolean hasStatus = status != null && !status.isBlank();

        if (hasCompany && hasStatus){
            return repository.findByCompanyIgnoreCaseAndStatusIgnoreCase(company, status, pageable);
        } else if (hasStatus) {
            return repository.findByStatusIgnoreCase(status, pageable);
        } else if (hasCompany) {
            return repository.findByCompanyIgnoreCase(company, pageable);
        }

        return repository.findAll(pageable);
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
