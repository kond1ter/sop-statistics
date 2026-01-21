package edu.konditer.workfinder_statistics.repository;

import edu.konditer.workfinder_statistics.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    @Query("SELECT v.jobName, COUNT(v) FROM Vacancy v GROUP BY v.jobName")
    List<Object[]> countByJobName();
}

