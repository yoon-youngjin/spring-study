package com.example.springbatch.example.repeat;

import com.example.springbatch.entity.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RepeatJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private int chunkSize = 10;

    @Bean
    public Job repeatJob() {
        return jobBuilderFactory.get("repeatJob")
                .start(repeatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    private Step repeatStep() {
        return stepBuilderFactory.get("step")
                .<Pay, Pay>chunk(chunkSize)
                .reader(jpaPagingItemReader())
//                .processor(new ItemProcessor<Pay, Pay>() {
//
//                    RepeatTemplate repeatTemplate = new RepeatTemplate();
//
//                    @Override
//                    public Pay process(Pay item) throws Exception {
//
//                        CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();
//                        CompletionPolicy[] completionPolicies = new CompletionPolicy[]{
////                                new SimpleCompletionPolicy(3),
//                                new TimeoutTerminationPolicy(3000)
//                        };
//                        compositeCompletionPolicy.setPolicies(completionPolicies);
//                        repeatTemplate.setCompletionPolicy(compositeCompletionPolicy);
//
//                        repeatTemplate.iterate(new RepeatCallback() {
//                            @Override
//                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
//                                System.out.println(item + " repeat");
//                                return RepeatStatus.CONTINUABLE;
//                            }
//                        });
//                        return item;
//                    }
//                })
                .processor(new ItemProcessor<>() {

                    RepeatTemplate repeatTemplate = new RepeatTemplate();

                    @Override
                    public Pay process(Pay item) {
                        // 해당 item 3번 반복
                        repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
                        // 3초 동안 반복
//                        repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(3000));
                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                System.out.println(item + " repeat");
                                return RepeatStatus.CONTINUABLE;
                            }
                        });
                        return item;
                    }
                })
                .writer(items -> System.out.println("items = " + items))
                .build();
    }

    @Bean
    public JpaPagingItemReader<Pay> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Pay>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Pay p WHERE amount >= 2000 ORDER BY id")
                .build();
    }

}
