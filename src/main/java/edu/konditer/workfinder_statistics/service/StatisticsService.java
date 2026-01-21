package edu.konditer.workfinder_statistics.service;

import edu.konditer.workfinder_statistics.entity.JobStatistics;
import edu.konditer.workfinder_statistics.repository.JobStatisticsRepository;
import edu.konditer.workfinder_statistics.repository.VacancyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private static final Logger log = LoggerFactory.getLogger(StatisticsService.class);
    
    private final VacancyRepository vacancyRepository;
    private final JobStatisticsRepository jobStatisticsRepository;

    public StatisticsService(VacancyRepository vacancyRepository, JobStatisticsRepository jobStatisticsRepository) {
        this.vacancyRepository = vacancyRepository;
        this.jobStatisticsRepository = jobStatisticsRepository;
    }

    @Transactional
    public void updateStatistics() {
        try {
            log.info("Updating job statistics...");
            
            // Получаем количество вакансий по каждой профессии
            List<Object[]> results = vacancyRepository.countByJobName();
            
            // Преобразуем результаты в Map
            Map<String, Long> countsByJobName = results.stream()
                    .collect(Collectors.toMap(
                            row -> (String) row[0],
                            row -> {
                                Object count = row[1];
                                if (count instanceof Long) {
                                    return (Long) count;
                                } else if (count instanceof Number) {
                                    return ((Number) count).longValue();
                                } else {
                                    return Long.parseLong(count.toString());
                                }
                            }
                    ));
            
            // Обновляем или создаем записи статистики
            for (Map.Entry<String, Long> entry : countsByJobName.entrySet()) {
                String jobName = entry.getKey();
                Long count = entry.getValue();
                
                JobStatistics statistics = jobStatisticsRepository.findByJobName(jobName)
                        .orElse(new JobStatistics(jobName, 0L));
                
                statistics.setCount(count);
                jobStatisticsRepository.save(statistics);
                
                log.debug("Updated statistics for job '{}': {} vacancies", jobName, count);
            }
            
            // Удаляем статистику для профессий, которых больше нет
            List<String> existingJobNames = countsByJobName.keySet().stream().toList();
            List<JobStatistics> allStatistics = jobStatisticsRepository.findAll();
            for (JobStatistics stat : allStatistics) {
                if (!existingJobNames.contains(stat.getJobName())) {
                    jobStatisticsRepository.delete(stat);
                    log.debug("Removed statistics for job '{}' (no vacancies)", stat.getJobName());
                }
            }
            
            log.info("Job statistics updated successfully. Total jobs: {}", countsByJobName.size());
        } catch (Exception e) {
            log.error("Error updating job statistics", e);
            throw e;
        }
    }
}

