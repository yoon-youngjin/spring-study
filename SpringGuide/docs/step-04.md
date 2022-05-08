# Directory Guide

- 패키지 구조는 크게 계층형, 도메인형 2가지 유형

## 계층형

```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── demo
    │   │               ├── DemoApplication.java
    │   │               ├── config
    │   │               ├── controller
    │   │               ├── dao
    │   │               ├── domain
    │   │               ├── exception
    │   │               └── service
    │   └── resources
    │       └── application.properties
```

- 계층형 구조는 각 계층을 대표하는 디렉토리를 기준으로 코들들이 구성
- 계층형 구조의 장점은 해당 프로젝트에 이해가 상대적으로 낮아도 전체적인 구조를 빠르게 파악할 수 있다.
- 계층형 구조의 단점은 디렉토리에 클래스들이 너무 많이 모이게 된다. 

## 도메인형

```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── demo
    │   │               ├── DemoApplication.java
    │   │               ├── coupon
    │   │               │   ├── controller
    │   │               │   ├── domain
    │   │               │   ├── exception
    │   │               │   ├── repository
    │   │               │   └── service
    │   │               ├── member
    │   │               │   ├── controller
    │   │               │   ├── domain
    │   │               │   ├── exception
    │   │               │   ├── repository
    │   │               │   └── service
    │   │               └── order
    │   │                   ├── controller
    │   │                   ├── domain
    │   │                   ├── exception
    │   │                   ├── repository
    │   │                   └── service
    │   └── resources
    │       └── application.properties

```

- 도메인 디렉토리 기준으로 코드를 구성
- 도메인 구조의 장점은 관련된 코드들이 응집해 있다.
- 도메인 구조의 단점은 프로젝트의 이해도가 낮을 경우 전체적인 구조를 파악하기 어렵다.

## Best Practice

**도메인형이 더 좋은 구조**라고 생각된다. 

### 너무 많은 클래스

계층형 구조의 경우 Controller, Service 등에 너무 많은 클래스들이 밀집하게 된다. 많게는 30 ~ 40의 클래스들이 xxxxController, 
xxxxService 같은 패턴으로 길게 나열되어 프로젝트 전체적인 구조는 상단 디렉토리 몇 개로 빠르게 파악할 수 있지만 그 이후로는 파악하기가 더 힘들어진다.

### 관련 코드의 응집

관련된 코드들이 응집해 있으면 자연스럽게 연관돼 있는 코드 스타일, 변수, 클래스 이름 등을 참고하게 되고 비슷한 코드 스타일과 패턴으로 개발할 수 있게 될 환경이 자연스럽게 마련된다.

계층형 구조일 경우 수신자에 대한 클래스명을 Receiver로 지정했다면, 너무 많은 클래스들로 Receiver에 대한 클래스가 자연스럽게 인식하지 않게 되고 Recipient 같은 클래스 명이나 네이밍을 사용하게 된다. 반면 도메인형은 관련된 코드들이 응집해있기 때문에 자연스럽게 기존 코드를 닮아갈 수 있다. 

또 해당 디렉토리가 컨텍스트를 제공해준다. order라는 디렉토리에 Receiver 클래스가 있는 경우 주문을 배송받는 수취인이라는 컨텍스트를 제공해줄 수 있다. (물론 OrderReceiver라고 더 구체적으로 명명하는 것이 더 좋은 네이밍)

## 도메인형 디렉토리 구조

### 전체적인 구조

```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── spring
    │   │           └── guide
    │   │               ├── ApiApp.java
    │   │               ├── SampleApi.java
    │   │               ├── domain
    │   │               │   ├── coupon
    │   │               │   │   ├── api
    │   │               │   │   ├── application
    │   │               │   │   ├── dao
    │   │               │   │   ├── domain
    │   │               │   │   ├── dto
    │   │               │   │   └── exception
    │   │               │   ├── member
    │   │               │   │   ├── api
    │   │               │   │   ├── application
    │   │               │   │   ├── dao
    │   │               │   │   ├── domain
    │   │               │   │   ├── dto
    │   │               │   │   └── exception
    │   │               │   └── model
    │   │               │       ├── Address.java
    │   │               │       ├── Email.java
    │   │               │       └── Name.java
    │   │               ├── global
    │   │               │   ├── common
    │   │               │   │   ├── request
    │   │               │   │   └── response
    │   │               │   ├── config
    │   │               │   │   ├── SwaggerConfig.java
    │   │               │   │   ├── properties
    │   │               │   │   ├── resttemplate
    │   │               │   │   └── security
    │   │               │   ├── error
    │   │               │   │   ├── ErrorResponse.java
    │   │               │   │   ├── GlobalExceptionHandler.java
    │   │               │   │   └── exception
    │   │               │   └── util
    │   │               └── infra
    │   │                   ├── email
    │   │                   └── sms
    │   │                       ├── AmazonSmsClient.java
    │   │                       ├── SmsClient.java
    │   │                       └── dto
    │   └── resources
    │       ├── application-dev.yml
    │       ├── application-local.yml
    │       ├── application-prod.yml
    │       └── application.yml

```

전체적인 구조는 도메인을 담당하는 domain 디렉토리, 전체적인 설정을 관리하는 global 디렉토리, 외부 인프라스트럭처를 관리하는 infra 디렉토리

### Domain

```├── domain
│   ├── member
│   │   ├── api
│   │   │   └── MemberApi.java
│   │   ├── application
│   │   │   ├── MemberProfileService.java
│   │   │   ├── MemberSearchService.java
│   │   │   ├── MemberSignUpRestService.java
│   │   │   └── MemberSignUpService.java
│   │   ├── dao
│   │   │   ├── MemberFindDao.java
│   │   │   ├── MemberPredicateExecutor.java
│   │   │   ├── MemberRepository.java
│   │   │   ├── MemberSupportRepository.java
│   │   │   └── MemberSupportRepositoryImpl.java
│   │   ├── domain
│   │   │   ├── Member.java
│   │   │   └── ReferralCode.java
│   │   ├── dto
│   │   │   ├── MemberExistenceType.java
│   │   │   ├── MemberProfileUpdate.java
│   │   │   ├── MemberResponse.java
│   │   │   └── SignUpRequest.java
│   │   └── exception
│   │       ├── EmailDuplicateException.java
│   │       ├── EmailNotFoundException.java
│   │       └── MemberNotFoundException.java
│   └── model
│       ├── Address.java
│       ├── Email.java
│       └── Name.java

```

- `model` 디렉토리는 Domain Entity 객체들이 공통적으로 사용할 객체들로 구성, 대표적으로 `Embeddable` 객체, `Enum` 객체가 포함

- `member` 디렉토리
  - `api`: 컨트롤러 클래스들이 존재한다. 외부 rest api로 프로젝트를 구성하는 경우가 많으니 api라고 지칭, Controller 같은 경우에는 ModelAndView를 리턴하는 느낌이 있어서 명시적으로 api라고 하는 게 더 직관적
  - `domain`: 도메인 엔티티에 대한 클래스로 구성, 특정 도메인에만 속하는 `Embeddable`, `Enum` 같은 클래스도 구성된다.
  - `dto`: 주로 Request, Response 객체들로 구성
  - `exception`: 해당 도메인이 발생시키는 Exception으로 구성
  - `application`: 도메인 객체와 외부 영역을 연결해주는 파사드와 같은 역할을 주로 담당하는 클래스로 구성, 대표적으로 데이터베이스 트랜잭션 처리를 진행한다. service 계층과 유사하며 디렉토리 이름을 service로 하지 않은 이유는 service로 했을 경우 xxxxService로 클래스 네임을 해야 한다는 강박관념이 생기기 때문에 application이라고 명명
  - `dao`: repository와 비슷, repository로 하지 않은 이유는 조회 전용 구현체들이 많이 작성되는데 이러한 객체들은 DAO라는 표현이 더 직관적이라고 판단


### global

```
├── global
│   ├── common
│   │   ├── request
│   │   └── response
│   │       └── Existence.java
│   ├── config
│   │   ├── SwaggerConfig.java
│   │   ├── properties
│   │   ├── resttemplate
│   │   │   ├── RestTemplateClientHttpRequestInterceptor.java
│   │   │   ├── RestTemplateConfig.java
│   │   │   └── RestTemplateErrorHandler.java
│   │   └── security
│   ├── error
│   │   ├── ErrorResponse.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── exception
│   │       ├── BusinessException.java
│   │       ├── EntityNotFoundException.java
│   │       ├── ErrorCode.java
│   │       └── InvalidValueException.java
│   └── util
```

- `global` 디렉토리는 프로젝트 전방위적으로 사용되는 객체들로 구성, global로 지정한 이유는 common, util, config 등 프로젝트 전체에서 사용되는 클래스들이 global이라는 디렉토리에 모여 있는 것이 좋다고 생각
  - `common`: 공통적으로 사용되는 Value 객체들로 구성, 페이징 처리를 위한 Request, 공통된 응답을 주는 Response 객체들이 포함
  - `config`: 스프링 각종 설정들로 구성
  - `error`: 예외 핸들링 담당하는 클래스로 구성
  - `util`: 유틸성 클래스들로 구성

### infra 

```
└── infra
    ├── email
    └── sms
        ├── AmazonSmsClient.java
        ├── KtSmsClient.java
        ├── SmsClient.java
        └── dto
            └── SmsRequest.java
```

- `infra` 디렉토리는 인프라스트럭처 관련된 코드들로 구성된다. 인프라스트럭처는 대표적으로 이메일 알림, SMS 알림 등 외부 서비스에 대한 코드들이 존재, 그렇기 때문에 domain, global에 속하지 않는다. global로 볼 수는 있지만 이 계층도 잘 관리해야 하는 대상이기에 별도의 디렉토리로 관리

- 인프라스트럭처는 대체성을 강하게 갔다. SMS 메시지를 보내는 클라이언트를 국내 사용자에게는 KT SMS, 해외 사용자가에게는 Amazon SMS 클라이언트를 이용해서 보낼 수 있다. 

