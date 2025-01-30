package com.musinsapayments.pointbatch.job;

import com.musinsapayments.pointcore.domain.point.Point;
import com.musinsapayments.pointcore.domain.point.PointTransactionType;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@RequiredArgsConstructor
@Configuration
public class PointExpirationJobConfiguration {

    private final static int CHUNK_SIZE = 50;

    public final static String JOB_NAME = "pointExpirationJob";
    public final static String STEP_NAME = "pointExpirationStep";

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job pointExpirationJob(JobRepository jobRepository, Step pointExpirationStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(pointExpirationStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step pointExpirationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<Point, Point>chunk(CHUNK_SIZE, transactionManager)
                .reader(expiredPointReader())
                .processor(pointProcessor())
                .writer(pointWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Point> expiredPointReader() {
        JpaPagingItemReader<Point> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT p FROM Point p WHERE p.expireAt <= CURRENT_TIMESTAMP AND p.expired = false");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @Bean
    public ItemProcessor<Point, Point> pointProcessor() {
        return point -> {
            point.expire();
            var expiredPoint = Point.builder()
                .user(point.getUser())
                .amount(-point.getRemainingAmount())
                .remainingAmount(-point.getRemainingAmount())
                .transactionType(PointTransactionType.EXPIRE)
                .relatedPoint(point)
                .build();
            point.use(point.getRemainingAmount());
            return expiredPoint;
        };
    }

    @Bean
    public JpaItemWriter<Point> pointWriter() {
        JpaItemWriter<Point> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
