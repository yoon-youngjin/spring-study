# Microservice 모니터링

### Micrometer + Monitoring

Mircrometer

자바 기반의 어플리케이션에 각종 지표를 수집하는 용도로 사용
모니터링: 현재 CPU 사용량, 메소드의 사용량, 네트워크 트래픽이 발생했고 어느정도 사용되고 있는지, 사용자의 요청이 몇번 호출, ... 수치화된 좌표를 도식화해주는 기능
분산된 여러개의 독립적인 소프트웨어로 구성되어있기 때문에 각종 서버들이 잘 작동 중인지, 문제가 생긴 곳이 있는지, 병목 현상이 있는지, ... 파악해서 바로 자원을 재할당하는 기능이 필요하다.

Micrometer
- https://micrometer.io/
- JVM 기반의 애플리케이션 Metrics 제공
- Spring Framework 5, spring boot 2부터 Spring의 Metrics 처리
- Prometheus등의 다양한 모니터링 시스템을 지원

Timer
- 짧은 지연 시간, 이벤트의 사용 빈도를 측정
- 시계열로 이벤트의 시간, 호출 빈도 등을 제공
- @Timed 제공

Microservice수정

라이브러리 추가
-micrometer-registry-prometheus

**application.yml**

```yml

...
management:
  endpoints:
    web:
      exposure:
        include: refresh, health, beans, busrefresh, info, metrics, prometheus
```
- metrics, prometheus 추가

**UserController**

```java
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;



    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        return String.format("It's Working in User Service"
                        + ", port(local.server.port)=" + env.getProperty("local.server.port")
                        + ", port(server.port)=" + env.getProperty("server.port")
                        + ", gateway ip=" + env.getProperty("gateway.ip")
                        + ", token secret=" + env.getProperty("token.secret")
                        + ", token expiration time=" + env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome() {
//        return greeting.getMessage();
        return env.getProperty("greeting.message");
    }
...
}
```

- `@Timed` 추가
- status(), welcome()을 사용자가 호출하게되면 호출된 정보가 Micrometer에서 기록되고 기록된 정보는 추후에 연결될 prometheus에서 사용할 수 있다.

welcome(), status() 호출

metrics
![image](https://user-images.githubusercontent.com/83503188/197508630-b1fe5cd7-1662-4d54-862d-3cb46c265fb3.png)

prometheus
![image](https://user-images.githubusercontent.com/83503188/197508931-34caddf9-5235-4839-9425-9c91389fc0be.png)
![image](https://user-images.githubusercontent.com/83503188/197508832-17260ad2-716a-4514-bc97-144a41f86716.png)

Prometheus와 Grafana 개요


Prometheus
- Metrics를 수집하고 모니터링 및 알람에 사용되는 오픈소스 애플리케이션
- 2016년부터 CNCF에서 관리되는 2버째 공식 프로젝트
- Level DB -> Time Seres Database(TSDB)

- Pull 방식의 구조와 다양한 Metrics Exporter 제공
- 시계열 DB에 Metrics 저장 -> 조회 가능(Query)

프로메테우스에서 스프링 클라우드가 수집한 정보를 가지고와서 시계열 DB화하여 저장하면 저장된 정보를 가지고 Grafana가 시각화한다.

Grafana
- 데이터 시각화, 모니터링 및 분석을 위한 오픈소스 애플리케이션
- 시계열 데이터를 시각화하기 위한 대시보드 제공

![image](https://user-images.githubusercontent.com/83503188/197510043-01a60417-f9ab-426d-98da-0438e213f72b.png)

prometheus 설치 후 prometheus.yml 파일 수정

```yml
...
  - job_name: "user-service"
    scrape_interval: 15s
    metrics_path: "/user-service/actuator/prometheus"
    static_configs:
      - targets: ["127.0.0.1:8000"]
  - job_name: "apigateway-service"
    scrape_interval: 15s
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["127.0.0.1:8000"]
  - job_name: "order-service"
    scrape_interval: 15s
    metrics_path: "order-service/actuator/prometheus"
    static_configs:
      - targets: ["127.0.0.1:8000"]


```

어디에서 정보를 수집해올것인지 타겟을 지정한다.

prometheus 실행 - Dashboard

![image](https://user-images.githubusercontent.com/83503188/197510986-18490a8f-2dcd-4604-a1dd-b868b7d700f2.png)

![image](https://user-images.githubusercontent.com/83503188/197511204-d0a549fd-d2a4-4935-9d6f-9b8a912b1e2d.png)
- http_server_request_seconds_count 지표 검색

Grafana 다운 및 실행

- http://127.0.0.1:3000
- ID: admin, PW: admin

Prometheus와 Grafana의 연동과 DashBoard 구성

Grafana Dashboard
- JVM(Micrometer)
- Prometheus
- Spring Cloud Gateway

Data Source 추가 -> prometheus

![image](https://user-images.githubusercontent.com/83503188/197514744-a83062a3-5b16-4910-846d-b3f43a8dc12c.png)

Dashboard import - JVM(micrometer)

![image](https://user-images.githubusercontent.com/83503188/197515305-b531cc15-820c-4caf-add1-377945e7db48.png)
![image](https://user-images.githubusercontent.com/83503188/197515380-0192eed0-bd1f-4ca0-b146-4e2f3360091c.png)

Dashboard import - Prometheus 2.0 overview

![image](https://user-images.githubusercontent.com/83503188/197515649-f8954741-f5aa-48ba-8857-73361e7ea08b.png)

Dashboard import - Spring Cloud Gateway

현재 서버의 값이 다르기 때문에 정확한 데이터가 나오지 않는 것

![image](https://user-images.githubusercontent.com/83503188/197516603-2dda90e8-d8e6-42a1-9377-ac475c461fd8.png)
