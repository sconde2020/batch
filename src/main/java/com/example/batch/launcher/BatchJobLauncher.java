package com.example.batch.launcher;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BatchJobLauncher implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BatchJobLauncher.class);

    private final JobOperator jobOperator;
    private final List<Job> jobs;

    public BatchJobLauncher(JobOperator jobOperator, List<Job> jobs) {
        this.jobOperator = jobOperator;
        this.jobs = jobs;
    }

    @Override
    public void run(String @NonNull ... args) {
        log.info("Application initialized. Launching {} job(s)...", jobs.size());

        for (Job job : jobs) {
            launchJob(job);
        }

        log.info("All jobs launched.");
    }

    private void launchJob(Job job) {
        try {
            String jobName = job.getName();

            JobParameters params = new JobParametersBuilder()
                    .addString("triggeredAt", LocalDateTime.now().toString())
                    .toJobParameters();

            log.info("Launching job '{}' with parameters '{}'", jobName, params);

            JobExecution jobExecution = jobOperator.start(job, params);

            log.info("Job '{}' executed with jobExecution={}", jobName, jobExecution);

        } catch (Exception ex) {
            log.error("Failed to launch job '{}': {}", job.getName(), ex.getMessage(), ex);
        }
    }
}
