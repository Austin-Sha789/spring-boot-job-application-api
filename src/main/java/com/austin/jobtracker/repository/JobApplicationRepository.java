package com.austin.jobtracker.repository;

import com.austin.jobtracker.model.JobApplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository
    extends JpaRepository<JobApplication, Integer>{
        List<JobApplication> findByCompanyIgnoreCase(String company);

        List<JobApplication> findByStatusIgnoreCase(String status);

        List<JobApplication> findByCompanyIgnoreCaseAndStatusIgnoreCase(String company, String status);
}