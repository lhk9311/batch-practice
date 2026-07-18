package com.practice.batchpractice.batch.job;

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

    // Spring Batch의 Job과 Step 실행 정보를 관리하는 객체
    private final JobRepository jobRepository;

    // Step 실행 시 트랜잭션을 관리하는 객체
    private final PlatformTransactionManager transactionManager;

    // CSV 파일을 읽고 TABLE_C에 적재하는 실제 작업 클래스
    private final CsvToTableCTasklet csvToTableCTasklet;

    /**
     * CSV 파일을 TABLE_C에 적재하는 전체 배치 Job
     *
     * Bean 이름은 메서드명과 같은 csvToTableCJob으로 등록된다.
     * Controller의 @Qualifier("csvToTableCJob")가 이 Bean을 찾는다.
     */
    @Bean
    public Job csvToTableCJob() {

        return new JobBuilder(
                "csvToTableCJob",   // Spring Batch 내부에서 관리할 Job 이름
                jobRepository
        )
                // Job 실행 시 csvToTableCStep을 먼저 실행한다.
                .start(csvToTableCStep())
                .build();
    }

    /**
     * CSV → TABLE_C 적재 작업을 수행하는 Step
     *
     * 이 Step이 실행되면 CsvToTableCTasklet의 execute()가 호출된다.
     */
    @Bean
    public Step csvToTableCStep() {

        return new StepBuilder(
                "csvToTableCStep",  // Spring Batch 내부에서 관리할 Step 이름
                jobRepository
        )
                // 실제 작업 Tasklet과 트랜잭션 관리자를 연결한다.
                .tasklet(
                        csvToTableCTasklet,
                        transactionManager
                )
                .build();
    }
}