# URI 와 웹 브라우저 요청 흐름

## URI

URI(Uniform Resource Identifier)

**URI? URL? URN?**

![image](https://user-images.githubusercontent.com/83503188/206900230-0b9a3740-3c3d-4187-83de-25b5a8f5192b.png)
- URI 라는 자원 식별자가 URL, URN 을 포함하는 개념
- URL(Resource Locator)는 자원의 위치를 나타낸다.
- URN(Resource Name)은 자원의 이름을 나타낸다. 

![image](https://user-images.githubusercontent.com/83503188/206900312-c24d5cef-20f9-429e-a76e-0bc80d17212b.png)

**URI**
- Uniform: 리소스 식별하는 통일된 방식
- Resource: 자원, URI 로 식별할 수 있는 모든 것(제한 X)
- Identifier: 다른 항목과 구부하는데 필요한 정보

**URL, URN**
- URL(Uniform Resource Locator) - Locator: 리소스가 있는 위치를 지정
- URN(Uniform Resource Name) - Name: 리소스에 이름을 부여
- 위치는 변할 수 있지만, 이름은 변하지 않는다.
- urn:isbn:8960777331 (어떤 책의 isbn URN)
- URN 이름만으로 실제 리소스를 찾을 수 있는 방법이 보편화 되지 않음
- 따라서 URN 은 거의 사용하지 않고, URL 를 사용한다.
- 앞으로 URI 를 URL 과 같은 의미로 생각

**URL 전체 문법**

- `scheme://[userinfo@]host[:port][/path][?query][#fragment]`
- `https://www.google.com:443/search?q=hello&hl=ko`

- 프로토콜(`https`)
- 호스트명(`www.google.com`)
- 포트 번호(`443`)
- path(`/search`)
- 쿼리 파라미터(`q=hello&hl=ko`)

**URL scheme**
- 주로 프로토콜 사용
- 프로토콜: 어떤 방식으로 자원에 접근할 것인가 하는 약속 규칙
  - 예) http, https, ftp, ...
- http 는 80 포트, https 는 443 포트를 주로 사용, 포트는 생략 가능
- https 는  http 에 보안 계층을 추가 (HTTP Secure)

**URL userinfo@**
- URL 에 사용자정보를 포함해서 인증
- 거의 사용하지 않음

**URL host**
- 호스트명
- 도메인명 또는 IP 주소를 직접 사용 가능

**URL PORT**
- 포트(PORT)
- 접속 포트
- 일반적으로 생략, 생략시 http 는 80, https 는 443

**URL path**
- 리소스 경로(path), 계층적 구조
- 예)
  - `/home/file1.jpg`
  - `/members`
  - `/members/100`, `/items/iphone12`

**URL query**
- key=value 형태
- ?로 시작, &로 추가 가능 -> `?keyA=value&keyB=valueB`
- query parameter, query string 등으로 불림, 웹서버에 제공하는 파라미터, 문자 형태

**URL fragment**
- html 내부 북마크 등에 사용
- 서버에 전송하는 정보 아님

## 웹 브라우저 요청 흐름

![image](https://user-images.githubusercontent.com/83503188/206900901-bda7da10-faf5-46d1-866e-826a3ced6475.png)
- 웹 브라우저에 해당 URL 을 입력

![image](https://user-images.githubusercontent.com/83503188/206900924-17979591-b5ff-492b-8ba5-92cc02f0ee42.png)
- 웹 브라우저에서는 DNS 에서 입력받은 호스트명의 IP를 조회한다.
- 조회 결과인 IP 주소와 입력받거나 생략된 PORT 번호를 가지고 HTTP 요청 메시지를 생성한다.

**생성된 HTTP 요청 메시지**

![image](https://user-images.githubusercontent.com/83503188/206901011-1ce4925c-5bda-4760-9aa6-56428f70884b.png)
- `GET`: HTTP 메서드
- `/search?q=hello&hl=ko`: path 정보, query parameter
- `HTTP/1.1`: HTTP 버전 정보
- `Host:www.google.com`: 호스트 정보

**HTTP 메시지 전송**

![image](https://user-images.githubusercontent.com/83503188/206901103-c2214244-5675-491e-a28b-1f616d0038c6.png)
- 웹 브라우저는 HTTP 메시지를 생성
- SOCKET 라이브러리를 통해 전달
  - IP 주소와 PORT 정보를 가지고 TCP/IP 연결
  - os 계층으로 데이터 전달
- TCP/IP 에서 전달받은 데이터를 통해 HTTP 메시지가 포함된 TCP/IP 패킷을 생성한다. 

![image](https://user-images.githubusercontent.com/83503188/206901225-d0d2f013-b475-4490-90ec-8fbe916da983.png)

![image](https://user-images.githubusercontent.com/83503188/206901238-c765afa0-ef9d-43f1-8112-02e6ffeff826.png)
- 요청을 받은 서버에서는 TCP 패킷에서 HTTP 메시지를 해석한 뒤 요청을 처리하고 응답 메시지를 만든다.

![image](https://user-images.githubusercontent.com/83503188/206901288-a3ecfe7d-3dd8-41ae-9550-cf1c4ff8dad3.png)
- `HTTP/1.1`: HTTP 버전 정보
- `200 OK`: 응답 코드 
- `Content-Type`: 응답하는 데이터 형식
- `Content-Length`: 응답하는 데이터 길이

![image](https://user-images.githubusercontent.com/83503188/206901308-b3060327-8de2-4d66-8705-c6b1c4e795bd.png)

![image](https://user-images.githubusercontent.com/83503188/206901320-6e809685-a68e-4801-8bea-94aec4055c15.png)
- 응답을 받은 웹 브라우저에서는 HTTP 메시지를 해석한다.

![image](https://user-images.githubusercontent.com/83503188/206901332-4a5be7a8-39b4-49a0-a5e3-235729326097.png)
- HTTP 메시지 데이터를 통해 웹 브라우저 HTML 렌더링한다.

