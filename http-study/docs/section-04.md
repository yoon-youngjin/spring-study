# HTTP 메서드

## HTTP API 를 만들어보자

**요구사항 - 회원 정보 관리 API**
- 회원 목록 조회
- 회원 조회
- 회원 등록
- 회원 수정
- 회원 삭제

**API URI 설계 - URI**
- 회원 목록 조회 -> `/read-member-list`
- 회원 조회 -> `/read-member-by-id`
- 회원 등록 -> `/create-member`
- 회원 수정 -> `/update-member`
- 회원 삭제 -> `/delete-member`

위의 URI 설계는 좋은 설계일까? X 
- 가장 중요한 것은 **리소스 식별**

**API URI 고민 - URI**
- 리소스의 의미?
  - 회원을 등록하고 수정하고 조회하는게 리소스가 아니다.
  - 예) 미네랄을 캐라 -> 미네랄이 리소스
  - *회원이라는 개념 자체가 바로 리소스*
- 리소스를 어떻게 식별하는게 좋을까?
  - 회원을 등록하고 수정하고 조회하는 것을 모두 배제
  - *회원이라는 리소스만 식별하면 된다. -> 회원 리소스를 URI 에 매핑*

**API URL 설계 - 리소스 식별, URI 계층 구조 활용**
- 회원 목록 조회 -> `/members`
- 회원 조회 -> `/members/{id}`
- 회원 등록 -> `/members/{id}`
- 회원 수정 -> `/members/{id}`
- 회원 삭제 -> `/members/{id}`

> 참고
> 
> 계층 구조상 상위를 컬렉션으로 보고 복수단어 사용 권장(member -> members)

조회, 등록, 수정, 삭제가 구분이 안된다. 

**리소스와 행위를 분리 - 가장 중요한 것은 리소스를 식별하는 것**
- URI 는 리소스만 식별
- **리소스**와 해당 리소스를 대상으로 하는 **행위**를 분리
  - 리소스: 회원
  - 행위: 조회, 등록, 삭제, 변경
- 리소스는 명사, 행위는 동사
- 행위(메서드)는 어떻게 구분? **HTTP 메서드**

## HTTP 매서드 - GET, POST

**HTTP 메서드 종류 - 주요 메서드**
- GET: 리소스 조회
- POST: 요청 데이터 처리, 주로 등록에 사용
- PUT: 리소스를 대체, 해당 리소스가 없으면 생성
- PATCH: 리소스 부분 변경
- DELETE: 리소스 삭제

**HTTP 메서드 종류 - 기타 메서드**
- HEAD: GET 과 동일하지만 메시지 부분을 제외하고, 상태 줄과 헤더만 반환
- OPTIONS: 대상 리소스에 대한 통신 가능 옵션(메서드)을 설명(주로 CORS 에서 사용)
- CONNECT: 대상 자원으로 식별되는 서버에 대한 터널을 설정
- TRACE: 대상 리소스에 대한 경로를 따라 메시지 루프백 테스트를 수행

### GET

![image](https://user-images.githubusercontent.com/83503188/207319122-ff5b64f8-d167-4659-a105-c9bc1e111998.png)

- 리소스 조회
- 서버에 전달하고 싶은 데이터는 query(쿼리 파라미터, 쿼리 스트링)를 통해서 전달
- 메시지 바디를 사용해서 데이터를 전달할 수 있지만, 지원하지 않는 곳이 많아서 권장하지 않음
  - 지원하지 않는 서버가 많음

**리소스 조회1 - 메시지 전달**

![image](https://user-images.githubusercontent.com/83503188/207319452-e423671b-3cae-4c9b-a6f3-f0f2eef4dc5e.png)

**리소스 조회2 - 서버도착**

![image](https://user-images.githubusercontent.com/83503188/207319493-d0bd504d-1cf5-49ba-af39-8f9022dd8b3f.png)

**리소스 조회3 - 응답 데이터**

![image](https://user-images.githubusercontent.com/83503188/207319760-62d60290-049b-4e99-b9d0-f2c1e7e70f42.png)


### POST

![image](https://user-images.githubusercontent.com/83503188/207320343-a1afb1fa-badf-4c1e-9af6-cc2d02f1da24.png)

- 요청 데이터 처리
- **메시지 바디를 통해 서버로 요청 데이터 전달**
- 서버는 요청 데이터를 처리
  - 메시지 바디를 통해 들어온 데이터를 처리하는 모든 기능을 수행한다.
- 주로 전달된 데이터로 신규 리소스 등록, 프로세스 처리에 사용

**리소스 등록1 - 메시지 전달**

![image](https://user-images.githubusercontent.com/83503188/207320602-b989afd2-4e74-4cfe-b3be-8020a82e52a9.png)


**리소스 등록2 - 서버도착**

![image](https://user-images.githubusercontent.com/83503188/207320625-df40cdae-e35d-4fcb-aac7-4d7a0eb697b8.png)


**리소스 등록3 - 응답 데이터**

![image](https://user-images.githubusercontent.com/83503188/207320659-7e570c41-1199-4fd3-9d42-0a2ad20f009f.png)
- 리소스 생성은 주로 201 응답 코드를 사용
- Location 을 통해 자원이 생성된 path 를 지정

**요청 데이터를 어떻게 처리한다는 뜻일까? 예시**

- 스펙: POST 메서드는 대상 리소스가 리소스의 고유 한 의미 체계에 따라 요청에 포함 된 표현을 처리하도록 요청합니다. (구글 번역)
- 예를 들어 POST는 다음과 같은 기능에 사용됩니다.
  - HTML 양식에 입력 된 필드와 같은 데이터 블록을 데이터 처리 프로세스에 제공
    - 예) HTML FORM에 입력한 정보로 회원 가입, 주문 등에서 사용
  - 게시판, 뉴스 그룹, 메일링 리스트, 블로그 또는 유사한 기사 그룹에 메시지 게시
    - 예) 게시판 글쓰기, 댓글 달기
  - 서버가 아직 식별하지 않은 새 리소스 생성
    - 예) 신규 주문 생성
  - 기존 자원에 데이터 추가
    - 예) 한 문서 끝에 내용 추가하기
- 정리: 이 리소스 URI에 POST 요청이 오면 요청 데이터를 어떻게 처리할지 리소스마다 따로 정해야 함 -> 정해진 것이 없음

**정리**

1. 새 리소스 생성(등록)
   - 서버가 아직 식별하지 않은 새 리소스 생성
2. **요청 데이터 처리**
   - **단순히 데이터를 생성하거나, 변경하는 것을 넘어서 프로세스를 처리해야 하는 경우**
   - 예) 주문에서 결제완료 -> 배달시작 -> 배달완료 처럼 단순히 값 변경을 넘어 프로세스의 상태가 변경되는 경우
   - POST 의 결과로 새로운 리소스가 생성되지 않을 수도 있음
   - 예) POST /orders/{orderId}/start-delivery (컨트롤 URI)
     - URI 설계가 자원만으로 한계가 있을 경우에 위와 같은 컨트롤 URI 가 존재할 수 있다.
3. 다른 메서드로 처리하기 애매한 경우
   - 예) JSON 으로 조회 데이터를 넘겨야 하는데, GET 메서드를 사용하기 어려운 경우
   - 애매하면 POST

## HTTP 메서드 - PUT, PATCH, DELETE

### PUT
![image](https://user-images.githubusercontent.com/83503188/207322436-c0ff5a45-75fd-42fe-a8fd-fdce2bd21c14.png)

- 리소스를 대체
  - 리소스가 있으면 대체
  - 리소스가 없으면 생성
  - 덮어쓰기
- **중요! 클라이언트가 리소스를 식별**
  - 클라이언트가 리소스 위치를 알고 URI 지정
    - 클라이언트가 members 의 100번 위치를 알고 있다
  - POST 와 차이점

**리소스가 있는 경우1**

![image](https://user-images.githubusercontent.com/83503188/207323077-ee6dd39f-8804-4713-961f-03626a24bcbf.png)

**리소스가 있는 경우2**

![image](https://user-images.githubusercontent.com/83503188/207323136-46ffbb59-4f4b-4aa1-8727-91f5fd684390.png)

**리소스가 없는 경우1**

![image](https://user-images.githubusercontent.com/83503188/207323208-602f9398-df6c-4beb-b83b-6c36075c4b6c.png)

**리소스가 없는 경우2**

![image](https://user-images.githubusercontent.com/83503188/207323240-f5d1ca46-2325-43a9-a2db-7ee4ee1020b4.png)

**주의! - 리소스를 완전히 대체한다1**

![image](https://user-images.githubusercontent.com/83503188/207323425-15c4c24e-88a4-4c32-b738-0de6728a7d6b.png)

**주의! - 리소스를 완전히 대체한다2**

![image](https://user-images.githubusercontent.com/83503188/207323455-23bfb583-944a-46e4-afa0-d4ff1bcc8213.png)
- username 필드가 사라진다.
- 리소스를 부분만 변경하기 위해서는 PATCH 를 사용해야한다.

### PATCH

리소스 부분 변경

![image](https://user-images.githubusercontent.com/83503188/207322490-69104b8e-afb4-4f9c-ba12-c152ce22d23d.png)


**리소스 부분 변경1**

![image](https://user-images.githubusercontent.com/83503188/207323693-fb47972c-cbdc-4e69-8343-b798f635a9ac.png)

**리소스 부분 변경2**

![image](https://user-images.githubusercontent.com/83503188/207323725-e7d0608f-11e5-459b-818a-67f96fca84f1.png)

리소스를 부분 변경할 때는 대부분 PATCH 를 사용하는데, PATCH 메서드를 받지 못하는 서버도 존재한다. 이런 경우에는 POST 메서드를 사용하면 된다.

### DELETE

리소스 제거

![image](https://user-images.githubusercontent.com/83503188/207322519-26aaebac-8bcc-46b2-9148-3fb7509fafd8.png)

**리소스 제거1**

![image](https://user-images.githubusercontent.com/83503188/207323922-22baabfe-e49f-4fc4-bd68-80d755c4a161.png)

**리소스 제거2**

![image](https://user-images.githubusercontent.com/83503188/207323945-1a3ece35-97fe-4b78-9cff-f14bbf0a7d78.png)

## HTTP 메서드의 속성

- 안전(Safe Methods)
- 멱등(Idempotent Methods)
- 캐시가능(Cacheable Methods)

![image](https://user-images.githubusercontent.com/83503188/207324544-d3ee5c88-8070-4ce7-bf0f-addd5efaa78f.png)

### 안전(Safe)

- 호출해도 리소스를 변경하지 않는다.
- Q: 계속 호출해서, 로그 같은게 쌓여서 장애가 발생하는 경우?
- A: 안전은 해당 리소스만 고려한다. 그런 부분까지 고려하지 않는다.

### 멱등(Idempotent)

- f(f(x)) = f(x)
- 한 번 호출하든 두 번 호출하든 100번 호출하든 결과가 똑같다.
- 멱등 메서드
  - GET: 한 번 조회하든, 두 번 조회하든 같은 결과가 조회된다.
  - PUT: 결과를 대체한다. 따라서 같은 요청을 여러번 해도 최종 결과는 같다.
  - DELETE: 결과를 삭제한다. 같은 요청을 여러번 해도 삭제된 결과는 똑같다.

POST 는 멱등이 아니다! 두 번 호출하면 같은 결제가 중복해서 발생할 수 있다.

- 활용
  - 자동 복구 매커니즘
  - 서버가 TIMEOUT 등으로 정상 응답을 못주었을 때, 클라이언트가 같은 요청을 다시 해도 되는가? 판단 근거
  - 멱등성을 가진 메서드는 같은 요청을 다시해도 되지만, 그렇지 않은 메서드는 다시 요청하면 안된다.

- Q: 재요청 중간에 다른 곳에서 리소스를 변경해버리면?
  - 사용자1: GET -> username:A, age:20
  - 사용자2: PUT -> username:A, age:30
  - 사용자1: GET -> username:A, age:30 -> 사용자2의 영향으로 바뀐 데이터 조회
- A: 멱등은 외부 요인으로 중간에 리소스가 변경되는 것 까지는 고려하지는 않는다.

### 캐시가능(Cacheable)

- 응답 결과 리소스를 캐시해서 사용해도 되는가?
  - 웹 브라우저에 큰 사이즈의 이미지를 요청하는 경우 웹 브라우저가 내부에 해당 이미지를 저장할 수 있는가?
- GET, HEAD, POST, PATCH 캐시가능 
- 실제로는 GET, HEAD 정도만 캐시로 사용
  - POST, PATCH 는 본문 내용까지 캐시 키로 고려해야 하는데, 구현이 쉽지 않음


