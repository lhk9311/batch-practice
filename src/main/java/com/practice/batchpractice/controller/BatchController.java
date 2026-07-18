package com.practice.batchpractice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BatchController {

    // Spring Batch의 Job을 실제로 실행하는 객체
    private final JobLauncher jobLauncher;

    // TABLE_A 건수를 조회하는 Job
    @Qualifier("tableCountJob")
    private final Job tableCountJob;

    // TABLE_A 데이터를 CSV 파일로 저장하는 Job
    @Qualifier("tableCsvJob")
    private final Job tableCsvJob;

    // CSV 파일을 읽어서 TABLE_C에 적재하는 Job
    @Qualifier("csvToTableCJob")
    private final Job csvToTableCJob;

    /**
     * TABLE_A 건수 조회 배치 실행
     *
     * 호출 주소:
     * GET /batch/count
     */
    @GetMapping("/batch/count")
    public String runCountBatch() {

        try {

            // 같은 Job을 반복 실행할 수 있도록
            // 현재 시간을 JobParameter에 넣는다.
            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters();

            // tableCountJob 실행
            jobLauncher.run(tableCountJob, jobParameters);

            return "Count Batch Success";

        } catch (Exception e) {

            e.printStackTrace();

            return "Count Batch Fail : " + e.getMessage();
        }
    }

    /**
     * TABLE_A 데이터를 CSV 파일로 저장하는 배치 실행
     *
     * 호출 주소:
     * GET /batch/csv
     */
    @GetMapping("/batch/csv")
    public String runCsvBatch() {

        try {

            // 같은 Job을 반복 실행하기 위한 JobParameter 생성
            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters();

            // tableCsvJob 실행
            jobLauncher.run(tableCsvJob, jobParameters);

            return "CSV Batch Success";

        } catch (Exception e) {

            e.printStackTrace();

            return "CSV Batch Fail : " + e.getMessage();
        }
    }

    /**
     * CSV 파일을 읽어서 TABLE_C에 적재하는 배치 실행
     *
     * 호출 주소:
     * GET /batch/csv-to-table-c
     */
    @GetMapping("/batch/csv-to-table-c")
    public String runCsvToTableCBatch() {

        try {

            // 같은 Job을 반복 실행할 수 있도록
            // 현재 시간을 JobParameter에 넣는다.
            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters();

            // CsvToTableCJobConfig에 등록된 csvToTableCJob 실행
            jobLauncher.run(csvToTableCJob, jobParameters);

            return "CSV To TABLE_C Batch Success";

        } catch (Exception e) {

            e.printStackTrace();

            return "CSV To TABLE_C Batch Fail : " + e.getMessage();
        }
    }
}