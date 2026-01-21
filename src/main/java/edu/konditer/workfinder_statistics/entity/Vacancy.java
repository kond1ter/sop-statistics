package edu.konditer.workfinder_statistics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vacancies")
public class Vacancy {
    @Id
    private Long id;

    @Column(nullable = false)
    private String jobName;

    public Vacancy() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}

