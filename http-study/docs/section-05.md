# HTTP 메서드 활용

## 클라이언트에서 서버로 데이터 전송

**데이터 전달 방식은 크게 2가지** 
- **쿼리 파라미터를 통한 데이터 전송**
  - GET
  - 주로 정렬 필터(검색어)
- **메시지 바디를 통한 데이터 전송**
  - POST, PUT, PATCH
  - 회원 가입, 상품 주문, 리소스 등록, 리소스 변경

**4가지 상황**
- 정적 데이터 조회
  - 이미지, 정적 텍스트 문서
- 동적 데이터 조회
  - 주로 검색, 게시판 목록에서 정렬 필터(검색어)
- HTML Form 을 통한 데이터 전송
  - 회원 가입, 상품 주문, 데이터 변경
- HTTP API 를 통한 데이터 전송
  - 회원 가입, 상품 주문, 데이터 변경
  - 서버 to 서버, 앱 클라이언트, 웹 클라이언트(Ajax)

### 정적 데이터 조회 - 쿼리 파라미터 미사용**

![image](https://user-images.githubusercontent.com/83503188/208878942-9f62a4f0-a5ea-40ef-88e3-e0e2b601879e.png)
- 이미지, 정적 텍스트 문서
- 조회는 GET 사용
- 정적 데이터는 일반적으로 쿼리 파라미터 없이 리소스 경로로 단순하게 조회 가능

### 동적 데이터 조회 - 쿼리 파라미터 사용**

![image](https://user-images.githubusercontent.com/83503188/208879275-371b3f08-417d-4bd7-b354-27425af8a6cd.png)
- 주로 검색, 게시판 목록에서 정렬 필터(검색어)
- 조회 조건을 줄여주는 필터, 조회 결과를 정렬하는 정렬 조건에 주로 사용
- 조회는 GET 사용
- GET 은 쿼리 파라미터 사용해서 데이터를 전달

### HTML Form 데이터 전송 

**POST 전송 - 저장**

![image](https://user-images.githubusercontent.com/83503188/208879760-e4148e10-17bc-4797-9cd6-6635b46532f7.png)
- `Content-Type: application/x-www-form-urlencoded`
- 메시지 바디에 쿼리 파라미터와 유사한 방식(key-value)으로 데이터를 생성하여 담는다.

**GET 전송 - 저장**

![image](https://user-images.githubusercontent.com/83503188/208880418-4198241b-96ce-4899-9b28-578593acdad0.png)
- method 가 `post` 인 경우에는 메시지 바디에 데이터를 넣는다.
- method 가 `get` 인 경우에는 url 경로에 데이터를 넣는다.

**GET 전송 - 조회**

![image](https://user-images.githubusercontent.com/83503188/208880736-5774370c-70b8-409c-9c23-8b2b1706208f.png)

**multipart/form-data**

![image](https://user-images.githubusercontent.com/83503188/208881032-de4ea3a2-e218-4118-84b8-ad398fdf38f3.png)
- `Content-Type: multipart/form-data`: 파일 전송 Content 타입
- username, age 뿐만 아니라 바이트로 표현된 파일도 전송

**정리**

- HTML Form submit시 POST 전송
  - 예) 회원 가입, 상품 주문, 데이터 변경
- Content-Type: application/x-www-form-urlencoded 사용
  - form의 내용을 메시지 바디를 통해서 전송(key=value, 쿼리 파라미터 형식)
  - 전송 데이터를 url encoding 처리
    - 예) abc김 -> abc%EA%B9%80
- HTML Form은 GET 전송도 가능
- Content-Type: multipart/form-data
  - 파일 업로드 같은 바이너리 데이터 전송시 사용
  - 다른 종류의 여러 파일과 폼의 내용 함께 전송 가능(그래서 이름이 multipart)
- 참고: HTML Form 전송은 GET, POST만 지원

### HTTP API 를 통한 데이터 전송

![image](https://user-images.githubusercontent.com/83503188/208882198-6ca6a296-2fa6-4728-8d74-26170c17ab7a.png)
- 서버 to 서버
  - 백엔드 시스템 통신
- 앱 클라이언트
  - 아이폰, 안드로이드
- 웹 클라이언트
  - HTML 에서 Form 전송 대신 자바 스크립트를 통한 통신에 사용(AJAX)
  - 예) React, VueJs 같은 웹 클라이언트와 API 통신
- POST, PUT, PATCH: 메시지 바디를 통해 데이터 전송
- GET: 조회, 쿼리 파라미터로 데이터 전달
- `Content-Type: application/json`을 주로 사용 (사실상 표준)
  - TEXT, XML, JSON 등등


## HTTP API 설계 예시

- HTTP API - 컬렉션
  - POST 기반 등록
  - 예) 회원 관리 API 제공
- HTTP API - 스토어
  - PUT 기반 등록
  - 예) 정적 컨텐츠 관리, 원격 파일 관리
- HTML FORM 사용
  - 웹 페이지 회원 관리
  - GET, POST 만 지원

### 회원 관리 시스템

**API 설계 - POST 기반 등록**
- **회원** 목록 `/members` -> **GET**
- **회원** 등록 `/members` -> **POST**
- **회원** 조회 `/members/{id}` -> **GET**
- **회원** 수정 `/members/{id}` -> **PATCH, PUT, POST**
  - PUT: 기존 리소스 덮어쓰기
    - 게시글 수정의 경우 기존 리소스를 사용자에게 전달하고
    - 사용자가 처음부터 다시 작성하여 전송하기 때문에 PUT이 알맞다.
  - PATCH: 리소스 부분 수정, 회원 데이터 수정은 개념적으로 PATCH가 가장 알맞다.
  - POST: PUT, PATCH 모두 애매한 경우
- **회원** 삭제 `/members/{id}` -> **DELETE**

**POST - 신규 자원 등록 특징**
- 클라이언트는 등록될 리소스의 URL 를 모른다.
  - 회원 등록 `/members` -> POST
  - POST `/members`
- 서버가 새로 등록된 리소스 URL 를 생성해준다.
  ```text
  HTTP /1.1 201 Created
  Location: /members/100
  ```
- 컬렉션(Collection)
  - 서버가 관리하는 리소스 디렉토리
  - 서버가 리소소의 URL 를 생성하고 관리
  - 여기서 컬렉션은 `/members`

### 파일 관리 시스템

**API 설계 - PUT 기반 등록**

- **파일** 목록 `/files` -> **GET**
- **파일** 조회 `/files/{filename}` -> **GET**
- **파일** 등록 `/files/{filename}` -> **PUT**
  - 서버에 filename에 해당하는 file이 존재하면 덮어쓰기
  - 존재하지 않으면 새로 생성 
- **파일** 삭제 `/files/{filename}` -> **DELETE**
- **파일** 대량 등록 `/files` -> **POST**

**PUT - 신규 자원 등록 특징**
- 클라이언트가 리소스 URI(`/files/{filename}`)를 알고 있어야 한다.
  - 파일 등록 `/files/{filename}` -> PUT
  - PUT `/files/star.jpg`
- 클라이언트가 직접 리소스의 URI 를 지정한다.
- 스토어(Store)
  - 클라이언트가 관리하는 리소스 저장소
  - 클라이언트가 리소스의 URI 를 알고 관리
  - 여기서 스토어는 `/files`

POST, PUT 기반 중에 대부분 POST 기반의 자원 등록을 사용한다.

### HTML FORM 사용

- HTML FORM은 GET, POST만 지원
- AJAX 같은 기술을 사용해서 해결 가능 -> 회원 API 참고
- 여기서는 순수 HTML, HTML FORM 이야기
- GET, POST만 지원하므로 제약이 있음

- **회원** 목록 `/members` -> **GET**
- **회원** 등록 폼 `/members/new` -> **GET**
- **회원** 등록 `/members/new`, `/members` -> **POST**
  - `/members/new`: 폼 URL 과 맞추는 형태
    - Validation 이 맞지 않는 경우 폼으로 다시 보내야 하는 경우가 발생할 수 있다.
    - 폼 URL 와 맞추는 경우 깔끔하게 처리가 가능하기 때문에 선호한다.
  - `/members`: 폼 URL 과 맞추지 않는 형태
- **회원** 조회 `/members/{id}` -> **GET**
- **회원** 수정 폼 `/members/{id}/edit` -> **GET**
- **회원** 수정 `/members/{id}/edit`, `/members/{id}` -> **POST**
- **회원** 삭제 `/members/{id}/delete` -> **POST**

- HTML FORM 은 GET, POST 만 지원
- 컨트롤 URI
  - GET, POST 만 지원하므로 제약이 있음
  - 이런 제약을 해결하기 위해 동사로 된 리소스 경로 사용
  - POST 의 `/new`, `/edit`, `/delete`가 컨트롤 URI
  - HTTP 메서드로 해결하기 애매한 경우 사용(HTTP API 포함)

최대한 리소스를 중심으로 URI 을 설계하고, 막히는 경우 컨트롤 URI 를 대체제로 사용하자

---

https://restfulapi.net/resource-naming

**참고하면 좋은 URI 설계 개념**

- 문서(document)
  - 단일 개념(파일 하나, 객체 인스턴스, 데이터베이스 row)
  - 예) `/members/100`, `/files/star.jpg`
- 컬렉션(collection)
  - 서버가 관리하는 리소스 디렉터리
  - 클라이언트는 요청만하고, 서버가 리소스의 URI를 생성하고 관리  
  - 예) `/members`
- 스토어(store)
  - 클라이언트가 관리하는 자원 저장소
  - 클라이언트가 리소스의 URI를 알고 관리
  - 예) `/files`
- 컨트롤러(controller), 컨트롤 URI
  - 문서, 컬렉션, 스토어로 해결하기 어려운 추가 프로세스 실행
  - 동사를 직접 사용
  - 예) `/members/{id}/delete`
