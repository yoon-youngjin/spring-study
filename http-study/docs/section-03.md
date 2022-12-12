# HTTP 기본

## 모든 것이 HTTP

HTTP(HyperText Transfer Protocol)는 처음에 HTML(Hyper Text Markup Language)을 전송하기 위한 프로토콜로 등장하였다.

하지만 현재는 모든 것을 HTTP 프로토콜에 담아서 전송한다.

- HTML, TEXT
- IMAGE, 음성, 영상, 파일
- JSON, XML (API)
- 거의 모든 형태의 데이터 전송 가능
- 서버간에 데이터를 주고 받을 때도 대부분 HTTP 사용

**HTTP 역사**
- HTTP/0.9: 1991년, GET 메서드만 지원, HTTP 헤더X
- HTTP/1.0: 1996년, 메서드, 헤더 추가
- **HTTP/1.1: 1997년, 가장 많이 사용, 우리에게 가장 중요한 버전**
  - RFC2608 (1997) -> RFC2616 (1999) -> RFC7230~7325(2014)
- HTTP/2: 2015년, 성능 개선
- HTTP/3: 진행중, TCP 대신에 UDP 사용, 성능 개선

**기반 프로토콜**
- TCP: HTTP/1.1, HTTP/2
- UDP: HTTP/3
- 현재 HTTP/1.1 주로 사용
  - HTTP/2, HTTP/3 도 점점 증가

![image](https://user-images.githubusercontent.com/83503188/207057553-a686e32b-843c-4052-a37e-9f9e0f9bbbec.png)
- h3 -> HTTP/3

**HTTP 특징**
- 클라이언트 서버 구조
- 무상태 프로토콜(stateless), 비 연결성
- HTTP 메시지
- 단순함, 확장 가능

## 클라이언트 서버 구조 

Request Response 구조

- 클라이언트는 서버에 요청을 보내고, 응답을 대기
- 서버가 요청에 대한 결과를 만들어서 응답

![image](https://user-images.githubusercontent.com/83503188/207058001-41558e19-7f2d-4f82-b593-16e3c1b8c0c4.png)

과거에는 클라이언트와 서버를 분리하는 개념이 명확하지 않았다.
클라이언트와 서버를 개념적으로 분리함으로써 비즈니스 로직, 데이터는 서버로 UI, 사용성은 클라이언트로 역할을 분리함으로써 각각 성장할 수 있는 구조로 변경되었다.

## Stateful, Stateless

**무상태 프로토콜(stateless)를 지향**
- 서버가 클라이언트의 상태를 보존X
- 장점: 서버 확장성 높음(스케일 아웃)
- 단점: 클라이언트가 추가 데이터를 전송해야한다. 

### Stateful, Stateless 차이 - Stateful

Stateful 한 상태는 서버가 클라이언트의 이전 상태(Context)를 보존

**상태유지 - Stateful**

```text
- 고객: 이 노트북 얼마인가요?
- 점원: 100만원 입니다.
- 고객: 2개 구매하겠습니다.
- 점원: 200만원 입니다. 신용카드, 현금 중에 어떤 걸로 구매하시겠어요?
- 고객: 신용카드로 구매하겠습니다.
- 점원: 200만원 결제 완료되었습니다.
```


**상태유지 - Stateful, 점원이 중간에 바뀌면?**
```text
- 고객: 이 노트북 얼마인가요?
- 점원A: 100만원 입니다.
- 고객: 2개 구매하겠습니다.
- 점원B: ? 무엇을 2개 구매하시겠어요?
- 고객: 신용카드로 구매하겠습니다.
- 점원C: ? 무슨 제품을 몇 개 신용카드로 구매하시겠어요?
```


**상태유지 - Stateful, 정리**
```text
- 고객: 이 노트북 얼마인가요?
- 점원: 100만원 입니다. (노트북 상태 유지)
- 고객: 2개 구매하겠습니다.
- 점원: 200만원 입니다. 신용카드, 현금 중에 어떤 걸로 구매하시겠어요? (노트북, 2개 상태 유지)
- 고객: 신용카드로 구매하겠습니다.
- 점원: 200만원 결제 완료되었습니다. (노트북, 2개, 신용카드 상태 유지)
```

### Stateful, Stateless 차이 - Stateless

Stateless 한 상태는 서버가 클라이언트의 이전 상태(Context)를 보존X

**무상태 - Stateless**
```text
- 고객: 이 **노트북** 얼마인가요?
- 점원: 100만원 입니다.
- 고객: **노트북 2개** 구매하겠습니다.
- 점원: 노트북 2개는 200만원 입니다. 신용카드, 현금 중에 어떤 걸로 구매하시겠어요?
- 고객: **노트북 2개를 신용카드**로 구매하겠습니다.
- 점원: 200만원 결제 완료되었습니다.
```

**무상태 - Stateless, 점원이 중간에 바뀌면?**
```text
- 고객: 이 **노트북** 얼마인가요?
- 점원A: 100만원 입니다.
- 고객: **노트북 2개** 구매하겠습니다.
- 점원B: 노트북 2개는 200만원 입니다. 신용카드, 현금 중에 어떤 걸로 구매하시겠어요?
- 고객: **노트북 2개를 신용카드**로 구매하겠습니다.
- 점원C: 200만원 결제 완료되었습니다.
```

Stateful 상태에서는 점원이 바뀌는 경우 장애로 이어지는 반면에 Stateless 상태는 정상적인 서비스를 이어갈 수 있다.

따라서 Stateless 로 설계하면 큰 확장성을 가져갈 수 있다.

### Stateful, Stateless 차이 - 정리

- 상태유지: 중간에 다른 점원으러 바뀌면 안된다.(중간에 다른 점으로 바뀔 때 상태 정보를 다른 점원에게 미리 알려줘야 한다.)
- 무상태: 중간에 다른 점원으로 바뀌어도 된다.
  - 갑자기 고객이 증가해도 점원을 대거 투입할 수 있다.
  - 갑자기 클라이언트 요청이 증가해도 서버를 대거로 투입할 수 있다.
- 무상태는 응답 서버를 쉽게 바꿀 수 있다. -> **무한한 서버 증설 가능**

**상태유지 - Stateful**

항상 같은 서버가 유지되어야 한다.

![image](https://user-images.githubusercontent.com/83503188/207063137-a37faaed-8858-42d4-8602-99f594c915ac.png)
- 처음 클라이언트 요청을 받은 서버1만 해당 클라이언트에게 응답을 할 수 있다.
- 서버 늘리기가 힘들다. 

![image](https://user-images.githubusercontent.com/83503188/207063427-49af0376-22e2-4364-8de3-9b211c05440a.png)
- 처음 요청을 받은 서버1이 장애가 생기면 클라이언트는 로직을 처음부터 다시 시작해야한다.

**무상태 - Stateless**

아무 서버나 호출해도 된다.

![image](https://user-images.githubusercontent.com/83503188/207063839-d37c0f1a-3f2e-4109-83b7-dd5f7c512ab3.png)
- 클라이언트는 처음부터 정보를 포함하여 서버에 요청을 보내게된다. 

![image](https://user-images.githubusercontent.com/83503188/207064102-e1ede014-7dd3-43dc-ae91-ade4a75ca570.png)
- 중간에 서버에 문제가 나더라도 다른 서버에서 처ㅂ리할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/207068162-7f94c6ad-9e22-4bdc-b271-5c2efd45090f.png)
- 상태를 유지하지 않기 때문에 스케일 아웃(scale out), 수평 확장에 유리하다.

**Stateless 실무 한계**
- 모든 것을 무상태로 설계 할 수 있는 경우도 있고 없는 경우도 있다.
- 무상태
  - 예) 로그인이 필요 없는 단순한 서비스 소개 화면
- 상태유지
  - 예) 로그인
- 로그인한 사용자의 경우 로그인 했다는 상태를 서버에 유지
- 일반적으로 브라우저 쿠키와 서버 세션등을 사용해서 상태 유지
- 상태 유지는 최소한만 사용

## 비 연결성(connectionless)

- HTTP 는 기본이 연결을 유지하지 않는 모델
- 일반적으로 초 단위의 이하의 빠른 속도로 응답
- 1시간 동안 수천명이 서비스를 사용해도 실제 서버에서 동시에 처리하는 요청은 수십개 이하로 매우 작음
  - 예) 웹 브라우저에서 계속 연속해서 검색 버튼을 누르지는 않는다.
- 서버 자원을 매우 효율적으로 사용할 수 있음

**연결을 유지하는 모델**

![image](https://user-images.githubusercontent.com/83503188/207066050-cbbfdab6-bf76-4dd5-9162-31e8425d7613.png)
- 클라이언트3이 요청을 보내는 시점에도 이전에 연결한 클라이언트1, 클라이언트2와 연결을 유지
- 서버에서는 클라이언트1, 클라이언트2의 연결을 유지하기 위한 자원이 소모된다.

**연결을 유지하지 않는 모델**

![image](https://user-images.githubusercontent.com/83503188/207066531-d53b2e0d-24c3-486c-8e6a-bde8987dbac4.png)
- 클라이언트1과 서버에 요청을 보내고 응답을 받은 뒤에 연결을 끊는다.

![image](https://user-images.githubusercontent.com/83503188/207066616-350c0b5a-6dff-44ce-bc07-36454323a32a.png)
- 서버 입장에서는 자원을 주고 받을 때만 연결을 유지하기 때문에 연결을 유지하기 위한 자원이 소모되지 않는다.

**비 연결성 - 한계와 극복**

- TCP/IP 연결을 새로 맺어야 함 - 3 way handshake 시간 추가
- 웹 브라우저로 사이트를 요청하면 HTML 뿐만 아니라 자바스크립트, css, 추가 이미지, ... 수 많은 자원이 함께 다운로드
- 지금은 HTTP 지속 연결(Persistent Connections)로 문제 해결
- HTTP/2, HTTP/3에서 더 많은 최적화

**HTTP 초기 - 연결, 종료 낭비**

![image](https://user-images.githubusercontent.com/83503188/207068314-7e496c69-7232-48d3-bb95-ebe96edd051b.png)

**HTTP 지속 연결(Persistent Connections)**

![image](https://user-images.githubusercontent.com/83503188/207068427-ba0f27c9-ef87-42d6-b62a-b59db0b51ff3.png)
- 요청/응답 후에 연결을 종료하는 것이 아닌 일정 기간동안 연결을 유지하게된다. 

## HTTP 메시지 

HTTP 요청 메시지와 HTTP 응답 메시지는 구조가 다르다.

![image](https://user-images.githubusercontent.com/83503188/207069451-a05d0048-8bde-411b-9403-647f13b55a24.png)
- 요청 메시지와 응답 메시지는 start-line 만 다르다.

**시작 라인 - 요청 메시지**

![image](https://user-images.githubusercontent.com/83503188/207070081-646c5752-b455-45bc-bfce-db5b2b703d7d.png)
- start-line = request-line / status-line
- request-line = method SP(공백) request-target SP HTTP -version CRLF(엔터)
  - HTTP 메서드 (`GET`)
  - 요청 대상(`/search?q=hello&hl=ko`)
  - HTTP Version(`HTTP/1.1`) 

**시작 라인 - HTTP 메서드**

- 종류: GET, POST, PUT, DELETE, ...
- 서버가 수행해야 할 동작 지정
  - GET: 리소스 조회
  - POST: 요청 내역 처리 

**시작 라인 - 요청 대상**

- `absolute-path[?query](절대경로[?쿼리])`
- 절대경로="/"로 시작하는 경로

**시작 라인 - 응답 메시지**

![image](https://user-images.githubusercontent.com/83503188/207071148-56077edd-5944-45bf-9a59-f24afd94d771.png)
- start-line = request-line / status-line
- status-line = HTTP-version SP status-code SP reason-phrase CRLF
  - HTTP 버전(`HTTP/1.1`)
  - HTTP 상태 코드(`200`): 요청 성공, 실패를 나타냄
    - 200: 성공
    - 400: 클라이언트 요청 오류
    - 500: 서버 내부 오류
  - 이유 문구(`OK`): 사람이 이해할 수 있는 짧은 상태 코드 설명 글

**HTTP 헤더**

![image](https://user-images.githubusercontent.com/83503188/207071687-4336e1b4-3539-4bad-8c52-bbfb1162429b.png)
- header-field = field-name ":" OWS field-value OWS (OWS:띄어쓰기 허용)
- field-name 은 대소문자 구문 없음

**HTTP 헤더 - 용도**
- HTTP 전송에 필요한 모든 부가정보
  - 예) 메시지 바디의 내용, 메시지 바디의 크기, 압축, 인증, 요청 클라이언트(브라우저) 정보, 서버 애플리케이션 정보, 캐시 관리 정보, ...
- 표준 헤더가 너무 많음
  - https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
- 필요시 임의의 헤더 추가 가능
  - `helloworld: hihi`

**HTTP 메시지 바디 - 용도**

![image](https://user-images.githubusercontent.com/83503188/207072133-81d2a276-d856-4ab6-af45-4a170a0dd581.png)
- 실제 전송할 데이터
- HTML 문서, 이미지, 영상, JSON 등등 byte 로 표현할 수 있는 모든 데이터 전송 가능
