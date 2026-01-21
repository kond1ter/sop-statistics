package edu.konditer.workfinder_statistics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_statistics")
public class JobStatistics {
    @Id
    @Column(nullable = false, unique = true)
    private String jobName;

    @Column(nullable = false)
    private Long count;

    public JobStatistics() {
    }

    public JobStatistics(String jobName, Long count) {
        this.jobName = jobName;
        this.count = count;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

