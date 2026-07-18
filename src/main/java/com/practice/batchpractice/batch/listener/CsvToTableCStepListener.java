package com.practice.batchpractice.batch.listener;

import com.practice.batchpractice.mapper.TableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvToTableCStepListener extends StepExecutionListenerSupport {

    // 선행 Job 실행 이력을 조회하기 위한 객체
    private final JobExplorer jobExplorer;

    // TABLE_C TRUNCATE 실행용 Mapper
    private final TableMapper tableMapper;

    /**
     * csvToTableCStep이 실행되기 전에 호출된다.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        log.info("CSV → TABLE_C Step 실행 전 처리 시작");

        /*
         * 선행 Job인 tableCsvJob의 최근 실행 이력을 조회한다.
         *
         * getJobInstances("Job 이름", 시작 위치, 조회 개수)
         */
        List<org.springframework.batch.core.JobInstance> jobInstances =
                jobExplorer.getJobInstances(
                        "tableCsvJob",
                        0,
                        1
                );

        /*
         * 선행 Job 실행 이력이 한 번도 없으면 현재 Step을 실패시킨다.
         */
        if (jobInstances.isEmpty()) {
            throw new IllegalStateException(
                    "선행 배치 tableCsvJob 실행 이력이 없습니다."
            );
        }

        /*
         * 가장 최근 JobInstance의 실행 내역을 조회한다.
         */
        List<JobExecution> jobExecutions =
                jobExplorer.getJobExecutions(jobInstances.get(0));

        if (jobExecutions.isEmpty()) {
            throw new IllegalStateException(
                    "선행 배치 tableCsvJob 실행 결과를 찾을 수 없습니다."
            );
        }

        /*
         * 가장 최근 실행 결과를 가져온다.
         */
        JobExecution latestExecution =
                jobExecutions.stream()
                        .max((execution1, execution2) ->
                                execution1.getCreateTime()
                                        .compareTo(execution2.getCreateTime()))
                        .orElseThrow();

        /*
         * 선행 Job 상태가 COMPLETED인지 확인한다.
         */
        if (latestExecution.getStatus() != BatchStatus.COMPLETED) {
            throw new IllegalStateException(
                    "선행 배치 tableCsvJob이 정상 완료되지 않았습니다. 상태: "
                            + latestExecution.getStatus()
            );
        }

        log.info(
                "선행 배치 완료 확인: jobName={}, executionId={}, status={}",
                latestExecution.getJobInstance().getJobName(),
                latestExecution.getId(),
                latestExecution.getStatus()
        );

        /*
         * 요구사항:
         * 현재 Job 실행 전에 beforeStep에서 TABLE_C를 TRUNCATE한다.
         */
        log.info("TABLE_C TRUNCATE 시작");

        tableMapper.truncateTableC();

        log.info("TABLE_C TRUNCATE 완료");
    }
}