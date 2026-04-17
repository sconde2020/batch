package com.example.batch.launcher;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        RestartRequest restartRequest = parseRestartRequest(args);
        if (restartRequest.requested()) {
            if (restartRequest.executionId() == null) {
                log.warn("Restart requested but no executionId provided. Use --restart=<executionId>.");
            } else {
                restartByExecutionId(restartRequest.executionId());
            }
            return;
        }

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

            log.info("Job '{}' started with executionId={}", jobName, jobExecution.getId());
            log.info("Job '{}' executed with jobExecution={}", jobName, jobExecution);

        } catch (Exception ex) {
            log.error("Failed to launch job '{}': {}", job.getName(), ex.getMessage(), ex);
        }
    }

    private void restartByExecutionId(long executionId) {
        JobExecution execution = new JobExecution(executionId, null, new JobParameters());
        try {
            JobExecution restarted = restart(execution);
            log.info("Restart requested from executionId={}, restartResultExecutionId={}",
                    executionId, restarted.getId());
        } catch (JobRestartException ex) {
            log.error("Failed to restart executionId={}: {}", executionId, ex.getMessage(), ex);
        }
    }

    private JobExecution restart(JobExecution jobExecution) throws JobRestartException {
        try {
            if (jobExecution == null) {
                throw new JobRestartException("JobExecution id is required to restart");
            }
            jobOperator.restart(jobExecution);
            return jobExecution;
        } catch (JobRestartException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JobRestartException("Failed to restart executionId="
                    + Objects.requireNonNull(jobExecution).getId(), ex);
        }
    }

    private RestartRequest parseRestartRequest(String[] args) {
        boolean requested = Arrays.asList(args).contains("--restart");
        Long executionId = null;

        for (String arg : args) {
            if (arg.startsWith("--restart=")) {
                requested = true;
                String value = arg.substring("--restart=".length());
                try {
                    executionId = Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    log.warn("Invalid executionId '{}' for --restart", value);
                }
            }
        }

        return new RestartRequest(requested, executionId);
    }

    private record RestartRequest(boolean requested, Long executionId) {
    }
}
