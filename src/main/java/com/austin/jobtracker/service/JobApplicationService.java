package com.austin.jobtracker.service;

import org.springframework.stereotype.Service;

import com.austin.jobtracker.exception.JobApplicationNotFoundException;
import com.austin.jobtracker.model.JobApplication;
import com.austin.jobtracker.repository.JobApplicationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JobApplicationService {
    
    private static final Logger logger = 
            LoggerFactory.getLogger(JobApplicationService.class);

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
        
        logger.info(
            "Fetching applications with company={}, status={}, page={}, size={}",
            company,
            status,
            page,
            size
        );

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
        JobApplication created = repository.save(application);

        logger.info(
            "Created job application with id={}, company={}, position{}",
            created.getId(),
            created.getCompany(),
            created.getPosition()
        );

        return created;
    }

    public JobApplication getApplicationById(Integer id) {
        return repository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Job application not found with id={}", id);    
                        return new JobApplicationNotFoundException(id);
                    });
    }

    public JobApplication updateApplication(
        Integer id,
        JobApplication updatedApplication) {
            
            JobApplication existingApplication = getApplicationById(id);

            existingApplication.setCompany(updatedApplication.getCompany());
            existingApplication.setPosition(updatedApplication.getPosition());
            existingApplication.setStatus(updatedApplication.getStatus());

            JobApplication updated = repository.save(existingApplication);

            logger.info(
                "Updated job application with id={}",
                updated.getId()
            );
                
            return updated;
        }

    public void deleteApplication(Integer id) {
        getApplicationById(id);
        repository.deleteById(id);

        logger.info(
            "Deleted job application with id={}",
            id
        );
    }
}
