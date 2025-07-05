package com.laze.settlementsystem.detail;

import com.laze.settlementsystem.domain.ApiOrder;
import com.laze.settlementsystem.domain.SettleDetail;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SettleDetailStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    // 첫번째 Step은 파일의 고객 + 서비스 별로 집계를 해서 Execution Context 안에 넣는다.
    // 두번째 Step은 집계된 Execution Context 데이터를 가지고 DB에 write 한다.

    @Bean
    public Step preSettlementDetailStep(
            FlatFileItemReader<ApiOrder> preSettlementDetailReader,
            PreSettlementDetailWriter preSettlementDetailWriter,
            ExecutionContextPromotionListener executionContextPromotionListener
    ) {
        return new StepBuilder("preSettlementDetailStep", jobRepository)
                .<ApiOrder, Key>chunk(10000, platformTransactionManager)
                .reader(preSettlementDetailReader)
                .processor(new PreSettlementDetailProcessor())
                .writer(preSettlementDetailWriter)
                .listener(executionContextPromotionListener)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<ApiOrder> preSettlementDetailReader(
            @Value("#{jobParameters['targetDate']}") String targetDate
    ) {
        final String fileName = targetDate + "_api_orders.csv";

        return new FlatFileItemReaderBuilder<ApiOrder>()
                .name("preSettlementDetailReader")
                .resource(new ClassPathResource("/datas/" + fileName))
                .linesToSkip(1)
                .delimited()
                .names("id", "customerId", "url", "state", "createdAt")
                .targetType(ApiOrder.class)
                .build();
    }

    @Bean
    public Step settlementDetailStep(
            SettlementDetailReader settlementDetailReader,
            SettlementDetailProcessor settlementDetailProcessor,
            JpaItemWriter<SettleDetail> settlementDetailWriter
    ) {
        return new StepBuilder("settlementDetailStep", jobRepository)
                .<KeyAndCount, SettleDetail>chunk(1000, platformTransactionManager)
                .reader(settlementDetailReader)
                .processor(settlementDetailProcessor)
                .writer(settlementDetailWriter)
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener executionContextPromotionListener() {
        final ExecutionContextPromotionListener executionContextPromotionListener = new ExecutionContextPromotionListener();
        executionContextPromotionListener.setKeys(new String[]{"snapshots"});

        return executionContextPromotionListener;
    }

    @Bean
    public JpaItemWriter<SettleDetail> settlementDetailWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<SettleDetail>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
