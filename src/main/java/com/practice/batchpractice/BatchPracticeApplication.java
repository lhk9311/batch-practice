package com.practice.batchpractice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.practice.batchpractice.mapper")
@SpringBootApplication
public class BatchPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchPracticeApplication.class, args);
    }
}