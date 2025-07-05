package com.laze.settlementsystem.detail;

import com.laze.settlementsystem.support.DateFormatJobParametersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SettlementJobConfiguration {

    private final JobRepository jobRepository;

    @Bean
    public Job settlementJob(
            Step preSettlementDetailStep,
            Step settlementDetailStep
    ) {
        return new JobBuilder("settlementJob", jobRepository)
                .validator(new DateFormatJobParametersValidator(new String[]{"targetDate"}))
                .incrementer(new RunIdIncrementer())
                .start(preSettlementDetailStep)
                .next(settlementDetailStep)
                .build();
    }
}
