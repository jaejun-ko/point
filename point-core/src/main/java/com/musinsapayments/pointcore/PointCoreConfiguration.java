package com.musinsapayments.pointcore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@ComponentScan
@EnableAutoConfiguration
@EnableJpaAuditing
public class PointCoreConfiguration {
}
