package com.austin.jobtracker.repository;

import com.austin.jobtracker.model.JobApplication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository
    extends JpaRepository<JobApplication, Integer>{
}