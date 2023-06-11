## Kafka를 활용하여 문제 해결하기

### Kafka 알아보기 

> 카프카란?

분산 이벤트 스트리밍 플랫폼

이벤트 스트리밍이란 소스에서 목적지까지 이벤트를 실시간 스트리밍 하는 것

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/a513a091-4b01-4d89-8476-370b6a794efd)
- Topic은 간단하게 큐 
- Topic에 데이터를 삽입할 수 있는 대상이 Producer(소스)
- Topic에서 데이터를 가져갈 수 있는 대상이 Consumer(목적지)

**토픽 생성**

```
docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --create --topic testTopic
```

**프로듀서 실행**

```
docker exec -it kafka kafka-console-producer.sh --topic testTopic --broker-list 0.0.0.0:9092
```

**컨슈머 실행**

```
docker exec -it kafka kafka-console-consumer.sh --topic testTopic --bootstrap-server localhost:9092
```

### Producer 사용하기

```java
@Configuration
public class KafkaProducerConfig {

    // producer 인스턴스를 생성하기 위해 설정값을 할당해야한다.
    // 스프링에서는 손쉽게 설정값을 설정할 수 있도록 ProducerFactory 인터페이스를 제공
    @Bean
    public ProducerFactory<String, Long> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // 서버 정보
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    // 토픽에 데이터를 전송하기 위한 KafakTemplate
    @Bean
    public KafkaTemplate<String, Long> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

```java
@Component
@RequiredArgsConstructor
public class CouponCreateProducer {
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public void create(Long userId) {
        kafkaTemplate.send("coupon_create", userId);
    }

}
```

```java
    public void apply(Long userId) {
        Long count = couponCountRepository.increment();
//        long count = couponRepository.count();

        if (count > 100) {
            return;
        }

//        couponRepository.save(new Coupon(userId));
        couponCreateProducer.create(userId);
    }
```

**결과**

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/4c2c2b98-ce92-447c-8fe4-39103433416d)

### Consumer 사용하기

```java
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, Long> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
```

```java
@Component
@RequiredArgsConstructor
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        couponRepository.save(new Coupon(userId));
    }
}
```