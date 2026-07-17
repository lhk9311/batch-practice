package com.practice.batchpractice.service;

import com.practice.batchpractice.mapper.TableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableMapper tableMapper;

    public int getTableACount() {
        return tableMapper.selectTableACount();
    }
}