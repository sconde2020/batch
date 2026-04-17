package com.example.batch.listener;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class StepListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(StepListener.class);

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        log.info("Step '{}' started at {}",
                stepExecution.getStepName(),
                stepExecution.getStartTime());
    }

    @Override
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        LocalDateTime start = stepExecution.getStartTime();
        LocalDateTime end = stepExecution.getEndTime();
        Long durationMs = (start != null && end != null) ? Duration.between(start, end).toMillis() : null;

        log.info("Step '{}' finished with status={} exitCode={} readCount={} writeCount={} skipCount={} durationMs={}",
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                stepExecution.getExitStatus().getExitCode(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                durationMs);

        return stepExecution.getExitStatus();
    }
}
