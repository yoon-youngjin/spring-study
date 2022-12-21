# 애플리케이션 배포 - Docker Container

## Create Bridge Network

### Docker Network

다양한 서비스들이 하나의 가상의 네트워크를 가짐으로써 서로 통신할때 불편함이 없도록 설정

**Bridge network**

`docker network create --driver bridge [브릿지 이름]`

**Host network**
- 네트워크를 호스트로 설정하면 호스트의 네트워크 환경을 그대로 사용
- 포트 포워딩 없이 내부 애플리케이션 사용

**None network**
- 네트워크를 사용하지 않음
- io 네트워크만 사용, 외부와 단절

```
docker network create ecommerce-network
docker network ls
```
> 참고
> 
> `docker system prune`: stop container 삭제, 불필요한 네트워크 삭제, 사용되지 않는 이미지, 캐시 삭제

**Docker network 생성**

```text
docker network create --gateway 172.18.0.1 --subnet 172.18.0.0/16 ecommerce-network
```

**network 상세 조회**
```text
docker network inspect ecommerce-network
```

![image](https://user-images.githubusercontent.com/83503188/199435389-8e461358-3d77-42de-9db3-1a03c544a70f.png)

![image](https://user-images.githubusercontent.com/83503188/199437068-70621bcc-0496-48e6-8293-4fcaaf705f67.png)
- 컨테이너에서 사용할 네트워크를 직접 생성하여 사용하면 좋은점은 일반적인 컨테이너는 하나의 guest os라고 생각하면 각각의 ip가 할당되는데 컨테이너들은 이러한 ip 주소를 통해 서로 통신을 하게 되는데 같은 네트워크에 포함된 컨테이너끼리는 ip 주소 외에도 컨테이너 id, 이름을 통해서 통신이 가능해진다.


**RabbitMQ -> Docker Container**

Configuration Service의 변경 내역을 모든 마이크로서비스에 한 번에 업데이트 시켜주기 위해서 Spring Cloud Bus를 이용했고, Spring Cloud Bus에서 사용할 수 있는 Message Queueing Server로써 RabbitMQ를 사용했다.

기존에 RabbitMQ를 Local 로 기동 -> **Docker Container화**

```text
docker run -d --name rabbitmq --network ecommerce-network \
 -p 15672:15672 -p 5672:5672 -p 15671:15671 -p 5671:5671 -p 4369:4369 \
 -e RABBITMQ_DEFAULT_USER=guest \
 -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:management
```
- `--name`: 네트워크에서 서로 통신 가능한 고유한 이름
- `--network`: 네트워크 지정, 네트워크를 설정하지 않으면 기본적으로 도커가 가진 bridge 네트워크를 가져와서 사용


**결과**

![image](https://user-images.githubusercontent.com/83503188/199439865-9561712f-ffda-4e9e-a05f-1b3f6008e84a.png)

![image](https://user-images.githubusercontent.com/83503188/199440042-b31c51ae-41df-4636-95e6-6868ece548ee.png)
- 네트워크에 rabbitmq container가 추가됨을 볼 수 있다.


## Configuration Service

- Docker Image로 변환하는 작업이 선행되어야한다.
- Configuration Server에 포함된 암호화에 필요한 key를 복사해야한다.
- 또한 사용되는 keyfile의 위치를 변화해야한다.

![image](https://user-images.githubusercontent.com/83503188/199441442-cdeffb21-8c68-4670-88e1-49a567d4261b.png)

**Dockerfile 생성**

```dockerfile
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY target/config-service-1.0.jar ConfigServer.jar
ENTRYPOINT ["java", "-jar", "ConfigServer.jar"]
```

**Image 빌드**
```text
docker build --tag yoon11/config-service:1.0 .
```

**Config service 실행**

```text
docker run -d -p 8888:8888 --network ecommerce-network \
 -e "spring.rabbitmq.host=rabbitmq" \
 -e "spring.profiles.active=default" \
  --name config-service edowon0623/config-service:1.0
```
현재 설정 파일에 host가 127.0.0.1로 기입되어있는데 실행할 때 설정을 주입

**결과**

![image](https://user-images.githubusercontent.com/83503188/199450161-86cd2d6e-434a-4bed-8315-dbeaedfe5d26.png)
- 네트워크에 2개의 컨테이너가 등록됨을 확인할 수 있다.

**bootstrap.yml에서 key-store 위치 변경 후 다시 빌드 -> 도커 이미지 생성**
```yml
encrypt:
#  key: asdfasdfsadfsdf
  key-store:
#    location: file:///C:\Users\dudwl\WorkSpace\SSS\msa-with-spring-cloud\keystore\apiEncryptionKey.jks
    location: file:/apiEncryptionKey.jks
    password: yoon1234
    alias: apiEncryptionKey
```

## Discovery Service

**Dockerfile 생성**

```dockerfile
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/ecommerce-1.0.jar DiscoveryService.jar
ENTRYPOINT ["java", "-jar", "DiscoveryService.jar"]
```

**Docker Image 생성**

```text
docker build --tag yoon11/discovery-service:1.0 .
```

**docker hub에 push**

```text
docker push yoon11/discovery-service:1.0
```

**discovery service 실행**

```text
docker run -d -p 8761:8761 --network ecommerce-network \
 -e "spring.cloud.config.uri=http://config-service:8888" \
 --name discovery-service edowon0623/discovery-service:1.0
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/199454490-a439340f-0cdf-4011-b2c5-1045104328b5.png)
- ecommerce-network에 등록된 모습

![image](https://user-images.githubusercontent.com/83503188/199454692-81ff0735-2b38-4216-8d4f-b0e813c5595d.png)

## ApiGateway Service

**Dockerfile 생성**

```dockerfile
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/apigateway-service-1.0.jar ApiGatewayService.jar
ENTRYPOINT ["java", "-jar", "ApiGatewayService.jar"]
```

**Docker Image 생성**

```text
docker build --tag yoon11/apigateway-service:1.0 .
```

**docker hub에 push**

```text
docker push yoon11/apigateway-service:1.0
```

**apigateway service 실행**

```text
docker run -d -p 8000:8000 --network ecommerce-network \
 -e "spring.cloud.config.uri=http://config-service:8888" \
 -e "spring.rabbitmq.host=rabbitmq" \
 -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
 --name apigateway-service \
 yoon11/apigateway-service:1.0
```
- 3가지 설정 정보(spring.cloud.config.uri, spring.rabbitmq.host, eureka.client.serviceUrl.defaultZone) 변경하여 실행

**결과**

![image](https://user-images.githubusercontent.com/83503188/199457415-f2381d94-d745-4102-a86d-1209ff41937c.png)
- ecommerce-network에 등록된 모습

## MariaDB

**Dockerfile 생성**

```dockerfile
FROM mariadb
ENV MYSQL_ROOT_PASSWORD 1234
ENV MYSQL_DATABASE mydb
COPY ./mysql_data/data /var/lib/mysql
EXPOSE 3306
ENTRYPOINT ["mysqld"]
```

- 데이터베이스를 만들때 테이블에 대한 정보가 있다면 script로 만들 수도 있고, 로컬에서 mariadb를 기동하면서 만들어둔 테이블을 컨테이너 안으로 복사할 수도 있다.
- 기존에 로컬에서 만든 db정보를 copy

**Docker Image 생성**

```text
docker build -t yoon11/my_mariadb:1.0 .
```

**mariadb 실행**
```text
docker run -d -p 3306:3306  --network ecommerce-network --name mariadb yoon11/my_mariadb:1.0
```

**mariadb 접속**
```text
docker exec -it mariadb /bin/bash
mysql -h127.0.0.1 -uroot -p
```

**root 권한 허용** 
root 계정에 어떠한 ip 주소로 접속된다고 하더라도 모든 데이터베이스에 허용할 수 있도록

```text
grant all privileges on *.* to 'root'@'%' identified by [password];
flush privileges;
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/199470537-b37f2fe0-eb8d-484a-add6-7db77ac1b1db.png)
- ecommerce-network에 등록된 모습

## Kafka

Kafka를 사용하기 위해서는 Zookeeper와 Kafka Server라고 불리는 Kafka Broker가 필요하다.

**Zookeeper + Kafka Standalone**
- docker-compose로 실행
- `git clone https://github.com/wurstmeister/kafka-docker`
- docker-compose-single-broker.yml 수정

> docker compose?
>
> 실행하려는 도커 컨테이너를 하나의 스크립트 파일로 실행할 수 있도록 만들어주는것

`docker-compose-single-broker.yml`

```yml
version: '2'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports:
      - "2181:2181"
    networks:
      my-network:
        ipv4_address: 172.18.0.100
  kafka:
    # build: .
    image: wurstmeister/kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 172.18.0.101
      KAFKA_CREATE_TOPICS: "test:1:1"
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    depends_on:
      - zookeeper
    networks:
      my-network:
        ipv4_address: 172.18.0.101

networks:
  my-network:
    name: ecommerce-network # 172.18.0.1~

```

**docker compose 실행**
```text
docker-compose -f docker-compose-single-broker.yml up -d
```

**docker compose 종료**
```text
docker-compose -f docker-compose-single-broker.yml down -d
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/199473747-3cd02075-3255-44c7-91a8-459c326b704a.png)
- zookeeper와 kafka가 컨테이너로 실행됨을 확인

![image](https://user-images.githubusercontent.com/83503188/199473920-f649c6b4-1971-42e7-a039-d09aca368053.png)
- 네트워크에도 compose에 정의한 대로 ip 할당

## Zipkin

**docker 실행**

```text
docker run -d -p 9411:9411 \
 --network ecommerce-network \
 --name zipkin \
 openzipkin/zipkin 
```


## Monitoring

Run Prometheus + Grafana

### Prometheus

```text
docker run -d -p 9090:9090 \
 --network ecommerce-network \
 --name prometheus \
 -v C:\Users\dudwl\Work\prometheus\prometheus-2.39.1.windows-amd64\prometheus-2.39.1.windows-amd64\prometheus.yml:/etc/prometheus/prometheus.yml \
 prom/prometheus 
```
- 로컬에 존재하는 `prometheus.yml`을 컨테이너 내부로 복사

**prometheus.yml** 수정

```yaml
# my global config
global:
  scrape_interval: 15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  # scrape_timeout is set to the global default (10s).

# Alertmanager configuration
alerting:
  alertmanagers:
    - static_configs:
        - targets:
          # - alertmanager:9093

# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: "prometheus"

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    static_configs:
      - targets: ["prometheus:9090"]

  - job_name: "user-service"
    scrape_interval: 15s
    metrics_path: "/user-service/actuator/prometheus"
    static_configs:
      - targets: ["apigateway-service:8000"]
  - job_name: "apigateway-service"
    scrape_interval: 15s
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["apigateway-service:8000"]
  - job_name: "order-service"
    scrape_interval: 15s
    metrics_path: "order-service/actuator/prometheus"
    static_configs:
      - targets: ["apigateway-service:8000"]

```

### Grafana

```text
docker run -d -p 3000:3000 \
 --network ecommerce-network \
 --name grafana \
 grafana/grafana 
```

**현재상황**

![image](https://user-images.githubusercontent.com/83503188/200021249-101ff82f-f2fe-48d3-9b87-9eda49e9bef4.png)

## User Microservice

```dockerfile
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /target/user-service-1.0.jar UserService.jar
ENTRYPOINT ["java", "-jar", "UserService.jar"]
```

```text
docker run -d --network ecommerce-network \
  --name user-service \
 -e "spring.cloud.config.uri=http://config-service:8888" \
 -e "spring.rabbitmq.host=rabbitmq" \
 -e "spring.zipkin.base-url=http://zipkin:9411" \
 -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
 -e "logging.file=/api-logs/users-ws.log" \
 yoon11/user-service
```


**Order Microservice**

```dockerfile
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /target/order-service-1.0.jar OrderService.jar
ENTRYPOINT ["java", "-jar", "UserService.jar"]
```

```text
docker run -d --network ecommerce-network \
  --name order-service \
 -e "spring.cloud.config.uri=http://config-service:8888" \
 -e "spring.rabbitmq.host=rabbitmq" \
 -e "spring.zipkin.base-url=http://zipkin:9411" \
 -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
 -e "spring.datasource.url=jdbc:mariadb://mariadb:3306/mydb" \
 -e "logging.file=/api-logs/orders-ws.log" \
 yoon11/order-service:1.0
```

**KafkaProducerConfig - 메시지 보낼때 사용하는 설정 변경**

```java
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.18.0.101:9092");
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

**mariadb 모든 ip 허용**

![image](https://user-images.githubusercontent.com/83503188/200112486-a7c9d2d2-dbec-4811-9651-2603fbb3432e.png)


## Catalog Service

**Dockerfile**

```dockerfile
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /target/catalog-service-1.0.jar CatalogService.jar
ENTRYPOINT ["java", "-jar", "CatalogService.jar"]
```

```text
docker run -d --network ecommerce-network \
  --name catalog-service \
 -e "spring.cloud.config.uri=http://config-service:8888" \
 -e "spring.rabbitmq.host=rabbitmq" \
 -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
 -e "logging.file=/api-logs/catalogs-ws.log" \
 yoon11/catalog-service:1.0
```

**KafkaConsumerConfig**

```java
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.18.0.101:9092"); // Kafka 주소
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


**Multi Profiles**

![image](https://user-images.githubusercontent.com/83503188/200112765-34b5dc06-7114-4a38-8281-eed9d6df837f.png)

![image](https://user-images.githubusercontent.com/83503188/200112792-7b717244-a8e9-48df-9558-85ddd84e04f1.png)

![image](https://user-images.githubusercontent.com/83503188/200112827-e1f10db9-6f07-419b-a078-9bc832a934ce.png)

