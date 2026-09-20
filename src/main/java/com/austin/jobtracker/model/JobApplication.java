package com.austin.jobtracker.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;

@Entity
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String company;

    @NotBlank 
    private String position;

    @NotBlank
    private String status;

    public JobApplication() {
    }

    public JobApplication(
        Integer id,
        String company,
        String position,
        String status
    ) {
        this.id = id;
        this.company = company;
        this.position = position;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getPosition() {
        return position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
