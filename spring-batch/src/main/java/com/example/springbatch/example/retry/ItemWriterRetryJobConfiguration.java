package com.example.springbatch.example.retry;

import com.example.springbatch.example.skip.SkippableException;
import lombok.RequiredArgsConstructor;
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

@Configuration
@RequiredArgsConstructor
public class ItemWriterRetryJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 5;

    @Bean
    public Job itemWriterRetryJob() {
        return jobBuilderFactory.get("itemWriterRetryJob")
                .start(itemWriterRetryStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step itemWriterRetryStep() {
        return stepBuilderFactory.get("itemWriterRetryStep")
                .<String, String>chunk(chunkSize)
                .reader(customItemReader())
                .processor(customItemProcessor())
                .writer(customItemWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .build();
    }

    private ItemWriter<? super String> customItemWriter() {
        return items -> {
            for (String item : items) {
                if (item.equals("4")) {
                    throw new RetryableException("4");
                }
            }
            System.out.println("items " + items);
        };
    }

    private ItemProcessor<? super String, String> customItemProcessor() {
        return item -> {
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
                System.out.println("itemReader : " + i);
                return i > 5 ? null : String.valueOf(i);
            }
        };
    }

}
