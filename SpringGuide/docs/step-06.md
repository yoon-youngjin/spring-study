# Test Guide

스프링은 다양한 테스트 전략을 제공하고 있다. 대표적으로 Slice Test 라는 것으로 특정 레이어에 대해서 Bean을 최소한으로 등록시켜 테스트 하고자 하는 부분에 최대한 단위 테스트를 지원하다.
다양하게 지원해주는 만큼 테스트 코드를 통일성 있게 관리하는 것이 중요하다.

# 테스트 전략 


| 어노테이션           | 설명                  | 부모 클래스          | Bean         |
| --------------- | ------------------- | --------------- | ------------ |
| @SpringBootTest | 통합 테스트, 전체          | IntegrationTest | Bean 전체      |
| @WebMvcTest     | 단위 테스트, Mvc 테스트     | MockApiTest     | MVC 관련된 Bean |
| @DataJpaTest    | 단위 테스트, Jpa 테스트     | RepositoryTest  | JPA 관련 Bean  |
| None            | 단위 테스트, Service 테스트 | MockTest        | None         |
| None            | POJO, 도메인 테스트       | None            | None         |

# 통합테스트 -> `@SpringBootTest`

## 장점

- 모든 Bean을 올리고 테스트를 진행하기 때문에 쉽게 테스트 진행 가능
- 모든 Bean을 올리고 테스트를 진행하기 때문에 운영환경과 가장 유사하게 테스트 가능
- API를 테스트할 경우 요청부터 응답까지 전체적인 테스트 진행 가능

## 단점

- 모든 Bean을 올리고 테스트를 진행하기 때문에 테스트 시간이 오래 걸림
- 테스트의 단위가 크기 때문에 테스트 실패시 디버깅이 어려움
- 외부 API 콜같은 Rollback 처리가 안되는 테스트 진행을 하기 어려움

