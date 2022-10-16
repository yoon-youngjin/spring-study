# 데이터 동기화를 위한 Apache Kafka 활용 - 2

Orders Microservice와 Catalogs Microservice에 Kafka Topic의 적용

데이터 동기화 1 - Orders -> Catalogs

- Orders Service에 요청 된 주문의 수량 정보를 Catalog Service에 반영 -> 수량 감소
- Orders Service에서 Kafka Topic으로 메시지 전송 -> Producer
- Catalog Service에서 Kafka Topic에 전송 된 메시지 취득 -> Consumer

![image](https://user-images.githubusercontent.com/83503188/195137234-1a936cd7-ebf4-44d1-b6c1-475390854282.png)

Orders Service에서 Kafka로 상품의 수량 관련 정보를 전달하면 Kafka의 Topic에 저장되었다가 Topic을 등록한 Consumer가 변경된 데이터 값을 가져가서 자신의 테이블에 반영시키는 형태

Catalogs Service -> Consumer

라이브러리 추가
- spring-kafka

**KafkaConsumerConfig**

```java
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); // Kafka 주소
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroupId"); // 그룹아이디란 카프카에서 토픽에 쌓여있는 메시지를 가져가는 Consumer를 그룹핑할 수 있다. 
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // key와 value가 한 세트로 저장되어있을 때 값을 가져와서 해석, 둘다 String
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        return kafkaListenerContainerFactory;
    }
}

```
- ConsumerFactory: 토픽에 접속하기 위한 정보를 가진 Factory 빈 생성
- ConcurrentKafkaListenerContainerFactory: 토픽에 변경사항이 존재하는 이벤트를 리스닝하는 빈
- properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroupId"); : 그룹아이디란 카프카에서 토픽에 쌓여있는 메시지를 가져가는 Consumer를 그룹핑할 수 있다.
  **kafkaConsumer**


```java
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    @KafkaListener(topics = "example-order-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message: =====> " + kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CatalogEntity entity = catalogRepository.findByProductId((String) map.get("productId"));
        if (entity != null) {
            entity.setStock(entity.getStock() - (Integer) map.get("qty"));
            catalogRepository.save(entity);
        }
    }
}


```
@KafkaListener(topics = "example-order-topic"): 변경을 확인할 토픽 이름을 명시


Orders Service -> Producer

라이브러리 추가
- spring-kafka

**KafkaProducerConfig**

```java
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

```

- KafkaTemplate: 토픽에 데이터를 전달하기 위해서 사용되는 빈


**KafkaProducer**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderDto send(String kafkaTopic, OrderDto orderDto) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";

        try {
            jsonInString = mapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(kafkaTopic, jsonInString);
        log.info("Kafka Producer send data from the Order microservice: " + orderDto);

        return orderDto;
    }
}
```

`jsonInString = mapper.writeValueAsString(orderDto);`: 주문 정보를 json 포맷으로 전달하기 위해서 변환
현재는 토픽에 용도가 단순히 메시지를 전달하는 용도로만 쓰이고 메시지를 가져가는 Consumer에서 다시 해석하는 과정을 거치기때문에
단순히 OrderDto값을 직렬화해서 보내도 상관없다. 따라서 이전처럼 Schema 정보를 넣는 행위가 필요없다.

**OrderController**

```java
@RestController
@RequestMapping("/order-service")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
...

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
        OrderDto createdOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        kafkaProducer.send("example-order-topic", orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }
...
}
```

주문 전 catalog
![image](https://user-images.githubusercontent.com/83503188/195149414-58f57370-3959-48ea-8060-80eacf5d8101.png)

주문
![image](https://user-images.githubusercontent.com/83503188/195149640-8fbf80d3-8208-43e9-adfa-a77c59100098.png)

주문 후  order 증가, catalog 감소
![image](https://user-images.githubusercontent.com/83503188/195149832-5bf08ed2-ab09-48cf-8f6b-c537b35e1501.png)

![image](https://user-images.githubusercontent.com/83503188/195149726-2d83db23-7d67-4eb8-ae49-d4bcec16e057.png)

![image](https://user-images.githubusercontent.com/83503188/195149966-f54b8981-1461-4dad-8314-ee93f50ee256.png)

Multi Orders Microservice 사용에 대한 데이터 동기화 문제

Orders Service 2개 기동
- Users의 요청 분산 처리
- Orders 데이터도 분산 저장 -> 동기화 문제

![image](https://user-images.githubusercontent.com/83503188/195150635-a0e7b0aa-6678-4f6d-a184-cb057a7d6853.png)

주문 5개 생성

![image](https://user-images.githubusercontent.com/83503188/195151248-201ed857-44e2-426b-82b4-11771b67f722.png)

![image](https://user-images.githubusercontent.com/83503188/195151221-fd9b8e11-ddc6-440a-a5a3-0c4b82076556.png)

각 인스턴스에 3개 2개 나눠서 생성

유저에서 조회하게되면 다른 결과를 확인하게되는 문제가 발생

Kafka 메세징 서버를 이용해서 해결


Kafka Connect를 활용한 단일 데이터베이스를 사용

Multiple Orders Service에서의 동기화

- Orders Service에 요청 된 주문 정보를 DB가 아니라 Kafka Topic으로 전송
- Kafka Topic에 설정 된 Kafka Sink Connect를 사용해 단일 DB에 저장 -> 데이터 동기화

![image](https://user-images.githubusercontent.com/83503188/195775271-5a86603b-f563-4983-bc52-8bf7cdac31e4.png)

메시지값을 kafka sink connect를 이용해서 단일 데이터베이스로 전송

- kafka topic에 메시지를 전달해주는 것이 source connect
- topic에서 데이터를 가져가서 사용하는 것이 sink connect

각각의 Order Service가 가진 데이터를 제거하고 각각의 Order Service로부터 전달된 메시지값을 메시지 큐잉 서버에 전달하게되면 메시지 서버가 가지고 있던 토픽의 데이터값을 sink connect에 의해서 단일 데이터베이스로 전달


Orders Microservice 수정 - MariaDB

```sql
create table `order` (

  id int auto_increment primary key,

  user_id varchar(50) not null,

  product_id varchar(20) not null,

  order_id varchar(50) not null,

  qty int default 0,

  unit_price int default 0,

  total_price int default 0,

  created_at datetime default now()

)
```

Orders Service의 JPA 데이터베이스 교체
- H2 DB -> MariaDB

```yml
...
  datasource:
    url: jdbc:mariadb://localhost:3306/mydb
    driver-class-name: org.mariadb.jdbc.Driver
    username: root
    password: 1234
#    driver-class-name: org.h2.Driver
#    url: jdbc:h2:mem:testdb
```

Orders Microservice 수정 - Orders Kafka Topic

Orders Service의 Controller 수정

```java
@RestController
@RequestMapping("/order-service")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
//        OrderDto createdOrder = orderService.createOrder(orderDto);
//        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        /* Send an order to the Kafka */
//        kafkaProducer.send("example-order-topic", orderDto);

        /* kafka */
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
        ResponseOrder responseOrder = mapper.map(orderDetails, ResponseOrder.class);

        kafkaProducer.send("example-category-topic", orderDto);
        orderProducer.send("orders", orderDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }
...
}

```

- kafkaProducer.send("example-category-topic", orderDto);: order와 catalog를 연동하기 위한 kafka producer
- orderProducer.send("orders", orderDto);: 사용자의 주문 정보를 kafka topic에 전달시키는 용도

Orders Service의 Producer에서 발생하기 위한 메시지 등록

![image](https://user-images.githubusercontent.com/83503188/195780064-731fda3a-8b71-4e64-a9b2-fc84b256113e.png)

- 기존에 가진 주문 정보를 어떻게 Topic에 보낼것인지가 중요
- Topic에 쌓인 메시지들은 sink connect에 의해서 토픽의 메시지 내용들을 열어서 형태를 파악하고 해당하는 테이블에 저장된다.
- 데이터의 형태가 맞지 않으면 데이터베이스에 저장 실패

schema: 테이블의 구조
- field: 각각의 데이터베이스의 필드에 저장될 값의 형태
  payload: 실제 저장될 값

![image](https://user-images.githubusercontent.com/83503188/195780569-2dca268c-931a-4477-9d9d-94e4ecdb587a.png)

Schema, Field, Payload를 클래스로 만듦으로써 ObjectMapper와 같은 API를 이용해서 자바의 Object를 json으로 쉽게 변경이 가능해진다.

Orders Service의 OrderProducer 생성

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProducer {
    private KafkaTemplate<String, String> kafkaTemplate;

    List<Field> fields = Arrays.asList(new Field("String", true, "order_id"),
            new Field("string", true, "user_id"),
            new Field("string", true, "product_id"),
            new Field("int32", true, "qty"),
            new Field("int32", true, "total_price"),
            new Field("int32", true, "unit_price")
    );

    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("orders")
            .build();

    public OrderDto send(String kafkaTopic, OrderDto orderDto) {
        Payload payload = Payload.builder()
                .order_id(orderDto.getOrderId())
                .user_id(orderDto.getUserId())
                .product_id(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unit_price(orderDto.getUnitPrice())
                .total_price(orderDto.getTotalPrice())
                .build();

        KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema, payload);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(kafkaTopic, jsonInString);
        log.info("Order Producer send data from the Order Microservice: " + kafkaTopic);

        return orderDto;
    }
}

```

- Schema는 불변이기 때문에 멤버 변수로 선언
- 실제로 변경되는 부분인 payload는 send 메서드에 선언

Orders Service를 위한 Kafka Sink Connector 추가

![image](https://user-images.githubusercontent.com/83503188/195782842-f4627efe-ee2b-4e82-bc2f-2096f70e72be.png)

Orders Serivce 2개 기동

결과

![image](https://user-images.githubusercontent.com/83503188/195791157-6609faa3-36bd-46f6-9e04-bc8363f73c90.png)
![image](https://user-images.githubusercontent.com/83503188/195791263-48ff78bb-28fa-4066-bc38-695c00c67a94.png)

![image](https://user-images.githubusercontent.com/83503188/195791312-03365ae5-a1a2-4cc4-bab4-9c13f1d5b125.png)


이로써 Kafka Topic에 저장된 값을 단일 데이터베이스로 옮기기 위해서 sink connector를 연동 완료

이후에 Microservice를 확장해서 어플리케이션을 응용하고싶으면 데이터베이스에 저장되는 메시지 큐잉 서버를 Event Sourcing이라는 데이터를 저장하는 부분과 읽어오는 부분을 분리해서 만드는 CQRS 패턴을 이용하면 좀 더 효율적으로 메시징기반의 시스템을 이용할 수 있으며 시간 순서에 의해서 메시지가 기록된 것을 데이터베이스 업데이트하는 기능도 구현이 가능하다.
