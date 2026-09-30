package com.austin.jobtracker.repository;

import com.austin.jobtracker.model.JobApplication;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobApplicationRepository
    extends JpaRepository<JobApplication, Integer>{
        Page<JobApplication> findByCompanyIgnoreCase(String company, Pageable pageable);

        Page<JobApplication> findByStatusIgnoreCase(String status, Pageable pageable);

        Page<JobApplication> findByCompanyIgnoreCaseAndStatusIgnoreCase(String company, String status, Pageable pageable);
}