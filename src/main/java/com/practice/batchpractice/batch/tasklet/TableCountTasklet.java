package com.practice.batchpractice.batch.tasklet;

import com.practice.batchpractice.mapper.TableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableCountTasklet implements Tasklet {

    private final TableMapper tableMapper;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) {

        int count = tableMapper.selectTableACount();

        tableMapper.insertTableBCount(count);

        log.info("TABLE_A COUNT = {}", count);

        return RepeatStatus.FINISHED;
    }
}