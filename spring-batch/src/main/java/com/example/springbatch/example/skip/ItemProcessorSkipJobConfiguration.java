package com.example.springbatch.example.skip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemProcessorSkipJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 5;

    @Bean
    public Job itemProcessorSkipJob() {
        return jobBuilderFactory.get("itemProcessorSkipJob")
                .start(itemProcessorSkipStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step itemProcessorSkipStep() {
        return stepBuilderFactory.get("itemProcessorSkipStep")
                .<String, String>chunk(chunkSize)
                .reader(customItemReader2())
                .processor(customItemProcessor())
                .writer(items -> System.out.println("items = " + items))
                .faultTolerant()
                .skip(SkippableException.class)
                .skipLimit(3)
                .build();
    }

    private ItemProcessor<? super String, String> customItemProcessor() {
        return item -> {
            System.out.println("itemProcessor " + item);

            if (item.equals("3"))
                throw new SkippableException("Process Failed");
            return item;
        };
    }

    @Bean
    public ItemReader<String> customItemReader2() {
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
