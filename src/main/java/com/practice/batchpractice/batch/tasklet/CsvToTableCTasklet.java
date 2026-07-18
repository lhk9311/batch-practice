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

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvToTableCTasklet implements Tasklet {

    // TABLE_C 초기화 및 INSERT에 사용할 MyBatis Mapper
    private final TableMapper tableMapper;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) throws Exception {

        /*
         * 읽어올 CSV 파일 경로
         *
         * 이전 CSV 생성 배치에서
         * new FileWriter("table_a.csv")
         * 로 파일을 만들었으므로 프로젝트 실행 경로에 존재한다.
         */
        String csvFilePath = "table_a.csv";

        /*
         * 적재된 데이터 건수를 저장할 변수
         */
        int insertCount = 0;

        /*
         * 기존 TABLE_C 데이터를 먼저 비운다.
         *
         * 이렇게 하면 배치를 여러 번 실행해도
         * 이전 데이터가 중복으로 남지 않는다.
         */
        tableMapper.truncateTableC();

        /*
         * try-with-resources
         *
         * try 문이 끝나면 BufferedReader가 자동으로 닫힌다.
         * reader.close()를 직접 호출하지 않아도 된다.
         */
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(csvFilePath))) {

            /*
             * CSV 파일의 첫 번째 줄을 읽는다.
             *
             * 첫 번째 줄은 데이터가 아니라 다음과 같은 헤더이므로
             * 읽기만 하고 INSERT하지 않는다.
             *
             * ID,NAME,EMAIL,CREATED_AT
             */
            String header = reader.readLine();

            log.info("CSV 헤더 = {}", header);

            /*
             * CSV의 실제 데이터 한 줄을 저장할 변수
             */
            String line;

            /*
             * CSV 파일을 한 줄씩 읽는다.
             *
             * readLine() 결과가 null이라는 것은
             * 파일의 마지막까지 모두 읽었다는 뜻이다.
             */
            while ((line = reader.readLine()) != null) {

                /*
                 * 빈 줄이 있을 경우 건너뛴다.
                 */
                if (line.isBlank()) {
                    continue;
                }

                /*
                 * CSV 한 줄을 쉼표(,) 기준으로 분리한다.
                 *
                 * 예:
                 * 1,홍길동,hong@test.com,2026-07-18T14:30:00
                 *
                 * 결과:
                 * columns[0] = "1"
                 * columns[1] = "홍길동"
                 * columns[2] = "hong@test.com"
                 * columns[3] = "2026-07-18T14:30:00"
                 *
                 * -1을 사용하면 마지막 값이 비어 있어도
                 * 배열 요소를 유지할 수 있다.
                 */
                String[] columns = line.split(",", -1);

                /*
                 * 정상적인 CSV라면 컬럼이 총 4개여야 한다.
                 */
                if (columns.length != 4) {
                    log.warn("CSV 컬럼 개수 오류. 해당 행 건너뜀: {}", line);
                    continue;
                }

                /*
                 * CSV 문자열 데이터를 Java 자료형으로 변환한다.
                 */
                Long id = Long.valueOf(columns[0]);
                String name = columns[1];
                String email = columns[2];

                /*
                 * CSV 생성 시 LocalDateTime을 문자열로 저장했기 때문에
                 * LocalDateTime.parse()로 다시 LocalDateTime 타입으로 변환한다.
                 *
                 * 예:
                 * "2026-07-18T14:30:00"
                 */
                LocalDateTime createdAt =
                        LocalDateTime.parse(columns[3]);

                /*
                 * CSV 한 행을 TableData 객체 하나로 만든다.
                 */
                TableData tableData = TableData.builder()
                        .id(id)
                        .name(name)
                        .email(email)
                        .createdAt(createdAt)
                        .build();

                /*
                 * MyBatis Mapper를 호출하여 TABLE_C에 INSERT한다.
                 *
                 * Mapper
                 * → TableMapper.xml의 insertTableC
                 * → Oracle TABLE_C
                 */
                tableMapper.insertTableC(tableData);

                /*
                 * 적재 성공 건수를 1 증가시킨다.
                 */
                insertCount++;
            }
        }

        /*
         * 배치 실행 결과 로그 출력
         */
        log.info("CSV → TABLE_C 적재 완료");
        log.info("CSV 파일 경로 = {}", csvFilePath);
        log.info("총 적재 건수 = {}", insertCount);

        /*
         * 현재 Tasklet의 작업이 끝났음을 Spring Batch에 알린다.
         */
        return RepeatStatus.FINISHED;
    }
}