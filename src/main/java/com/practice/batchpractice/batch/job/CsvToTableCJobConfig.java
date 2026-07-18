package com.practice.batchpractice.batch.job;

import com.practice.batchpractice.batch.listener.CsvToTableCStepListener;
import com.practice.batchpractice.batch.tasklet.CsvToTableCTasklet;
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
public class CsvToTableCJobConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final CsvToTableCTasklet csvToTableCTasklet;

    // Step 실행 전 선행 Job 확인 및 TABLE_C TRUNCATE 처리
    private final CsvToTableCStepListener csvToTableCStepListener;

    @Bean
    public Job csvToTableCJob() {

        return new JobBuilder(
                "csvToTableCJob",
                jobRepository
        )
                .start(csvToTableCStep())
                .build();
    }

    @Bean
    public Step csvToTableCStep() {

        return new StepBuilder(
                "csvToTableCStep",
                jobRepository
        )
                /*
                 * Step 실행 전:
                 * 1. tableCsvJob 완료 여부 확인
                 * 2. TABLE_C TRUNCATE
                 */
                .listener(csvToTableCStepListener)

                /*
                 * 그 후 CSV 파일을 읽어서
                 * TABLE_C에 INSERT한다.
                 */
                .tasklet(
                        csvToTableCTasklet,
                        transactionManager
                )
                .build();
    }
}