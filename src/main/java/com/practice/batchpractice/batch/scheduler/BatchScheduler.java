package com.practice.batchpractice.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job csvToTableCJob;

    @Scheduled(cron = "0 0 15 * * *")
    public void runBatch() {

        try {

            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters();

            jobLauncher.run(csvToTableCJob, jobParameters);

            log.info("===== Scheduler Batch Start =====");

        } catch (Exception e) {

            log.error("Batch Scheduler Error", e);

        }
    }

}