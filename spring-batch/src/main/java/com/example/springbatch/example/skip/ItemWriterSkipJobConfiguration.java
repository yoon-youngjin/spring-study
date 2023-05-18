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
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemWriterSkipJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 5;

    @Bean
    public Job itemWriterSkipJob() {
        return jobBuilderFactory.get("itemWriterSkipJob")
                .start(itemWriterSkipStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step itemWriterSkipStep() {
        return stepBuilderFactory.get("itemWriterSkipStep")
                .<String, String>chunk(chunkSize)
                .reader(customItemReader3())
                .processor(customItemProcessor2())
                .writer(customItemWriter())
                .faultTolerant()
                .skip(SkippableException.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    public ItemWriter<? super String> customItemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println("ItemWriter " + item);
                if (item.equals("4")){
                    throw new SkippableException("4");
                }
            }
            System.out.println("items = " + items);
        };
    }

    @Bean
    public ItemProcessor<? super String, String> customItemProcessor2() {
        return item -> {
            System.out.println("itemProcessor " + item);
            return item;
        };
    }

    @Bean
    public ItemReader<String> customItemReader3() {
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
