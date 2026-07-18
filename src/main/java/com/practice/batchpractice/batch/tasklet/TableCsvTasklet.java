package com.practice.batchpractice.batch.tasklet;

import com.practice.batchpractice.domain.TableData;
import com.practice.batchpractice.mapper.TableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableCsvTasklet implements Tasklet {

    private final TableMapper tableMapper;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) throws Exception {

        // ============================================================
        // 1. TABLE_A의 모든 데이터를 조회한다.
        // Mapper → XML → SELECT * FROM TABLE_A
        // ============================================================
        List<TableData> tableDataList =
                tableMapper.selectAllTableA();

        // ============================================================
        // 2. CSV 파일 생성
        // 프로젝트 실행 경로에 table_a.csv 생성
        // ============================================================
        BufferedWriter writer =
                new BufferedWriter(
                        new FileWriter("table_a.csv")
                );

        // ============================================================
        // 3. CSV 첫 줄(Header) 작성
        // ============================================================
        writer.write("ID,NAME,EMAIL,CREATED_AT");
        writer.newLine();

        // ============================================================
        // 4. 조회한 데이터를 한 건씩 CSV에 작성
        // ============================================================
        for (TableData data : tableDataList) {

            writer.write(
                    data.getId() + "," +
                            data.getName() + "," +
                            data.getEmail() + "," +
                            data.getCreatedAt()
            );

            // 줄바꿈
            writer.newLine();
        }

        // ============================================================
        // 5. 파일 저장 후 닫기
        // ============================================================
        writer.close();

        // 로그 출력
        log.info("CSV 파일 생성 완료");
        log.info("총 {}건 저장", tableDataList.size());

        // Step 종료
        return RepeatStatus.FINISHED;
    }
}