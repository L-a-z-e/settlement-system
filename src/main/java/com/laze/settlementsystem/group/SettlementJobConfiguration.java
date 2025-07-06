package com.laze.settlementsystem.group;

import com.laze.settlementsystem.support.DateFormatJobParametersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class SettlementJobConfiguration {

    private final JobRepository jobRepository;

    @Bean
    public Job settlementJob(
            Step preSettlementDetailStep,
            Step settlementDetailStep,
            Step settleGroupStep
    ) {
        return new JobBuilder("settlementJob", jobRepository)
                .validator(new DateFormatJobParametersValidator(new String[]{"targetDate"}))
                .incrementer(new RunIdIncrementer())
                .start(preSettlementDetailStep)
                .next(settlementDetailStep)
                .next(isFridayDecider())
                .on("COMPLETED").to(settleGroupStep)
                .build()
                .build();
    }

    // 매주 금요일마다 주간 정산을 한다.
    public JobExecutionDecider isFridayDecider() {
        return new JobExecutionDecider() {
            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                final String targetDate = stepExecution.getJobParameters().getString("targetDate");
                final LocalDate date = LocalDate.parse(targetDate, formatter);

                if(date.getDayOfWeek() != DayOfWeek.FRIDAY) {
                    return new FlowExecutionStatus("NOOP");
                }

                return FlowExecutionStatus.COMPLETED;
            }
        };
    }
}
