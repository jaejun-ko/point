package com.musinsapayments.pointbatch;

import com.musinsapayments.pointcore.PointCoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Import(PointCoreConfiguration.class)
@SpringBootApplication
public class PointBatchApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application-core,application-batch");
        SpringApplication.run(PointBatchApplication.class, args);
    }

}
