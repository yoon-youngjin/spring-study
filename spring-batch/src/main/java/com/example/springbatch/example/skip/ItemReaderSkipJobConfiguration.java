package com.example.springbatch.example.skip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemReaderSkipJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 5;

    @Bean
    public Job skipJob() {
        return jobBuilderFactory.get("skipJob")
                .start(skipStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step skipStep() {
        return stepBuilderFactory.get("skipStep")
                .<String, String>chunk(chunkSize)
                .reader(customItemReader())
                .writer(items -> System.out.println("items = " + items))
                .faultTolerant()
                .skip(SkippableException.class)
                .skipLimit(4)
                .build();
    }

    @Bean
    public ItemReader<String> customItemReader() {
        return new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws SkippableException {
                i++;
                if (i==3){
                    throw new SkippableException("skip exception");
                }
                System.out.println("itemReader : " + i);
                return i > 20 ? null : String.valueOf(i);
            }
        };
    }

}
