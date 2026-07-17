package com.practice.batchpractice.batch.job;

import com.practice.batchpractice.batch.tasklet.TableCountTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TableCountJobConfig {

    private final TableCountTasklet tableCountTasklet;

    @Bean
    public Step tableCountStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("tableCountStep", jobRepository)
                .tasklet(tableCountTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job tableCountJob(
            JobRepository jobRepository,
            Step tableCountStep
    ) {
        return new JobBuilder("tableCountJob", jobRepository)
                .start(tableCountStep)
                .build();
    }
}