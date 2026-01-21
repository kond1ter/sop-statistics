package edu.konditer.workfinder_statistics.repository;

import edu.konditer.workfinder_statistics.entity.JobStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobStatisticsRepository extends JpaRepository<JobStatistics, String> {
    Optional<JobStatistics> findByJobName(String jobName);
}

