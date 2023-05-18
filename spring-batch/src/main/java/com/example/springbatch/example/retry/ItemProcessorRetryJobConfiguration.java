package com.example.springbatch.example.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemProcessorRetryJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 5;

    @Bean
    public Job itemProcessorRetryJob() {
        return jobBuilderFactory.get("itemProcessorRetryJob")
                .start(itemProcessorRetryStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step itemProcessorRetryStep() {
        return stepBuilderFactory.get("itemProcessorRetryStep")
                .<String, String>chunk(chunkSize)
                .reader(customItemReader())
                .processor(customItemProcessor())
                .writer(customItemWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .skip(RetryableException.class)
                .skipLimit(2)
                .build();
    }

    private ItemWriter<? super String> customItemWriter() {
        return items -> {
            log.error("items " + items);
            System.out.println("items " + items);
        };
    }

    private ItemProcessor<? super String, String> customItemProcessor() {
        return item -> {
            if (item.equals("4"))
                throw new RetryableException("Process Failed");

            log.error("itemProcessor " + item);
            System.out.println("itemProcessor " + item);
            return item;
        };
    }

    private ItemReader<String> customItemReader() {
        return new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() {
                i++;
                log.error("itemReader : " + i);
                System.out.println("itemReader : " + i);
                return i > 5 ? null : String.valueOf(i);
            }
        };
    }

}
