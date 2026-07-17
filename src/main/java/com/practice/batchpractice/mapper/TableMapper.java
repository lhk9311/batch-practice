package com.practice.batchpractice.mapper;

import com.practice.batchpractice.domain.TableData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TableMapper {

    int selectTableACount();

    int insertTableBCount(@Param("dataCount") int dataCount);

    List<TableData> selectAllTableA();

    int insertTableC(TableData tableData);

    int truncateTableC();
}