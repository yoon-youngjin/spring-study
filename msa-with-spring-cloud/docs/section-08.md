# Spring Cloud Bus

각각의 Microservice 가 좀 더 효율적으로 변경된 사항을 가져갈 수 있는 Spring Cloud Bus

### Spring Cloud Bus 개요

이전에 사용한 Actuator 를 사용하는 방법은 만약 수십, 수백개의 application 이 존재한다고 가정하면 각각의 어플리케이션마다 수동으로 refresh 를 호출해야 한다.

이러한 문제점을 해결할 수 있는 것이 Spring Cloud Bus

- 분산 시스템의 노드(Microservice)를 경량 메시지 브로커(RabbitMQ)와 연결
- 상태 및 구성에 대한 변경 사항을 연결된 노드에게 전달(Broadcast)

기존에는 하나의 어플리케이션에서 다른 어플리케이션을 연동하는 방식은 p2p 방식 직접적으로 이뤄졌다.

중간에 요청사항을 가진 미들웨어(Messaging Server, RabbitMQ)를 배치함으로써 좀 더 안정적이고 보내는 쪽과 받는 쪽이 서로에게 신경쓰지 않을 수 있다.


![image](https://user-images.githubusercontent.com/83503188/193805255-75b4c7fc-125c-43e2-8070-e0474d09bd1f.png)

Spring Cloud Bus 에 연결된 다양한 Microservice 에 데이터의 갱신을 push 방식으로 전달하는데, AMQP 라는 프로토콜을 사용한다.

**AMQP**
- 메시지 지향, 큐잉, 라우팅(P2P), 신뢰성, 보안
- Erlang, RabbitMQ에서 사용

**Kafka 프로젝트**
- Apache Software Foundation이 scalar 언어로 개발한 오픈 소스 메시지 브로커 프로젝트
- 분산형 스트리밍 플랫폼
- 대용량의 데이터를 처리 가능한 메시징 시스템


**RabbitMQ vs. Kafka**

**RabbitMQ** -> 보다 적은 데이터를 안전하게 전달함을 보장시키기 위한 솔루션
- 메시지 브로커
- 초당 20+ 메시지를 소비자(메시지를 받고자하는 시스템)에게 전달
- 메시지 전달 보장, 시스템 간 메시지 전달
- 브로커, 소비자 중심

**Kafka** -> 대용량 데이터를 빠른 시간내에 처리하기 위한 솔루션
- 초당 100k+ 이상의 이벤트 처리
- Pub/Sub, Topic(저장소)에 메시지 전달
- Ack를 기다리지 않고 전달 가능
- 생산자(보낸이) 중심

Config Server 에서 Repository 를 통해 변경된 데이터를 가져오면 이전에는 각각의 Microservice 에서 refresh 를 호출하는 방식

Cloud Bus Server 에 연결된 각각의 Microservice 는 외부에서 POST 방식으로 `/busrefresh` 를 Spring Cloud Bus 에 연결된 아무 Microservice 에게 호출하면 호출받은 Microservice 는 Spring Cloud Bus 에게 알려주고 Bus 와 연결된 또 다른 Microservice 에 전달한다.


**Config Server**

라이브러리 추가
- AMQP for Spring Cloud Bus, Actuator

**Users Microservice, Gateway Service**

라이브러리 추가
- AMQP for Spring Cloud Bus

**Config Server, Users Microservice, Gateway Service**

application.yml 수정

```yml
spring:
  application:
    name: config-service
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
...

management:
  endpoint:
    web:
      exposure:
        include: refresh, health, beans, httptrace, busrefresh
```

- RabbitMQ를 웹 브라우저에서 접속할 때의 port는 15672, 시스템에서 amqp 프로토콜을 사용할때는 5672 port를 사용한다

**Remote git repository 에서 yml 수정** 

```yml
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: secret-key-dev-changed#2 # 토큰 키
gateway:
  ip: 127.0.0.1
```

**업데이트 전(127.0.0.1:8000/user-service/actuator/busrefresh x)**

![image](https://user-images.githubusercontent.com/83503188/193823318-b62b2902-369c-4c83-85a9-a140d95a839c.png)

**업데이트 후(127.0.0.1:8000/user-service/actuator/busrefresh o)**

![image](https://user-images.githubusercontent.com/83503188/193823613-35ed7f18-d6e2-480a-b17e-8c4eae76f120.png)

- 토큰이 변경되어 인증을 통과하지 못하는 모습

![image](https://user-images.githubusercontent.com/83503188/193823796-69ac2b01-eb42-495c-a314-ddc99d3bddc1.png)

![image](https://user-images.githubusercontent.com/83503188/193824441-6d3ed1f9-31a1-4c4b-a5aa-ee9f3d9169e3.png)

![image](https://user-images.githubusercontent.com/83503188/193824503-d5eb4ee8-ea70-4d37-8768-a5bf6e12a9c9.png)

user-service 에 `/busrefresh`를 호출했는데 apigateway-service 또한 refresh 되는 모습을 확인할 수 있다. 


