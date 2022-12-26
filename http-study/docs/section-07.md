# HTTP 헤더1 - 일반 헤더 

## HTTP 헤더 개요

![image](https://user-images.githubusercontent.com/83503188/209552559-488b971e-5903-4ee3-833e-76c49f35ba34.png)

- header-field =field-name ":" OWS field-value OWS (OWS: 띄어쓰기 허용)
- field-name은 대소문자 구문 없음

**HTTP 헤더 용도** 

- HTTP 전송에 필요한 모든 부가정보
  - 예) 메시지 바디의 내용, 메시지 바디의 크기, 압축, 인증, 요청 클라이언트, 서버 정보, 캐시 관리 정보, ...
- 표준 헤더가 너무 많음
  - https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
- 필요시 임의의 헤더 추가 가능
  - `helloword: hihi`

**message body - RFC7230(최신)**

![image](https://user-images.githubusercontent.com/83503188/209552967-9cd62d02-5830-4235-851f-3f63b4e8028e.png)

- 메시지 본문(message body)을 통해 표현 데이터 전달
- 메시지 본문 = 페이로드(payload)
- 표현(표현 헤더 + 표현 데이터)은 요청이나 응답에서 전달할 실제 데이터
- 표현 헤더는 표현 데이터를 해석할 수 있는 정보 제공
  - 데이터 유형(html, json), 데이터 길이, 압축 정보, ...
- 참고: 표현 헤더는 표현 메타데이터와 페이로드 메시지를 구분해야 하지만, 여기서는 생략

## 표현

![image](https://user-images.githubusercontent.com/83503188/209553335-696e676b-c6e3-44d8-9fd6-a6422082343a.png)
- Content-Type: 표현 데이터의 형식
- Content-Encoding: 표현 데이터의 압축 방식
- Content-Language: 표현 데이터의 자연 언어
- Content-Length: 표현 데이터의 길이 
- 표현 헤더는 전송, 응답 둘다 사용

### Content-type


**표현 데이터의 형식 설명**

![image](https://user-images.githubusercontent.com/83503188/209553683-bf7feb85-5912-4d3e-9d62-58d06870d4ce.png)

- 미디어 타입, 문자 인코딩
- 예)
  - text/html;charset=UTF-8
  - application/json
  - application/x-www-form-urlencoded
  - image/png

### Content-Encoding

**표현 데이터 인코딩**

![image](https://user-images.githubusercontent.com/83503188/209553771-61682023-731c-497c-8baa-1b7ff735eb38.png)
- 표현 데이터를 압축하기 위해 사용
- 데이터를 전달하는 곳에서 압축 후 인코딩 헤더 추가 
- 데이터를 읽는 쪽에서 인코딩 헤더의 정보로 압축 해제
- 예)
  - gzip
  - deflate
  - identity: 압축X

### Content-Language

**표현 데이터의 자연 언어**

![image](https://user-images.githubusercontent.com/83503188/209553893-a153d408-2c34-4a04-9626-100dcfa20460.png)
- 표현 데이터의 자연 언어를 표현
- 예)
  - ko
  - en
  - en-US

### Content-Length

**표현 데이터의 길이**

![image](https://user-images.githubusercontent.com/83503188/209553932-ea3c9f5d-5e65-4323-ac84-ed21e6daaf7b.png)
- 바이트 단위
- Transfer-Encoding(전송 코딩)을 사용하면 Content-Length를 사용하면 안됨

## 콘텐츠 협상(콘텐츠 니고시에이션)

**클라이언트가 선호하는 표현 요청**
- Accept: 클라이언트가 선호하는 미디어 타입 전달
- Accept-Charset: 클라이언트가 선호하는 문자 인코딩
- Accept-Encoding: 클라이언트가 선호나는 압축 인코딩
- Accept-Language: 클라이언트가 선호하는 자연 언어
- 협상 헤더는 요청시에만 사용 

**Accept-Language 적용 전**

![image](https://user-images.githubusercontent.com/83503188/209554338-264edeae-85d0-4aa2-a190-e25cdba21a15.png)
- 한국어 브라우저를 사용하는데, 클라이언트가 서버로 요청을 보낼때 Accept-Language 헤더를 적용하지 않으면 서버의 기본 언어(en)로 응답

**Accept-Language 적용 후**

![image](https://user-images.githubusercontent.com/83503188/209554360-e4fdc5f0-57ed-477b-86c1-464c261b4849.png)
- 클라이언트가 서버로 요청을 보낼때 Accept-Language 를 지정하면 해당 언어를 지원하는 경우 해당 언어로 응답


**Accept-Language 복잡한 예시**

![image](https://user-images.githubusercontent.com/83503188/209554662-c85984a0-36d0-467c-abeb-0ae938acefe4.png)
- 클라이언트가 서버로 요청을 보낼때 Accept-Language 로 지정한 언어를 지원하지 않는 서버의 경우에는 기본 언어(de)로 응답
- 우선순위를 지정하기 위해서?

### 협상과 우선순위1 - Quality Values(q)

![image](https://user-images.githubusercontent.com/83503188/209554860-75c153f9-b14e-4b0d-8b62-6b300b5b0e01.png)

- Quality Values(q) 값 사용
- 0~1, 클수록 높은 우선순위
- 생략하면 1
  - Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
    1. ko-KR;q=1 (q생략)
    2. ko;q=0.9
    3. en-US;q=0.8
    4. en:q=0.7

**Accept-Language 복잡한 예시**

![image](https://user-images.githubusercontent.com/83503188/209555027-c32fb2dc-d5d1-4913-bdd7-d57b47c8f81c.png)
- 우선순위를 파악하여 서버에서 지원하는 경우 응답

### 협상과 우선순위2 - Quality Values(q)

![image](https://user-images.githubusercontent.com/83503188/209555072-19bc107f-11d2-4ac0-8f08-b9346ad227b6.png)

- 구체적인 것이 우선한다.
- Accept: text/*, text/plain, text/plain;format=flowed, */*
  1. text/plain;format=flowed
  2. text/plain
  3. text/*
  4. */*


### 협상과 우선순위3 - Quality Values(q)

- 구체적인 것을 기준으로 미디어 타입을 맞춘다.
- Accept: text/*;q=0.3, text/html;q=0.7, text/html;level=1, text/html;level=2;q=0.4, */*;q=0.5

![image](https://user-images.githubusercontent.com/83503188/209555146-e0e9ef25-d2cf-4896-affe-0afca2b66af6.png)

## 전송 방식

- Transfer-Encoding
- Range, Content-Range

**전송 방식 설명**
- 단순 전송
- 압축 전송
- 분할 전송
- 범위 전송 

### 단순 전송

**Content-Length**

![image](https://user-images.githubusercontent.com/83503188/209555402-5555a9e5-632a-4d97-8325-a9880de21acd.png)
- message body 에 대한 Content-Length 를 지정하는 방식
- Content 에 대한 길이를 알 수 있는 경우에 사용

### 압축 전송

**Content-Encoding**

![image](https://user-images.githubusercontent.com/83503188/209555501-7b6d8fc6-f952-48f0-aab2-eb88a3c9afa0.png)
- 서버에서 gzip 과 같은 압축 시스템으로 Content 압축하여 전송하는 방식
- 단, Content-Encoding 헤더를 추가

### 분할 전송

**Transfer-Encoding**

![image](https://user-images.githubusercontent.com/83503188/209555564-7d57373e-05ba-494e-acf4-5915ca604a18.png)
- 주로 Content 가 큰 경우 사용  
- Content 를 쪼개서 보내는 방식
- Content-Length 를 넣으면 안된다. 

### 범위 전송

**Range, Content-Range**

![image](https://user-images.githubusercontent.com/83503188/209555743-16483a67-d03e-473c-81c8-e347dfecb088.png)
- 범위를 지정하여 요청하는 방식

## 일반 정보

**정보성 헤더**

- From: 유저 에이전트의 이메일 정보
- Referer: 이전 웹 페이지 주소
- User-Agent: 유저 에이전트 애플리케이션 정보
- Server: 요청을 처리하는 오리진 서버의 소프트웨어 정보
- Date: 메시지가 생성된 날짜

### From

**유저 에이전트의 이메일 정보**

- 일반적으로 잘 사용되지 않음
- 검색 엔진 같은 곳에서 주로 사용
- 요청에서 사용

### Referer

**이전 웹 페이지 주소**
- 현재 요청된 페이지의 이전 웹 페이지 주소
- A -> B로 이동하는 경우 B를 요청할 때 Referer: A 를 포함해서 요청
- Referer 를 사용해서 **유입 경로 분석 가능**
- 요청에서 사용
- 참고: referer 는 단어 referrer 의 오타

### User-Agent

**유저 에이전트 애플리케이션 정보**

- user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/ 537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36
- 클라이언트의 애플리케이션 정보(웹 브라우저 정보, 등등)
- 통계 정보
- 어떤 종류의 브라우저에서 장애가 발생하는지 파악 가능
- 요청에서 사용

### Server

**요청을 처리하는 ORIGIN 서버의 소프트웨어 정보**

> Origin 서버?
> 
> 사용자가 요청을 보내면 중간에 여러번의 Proxy 서버를 거치게 되는데, Proxy 서버가 아닌, 나의 요청을 실제로 처리해주는 서버가 Origin 서버

- Server: Apache/2.2.22 (Debian)
- Server: nginx
- 응답에서 사용

### Date

**메시지가 발생한 날짜와 시간**

- Date: Tue, 15 Nov 1994 08:12:31 GMT
- 응답에서 사용

## 특별한 정보

- Host: 요청한 호스트 정보(도메인)
- Location: 페이지 리다이렉션
- Allow: 허용 가능한 HTTP 메서드
- Retry-After: 유저 에이전트가 다음 요청을 하기까지 기다려야 하는 시간

### Host

**요청한 호스트 정보(도메인)**

![image](https://user-images.githubusercontent.com/83503188/209556437-e7b4bf34-f1f1-4b5d-bfb4-62a914b24ef5.png)
- 요청에서 사용
- 필수
- 하나의 서버가 여러 도메인을 처리해야 할 때
- 하나의 IP 주소에 여러 도메인이 적용되어 있을 때

가상호스트를 통해 여러 도메인을 한번에 처리할 수 있는 서버 실제 애플리케이션이 여러개 구동될 수 있다.

![image](https://user-images.githubusercontent.com/83503188/209556524-5bb001b9-d791-4582-aa2f-f6c1e926def1.png)

![image](https://user-images.githubusercontent.com/83503188/209556550-0e2d4022-d10d-49c4-8c2a-8344a5e0a1f0.png)

### Location

**페이지 리다이렉션**
- 웹 브라우저는 3xx 응답의 결과에 Location 헤더가 있으면, Location 위치로 자동 이동(리다이렉트)
- 201 (Created): Location 값은 요청에 의해 생성된 리소스 URI
- 3xx (Redirection): Location 값은 요청을 자동으로 리디렉션하기 위한 대상 리소스를 가리킴

### Allow

**허용 가능한 HTTP 메서드**

- 405 (Method Not Allowed) 에서 응답에 포함해야함
- Allow: GET, HEAD, PUT
- 잘 구현되어있지 않음

### Retry-After

**유저 에이전트가 다음 요청을 하기까지 기다려야 하는 시간**

- 503 (Service Unavailable): 서비스가 언제까지 불능인지 알려줄 수 있음
- Retry-After: Fri, 31 Dec 1999 23:59:59 GMT (날짜 표기)
- Retry-After: 120 (초단위 표기)

## 인증

- Authorization: 클라이언트 인증 정보를 서버에 전달
- WWW-Authenticate: 리소스 접근시 필요한 인증 방법 정의

### Authorization

**클라이언트 인증 정보를 서버에 전달**

- Authorization: Basic xxxxxxxxxxxxxxxx
- Auth: Bearer xxxxxx

### WWW-Authenticate

**리소스 접근시 필요한 인증 방법 정의**

- 리소스 접근시 필요한 인증 방법 정의
- 401 Unauthorized 응답과 함께 사용
- WWW-Authenticate: Newauth realm="apps", type=1, title="Login to \"apps\"", Basic realm="simple"

## 쿠키

- Set-Cookie: 서버에서 클라이언트로 쿠키 전달(응답)
- Cookie: 클라이언트가 서버에서 받은 쿠키를 저장하고, HTTP 요청시 서버로 전달

### 쿠키 미사용

**처음 welcome 페이지 접근**

![image](https://user-images.githubusercontent.com/83503188/209557241-b882ca77-b323-488c-a021-e8377cf21f6a.png)

**로그인**

![image](https://user-images.githubusercontent.com/83503188/209557270-41d5a1dd-a03d-4840-b3a7-da586e10d3d5.png)

**로그인 이후 welcome 페이지 접근**

![image](https://user-images.githubusercontent.com/83503188/209557301-5f1c6185-f063-4a11-82d6-d6c6d238cb9e.png)

로그인을 하고 다시 welcome 페이지에 접근해도 서버에서는 사용자의 상태를 기억하지 않기 때문에(Stateless) 사용자를 구분할 수 있는 방법이 없다. 

### Stateless

- HTTP는 무상태(Stateless) 프로토콜이다.
- 클라이언트와 서버가 요청과 응답을 주고 받으면 연결이 끊어진다.
- 클라이언트가 다시 요청하면 서버는 이전 요청을 기억하지 못한다.
- 클라이언트와 서버는 서로 상태를 유지하지 않는다.

### 쿠키 미사용 - 대안

**모든 요청에 사용자 정보 포함**

![image](https://user-images.githubusercontent.com/83503188/209557409-44c289a6-ba87-44aa-9f9a-8be64b7cc69d.png)

**모든 요청과 링크에 사용자 정보 포함?**

![image](https://user-images.githubusercontent.com/83503188/209557441-53ecd0e1-6439-430e-af7a-d7bc98340b31.png)

**모든 요청에 정보를 넘기는 문제**
- 모든 요청에 사용자 정보가 포함되도록 개발 해야함
- 브라우저를 완전히 종료하고 다시 열면?

### 쿠키 사용

**로그인**

![image](https://user-images.githubusercontent.com/83503188/209557536-94bd4731-5323-466b-9461-8f38560a35d6.png)
- 서버에서는 로그인이 성공되면 Session key 를 생성하여 서버의 데이터베이스에 저장해두고
- `Set-Cookie: sessionId = '생성한 session key'`  이와 같이 헤더에 담아서 반환한다.
- 클라이언트는 요청마다 전달받은 Cookie 를 헤더에 담고, 서버에서는 요청으로 받은 Cookie 와 서버의 데이터베이스를 비교하여 사용자를 판별한다.

**로그인 이후 welcome 페이지 접근**

![image](https://user-images.githubusercontent.com/83503188/209557573-754e09ba-eed9-4d08-a916-484f377a73fd.png)
- 자동으로 웹 브라우저는 서버에 요청을 보낼때 마다 쿠키 저장소에서 쿠키 값을 꺼내서 Cookie 헤더의 value 로 지정한다.
- 서버에서는 Cookie 헤더의 값을 읽어서 사용자를 구분한다.

**모든 요청에 쿠키 정보 자동 포함**

![image](https://user-images.githubusercontent.com/83503188/209557616-ace2114d-d55c-4c99-a098-c4c02929c432.png)

**쿠키**

- 예) set-cookie: sessionId=abcde1234; expires=Sat, 26-Dec-2020 00:00:00 GMT; path=/; domain=.google.com; Secure
- 사용처
  - 사용자 로그인 세션 관리
  - 광고 정보 트래킹
- 쿠키 정보는 항상 서버에 전송됨
  - 네트워크 트래픽 추가 유발
  - 최소한의 정보만 사용(세션 id, 인증 토큰)
  - 서버에 전송하지 않고, 웹 브라우저 내부에 데이터를 저장하고 싶으면 웹 스토리지 (localStorage, sessionStorage) 참고
- 주의!
  - 보안에 민감한 데이터는 저장하면 안됨(주민번호, 신용카드 번호 등등)

쿠키 정보는 항상 자동으로 서버에 전송되기 때문에 보안상 문제가 발생할 수 있다. 따라서 제한하는 방법이 필요하다.


### 쿠키 - 생명주기

**expires, max-age**

- Set-Cookie: **expires**=Sat, 26-Dec-2020 04:39:21 GMT
  - 만료일이 되면 쿠키 삭제
- Set-Cookie: **max-age**=3600 (3600초)
  - 0이나 음수를 지정하면 쿠키 삭제
- 세션 쿠키: 만료 날짜를 생략하면 브라우저 종료시 까지만 유지
- 영속 쿠키: 만료 날짜를 입력하면 해당 날짜까지 유지

### 쿠키 - 도메인

**domain**

- 예) domain=example.org
- 명시: 명시한 문서 기준 도메인 + 서브 도메인 포함 -> 서버에서 쿠키를 생성할 떄 지정한 domain( `cookie.setDomain("google.com");` )의 경우
  - domain=example.org 를 지정해서 쿠키 생성
    - example.org 는 물론이고
    - dev.example.org 도 쿠키 접근
- 생략: 현재 문서 기준 도메인만 적용
  - example.org 에서 쿠키를 생성하고 domain 지정을 생략
    - example.org 에서만 쿠키 접근
    - dev.example.org 는 쿠키 미접근

### 쿠키 - 경로

**path**

클라이언트에서 서버로 지정한 path( `cookie.setPath("/home");` ) 와 path 를 포함한 하위 path 로 접근하면 서버로 쿠키를 전달

- 예) path=/home
- 이 경로를 포함한 하위 경로 페이지만 쿠키 접근
  - 일반적으로 path=/ 루트로 지정
  - 예)
    - path=/home 지정
    - /home -> 가능
    - /home/level1 -> 가능
    - /home/level1/level2 -> 가능
    - /hello -> 불가능

### 쿠키 - 보안

**Secure, HttpOnly, SameSite**

- Secure
  - 쿠키는 http, https 를 구분하지 않고 전송
  - Secure 를 적용하면 https 인 경우에만 전송
- HttpOnly
  - XSS 공격 방지
  - 자바스크립트에서 접근 불가(document.cookie)
  - HTTP 전송에만 사용
- SameSite
  - XSRF 공격 방지
  - 요청 도메인과 쿠키에 설정된 도메인이 같은 경우만 쿠키 전송

