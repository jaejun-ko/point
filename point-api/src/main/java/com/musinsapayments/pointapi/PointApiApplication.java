package com.musinsapayments.pointapi;

import com.musinsapayments.pointcore.PointCoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(PointCoreConfiguration.class)
@SpringBootApplication
public class PointApiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application-core,application-api");
        SpringApplication.run(PointApiApplication.class, args);
    }

}
