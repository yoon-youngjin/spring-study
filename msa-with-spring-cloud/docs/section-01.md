# Service Discovery

## Spring Cloud Netflix Eureka

하나의 Microservice 가 세가지 인스턴스에 의해서 확장되어 개발되는 경우 PC가 한대라면 port 를 나눠서 사용하고, PC가 3대라면 같은 port 로 사용할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/192510663-51ac2f66-a293-4f3b-ab6d-c431c2ec4582.png)

모든 Microservice 는 `Spring Cloud Netflix Eureka` 에 등록해야 한다.

`Eureka` 가 해주는 역할을 `Service Discovery` 라고 한다.

> Service Discovery? - Eureka
>
> 서버의 등록과 검색을 해주는 서비스, 외부에서 다른 서비스들이 Microservice 를 검색하기 위해서 사용되는 개념
> 
> 일종의 전화부책 -> 예를 들어 key, value 로 저장된다면 key 에는 서버 이름 value 에는 ip와 같은 위치정보

![image](https://user-images.githubusercontent.com/83503188/192512053-ab3f1250-6d2f-42bc-9c6c-92d2dd01ed09.png)

Microservice 를 사용하고자하는 클라이언트에서 필요한 요청 정보를 Load Balancer 또는 API Gateway 에 요청 정보를 전달하면 해당 정보를 `Service Discovery` 에 전달하여 필요한 서버 정보를 반환해주는 것이다.

따라서 `Service Discovery` 의 역할은 각각의 Microservice 가 어디에 저장되어있으며 요청 정보가 들어왔을 때 요청 정보에 따라서 필요한 서비스의 위치를 알려주는 역할을 한다.


### Eureka Service Discovery - 프로젝트 생성

**EcommerceApplication.java**

```java
@SpringBootApplication
@EnableEurekaServer
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
```

- `@EnableEurekaServer`: Eureka 서버의 자격으로 등록, 해당 어노테이션을 만나면 `Service Discovery` 로써 프로젝트를 기동한다.


**application.yml**

```yml
server:
  port: 8761

spring:
  application:
    name: discoveryservice

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

- `spring.application.name`: Microservice 를 담당하는 springboot 에서 각각의 Microservice 에 고유한 아이디를 할당
- `eureka.client`
  - 유레카 라이브러리가 포함된 상태에서 스프링이 실행되면 기본적으로 유레카 클라이언트 역할로써 어딘가에 등록하는 작업을 시도하게 된다.
  - 그 중에서 `register-with-eureka`, `fetch-registry` 는 기본값이 true 이므로 false 로 변경해야 한다.
  - 해당 설정이 true 라면 유레카 서버 자신을 클라이언트로 등록하는 쓸데없는 작업을 수행하게된다.


**실행화면**

![image](https://user-images.githubusercontent.com/83503188/192515134-aa6b2518-a5de-41d7-b0bd-77eb32ff0da7.png)

- 현재 등록된 Instance(= Microservice)를 확인할 수 있다.


### User Service - 프로젝트 생성


**유레카 서버에 들어갈 클라이언트 서버**

![image](https://user-images.githubusercontent.com/83503188/192516563-21e12057-929b-4219-bfcf-4b460bce351d.png)


**UserServiceApplication**

```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

```

**application.yml**

```java
server:
  port: 9001

spring:
  application:
    name: user-service

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka
```

- `eureka.client.fetch-registry: true`-> Eureka 서버로부터 인스턴스들의 정보를 주기적으로 가져올 것인지를 설정하는 속성이다. true 로 설정하면, 갱신 된 정보를 받겠다는 설정
- `eureka.client.service-url`-> 서버의 위치가 어디인지 항목을 지정하는 부분, 유레카 서버의 위치 폴더를 입력
  - `service-url.defaultZone: http://127.0.0.1:8761/eureka` -> 해당 url에 현재 Microservice 정보를 등록

**유레카 서버 화면**

![image](https://user-images.githubusercontent.com/83503188/192518723-dc8d0809-2da0-4266-ae0c-4d052f2de540.png)

- 인스턴스가 추가된 것을 볼 수 있다.
  - Status 가 UP 상태 
    - UP: 작동 중
    - DOWN: 작동 중지
    
### User Service - 등록

![image](https://user-images.githubusercontent.com/83503188/192520417-a0c1ac8e-03b0-4ff1-b9ce-d8ceca25c528.png)

위와 같은 방법은 서버 자체의 코드를 변경하는 것이 아니다. 따라서 한번 작성된 코드가 다시 빌드, 배포되는 것이 아닌 서버를 기동하는 방법에 의해서 부가적인 파라미터를 전달함으로써 서버 포트를 지정할 수 있는 특징을 가진다.


**유레카 서버 화면**

![image](https://user-images.githubusercontent.com/83503188/192520561-1642607a-131f-413a-b86f-0e81f8ad4560.png)

만약 외부에서 클라리언트 요청이 USER-SERVICE 로 전달된다면 `Discovery Service` 안에서 9001번으로 전달할지 9002번으로 전달할지를 어떤 인스턴스가 살아있는지에 대한 정보값을 gateway 또는 라우팅 서비스에 전달해주면 두가지 서비스에 의해서 분산된 서비스가 실행될 수 있게 되었다.


**maven 명령어로 실행**

```text
mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=9003'
```


**mvn clean**

- 빌드 시 생성된 모든 것들을 삭제 -> target 폴더 삭제

![image](https://user-images.githubusercontent.com/83503188/192532309-9dc3a435-01c0-418f-b532-bf54f6c84f35.png)

**mvn compile package**

- 컴파일 -> target 폴더 생성 

**java -jar -Dserver.port=9004 ./target/user-service-0.0.1-SNAPSHOT.jar**

- 실행 

![image](https://user-images.githubusercontent.com/83503188/192533522-1bf1944d-e9a4-4b00-8eb3-23ec465411dd.png)

매번 인스턴스를 달리해서 기동할 때마다 포트번호를 지정한다는 것은 불편하므로 스프링에서 지원하는 random port 를 이용하자.

**application.yml**

```yml
server:
  port: 0

...
```


- `server.port = 0`: random port 를 사용하겠다. 


![image](https://user-images.githubusercontent.com/83503188/192534581-e910f4b5-050e-4f7e-851f-52ca229cf187.png)

![image](https://user-images.githubusercontent.com/83503188/192534846-e9562035-8e35-4a51-b788-f9a33ab92863.png)

**mvn spring-boot:run**
- 실행
- 이제 이전과 같이 파라미터를 통해 port 를 넘겨주지 않아도 랜덤으로 port 가 지정됨을 볼 수 있다.

![image](https://user-images.githubusercontent.com/83503188/192535227-c2fd4e8a-0a4d-46b0-a6ab-6baf0e5b2e27.png)


유레카 서버에서 확인해보면 하나의 인스턴스밖에 확인이 되지않는다. 

![image](https://user-images.githubusercontent.com/83503188/192535364-4e6f42da-5f1a-4f18-8259-533cb91a8d5b.png)

클라이언트에 추가적인 id값을 부여하면 해결된다.

```java
...

eureka:
  instance:
    instance-id:  ${spring.cloud.client.hostname}:${spring.application.instance_id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka
```

![image](https://user-images.githubusercontent.com/83503188/192536243-062d68e5-9fc9-45c7-b602-0b19a7e75ee9.png)

계속 스케일링 작업을 하기 위해서 user-service 를 실행할 때 마다 자동으로 port 가 부여되고 사용자는 인식할 수 없는 상태에서 여러개의 인스턴스가 만들어지고 각각의 인스턴스들은 유레카 서비스(= Discovery Service)에 등록되며, 라우팅, 게이트웨이에 의해서 필요한 작업을 호출할 수 있게된다.

간단하게 로드밸런싱을 구현할 수 있음이 Spring Cloud 의 큰 장점이다. 







