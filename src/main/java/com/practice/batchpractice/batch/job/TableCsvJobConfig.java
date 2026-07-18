package com.practice.batchpractice.batch.job;
import com.practice.batchpractice.batch.tasklet.TableCsvTasklet;
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
public class TableCsvJobConfig {

    // Spring Batch 실행 정보를 관리하는 객체
    // Job과 Step을 생성할 때 필요하다.
    private final JobRepository jobRepository;

    // Step 안에서 트랜잭션을 관리하는 객체
    private final PlatformTransactionManager transactionManager;

    // TABLE_A를 조회하고 CSV 파일을 만드는 실제 작업 클래스
    private final TableCsvTasklet tableCsvTasklet;

    /**
     * CSV 생성 배치 Job
     *
     * Job은 배치 작업 전체 단위이다.
     * 이 Job이 실행되면 tableCsvStep이 시작된다.
     */
    @Bean
    public Job tableCsvJob() {

        return new JobBuilder(
                "tableCsvJob",       // Spring Batch에 등록되는 Job 이름
                jobRepository
        )
                // Job이 시작되면 tableCsvStep을 실행한다.
                .start(tableCsvStep())
                .build();
    }

    /**
     * CSV 생성 Step
     *
     * Step은 Job 내부의 실제 작업 단계이다.
     * 이 Step에서는 TableCsvTasklet을 실행한다.
     */
    @Bean
    public Step tableCsvStep() {

        return new StepBuilder(
                "tableCsvStep",      // Spring Batch에 등록되는 Step 이름
                jobRepository
        )
                // 이 Step에서 실행할 Tasklet과 트랜잭션 관리자를 설정한다.
                .tasklet(
                        tableCsvTasklet,
                        transactionManager
                )
                .build();
    }
}