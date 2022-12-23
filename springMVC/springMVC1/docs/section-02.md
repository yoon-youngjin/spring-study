# 서블릿

[//]: # (현재는 JSP를 공부해야하므로)

[//]: # (Packaging -> War: Jar는 빌드된 결과물에 톰캣 서버를 내장한다 / War는 톰캣 서버를 별도로 설치하는 경우에 사용한다.)

### Hello 서블릿

스프링 부트 환경에서 서블릿 등록하고 사용해보자.

#### 스프링 부트 서블릿 환경 구성

스프링 부트는 서블릿을 직접 등록해서 사용할 수 있도록 `@ServletComponentScan` 을 지원한다.

```java

@ServletComponentScan //서블릿 자동 등록
@SpringBootApplication
public class ServletApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServletApplication.class, args);
    }
}
```

**HelloServlet**

```java
// "/hello"로 요청하는 경우 처리
@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + req);
        System.out.println("response = " + resp);

        String username = req.getParameter("username");
        System.out.println("username = " + username);

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write("hello " + username);

    }
}
```

- Http 요청이 오면 WAS 의 Servlet Container 가 Request, Response를 새로 생성하여 Servlet에 전달한다.
- HttpServletRequest, HttpServletResponse 는 모두 인터페이스 톰캣, Jetty, .. 등등의 WAS 서버가 해당 표준 인터페이스를 구현
- HttpServletResponse 에 응답 데이터를 담는다.

**결과**

```text
HelloServlet.service
request = org.apache.catalina.connector.RequestFacade@51152c2
response = org.apache.catalina.connector.ResponseFacade@3d3af814
username = yoon
```

HTTP 스펙을 직접 맞춰서 응답 메시지를 만들어서 반환하는 과정은 굉장히 귀찮은 작업인데 서블릿을 통해 간편하게 처리할 수 있다.

- `@WebServlet` 서블릿 애노테이션
    - name: 서블릿 이름 -> 중복 X
    - urlPatterns: URL 매핑

HTTP 요청을 통해 매핑된 URL이 호출되면 서블릿 컨테이너는 다음 메서드를 실행한다.

`protected void service(HttpServletRequest request, HttpServletResponse response)`

#### HTTP 요청 메시지 로그로 확인하기

```properties
logging.level.org.apache.coyote.http11=debug
```

**결과**

```text
Host: localhost:8080
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: "Google Chrome";v="107", "Chromium";v="107", "Not=A?Brand";v="24"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Sec-Fetch-Site: none
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
```

#### 서블릿 컨테이너 동작 방식 설명

**내장 톰캣 서버 생성**

![image](https://user-images.githubusercontent.com/83503188/200858273-7d5cd4f3-1d80-4ddc-a3a5-a5d72a9587fa.png)

- 스프링 부트를 실행하면 스프링이 내장 톰캣 서버를 실행해준다.
- 톰캣 서버는 내부에 서블릿 컨테이너 기능을 가지고 있다.
- 서블릿 컨테이너는 서블릿을 생성해준다.

**HTTP 요청, HTTP 응답 메시지**

![image](https://user-images.githubusercontent.com/83503188/200858401-06e2f2d3-baa8-4850-98f8-2104a6f1014e.png)

**웹 애플리케이션 서버의 요청 응답 구조**

![image](https://user-images.githubusercontent.com/83503188/200858465-7bacd76f-f70e-4fc5-8678-cdabb90db2c5.png)

- 요청이 오면 WAS 서버는 request, response 객체를 만들어서 객체를 파라미터로 servlet 의 `service()`를 호출한다.
- 전달받은 response 객체에 header 정보, data 정보를 담아서 반환할 수 있다.

### HttpServletRequest - 개요

**HttpServletRequest 역할**

HTTP 요청 메시지를 개발자가 직접 파싱해서 사용해도 되지만, 매우 불편할 것이다. 서블릿은 개발자가 HTTP 요청 메시지를 편리하게 사용할 수 있도록 개발자 대신에 HTTP 요청 메시지를 파싱한다. 그리고 그
결과를 HttpServletRequest 객체에 담아서 제공한다.

**HTTP 요청 메시지**

```text
POST /save HTTP/1.1 -> START LINE
Host: localhost:8080 -> Header
Content-Type: application/x-www-form-urlencoded -> Header

username=kim&age=20 -> Body
```

- START LINE
  - HTTP 메소드
  - URL
  - 쿼리 스트링
  - 스키마, 프로토콜
- 헤더
  - 헤더 조회
- 바디
  - form 파라미터 형식 조회
  - message body 데이터 직접 조회

HttpServletRequest 객체는 추가로 여러가지 부가기능도 함께 제공한다.

**임시 저장소 기능**
- 해당 HTTP 요청이 시작부터 끝날 때 까지 유지되는 임시 저장소 기능
  - 저장: `request.setAttribute(name, value)`
  - 조회: `request.getAttribute(name)`
**세션 관리 기능**
- `request.getSession(create: true)`

### HttpServletRequest - 기본 사용법

**RequestHeaderServlet**

```java
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printStartLine(request);
        printHeaders(request);
        printHeaderUtils(request);
        printEtc(request);
        response.getWriter().write("ok");

    }
...
}
```

**start-line 정보**

```java
 private void printStartLine(HttpServletRequest request) {
        System.out.println("--- REQUEST-LINE - start ---");
        System.out.println("request.getMethod() = " + request.getMethod()); // GET
        System.out.println("request.getProtocol() = " + request.getProtocol()); // HTTP/1.1
        System.out.println("request.getScheme() = " + request.getScheme()); // http
        System.out.println("request.getRequestURL() = " + request.getRequestURL()); // http://localhost:8080/request-header

        System.out.println("request.getRequestURI() = " + request.getRequestURI());  // /request-header

        System.out.println("request.getQueryString() = " + request.getQueryString()); //username=hi

        System.out.println("request.isSecure() = " + request.isSecure()); //https 사용 유무
        System.out.println("--- REQUEST-LINE - end ---");
        System.out.println();
    }
```

```text
--- REQUEST-LINE - start ---
request.getMethod() = GET
request.getProtocol() = HTTP/1.1
request.getScheme() = http
request.getRequestURL() = http://localhost:8080/request-header
request.getRequestURI() = /request-header
request.getQueryString() = username=yoon
request.isSecure() = false
--- REQUEST-LINE - end ---
```

**헤더 정보**

```java
private void printHeaders(HttpServletRequest request) {
        System.out.println("--- Headers - start ---");
        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> System.out.println(headerName + ": " + request.getHeader(headerName)));
        System.out.println("--- Headers - end ---");
        System.out.println();
    }
```

```text
--- Headers - start ---
host: localhost:8080
connection: keep-alive
sec-ch-ua: "Google Chrome";v="107", "Chromium";v="107", "Not=A?Brand";v="24"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
upgrade-insecure-requests: 1
user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36
accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
sec-fetch-site: none
sec-fetch-mode: navigate
sec-fetch-user: ?1
sec-fetch-dest: document
accept-encoding: gzip, deflate, br
accept-language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
--- Headers - end ---
```

**Header 편리한 조회**

```java
private void printHeaderUtils(HttpServletRequest request) {
        System.out.println("--- Header 편의 조회 start ---");
        System.out.println("[Host 편의 조회]");
        System.out.println("request.getServerName() = " +
                request.getServerName()); //Host 헤더
        System.out.println("request.getServerPort() = " +
                request.getServerPort()); //Host 헤더
        System.out.println();
        System.out.println("[Accept-Language 편의 조회]");
        request.getLocales().asIterator()
                .forEachRemaining(locale -> System.out.println("locale = " +
                        locale));
        System.out.println("request.getLocale() = " + request.getLocale());
        System.out.println();
        System.out.println("[cookie 편의 조회]");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            }
        }
        System.out.println();
        System.out.println("[Content 편의 조회]");
        System.out.println("request.getContentType() = " + request.getContentType());
        System.out.println("request.getContentLength() = " + request.getContentLength());
        System.out.println("request.getCharacterEncoding() = " + request.getCharacterEncoding());
        System.out.println("--- Header 편의 조회 end ---");
        System.out.println();
    }
```

```text
--- Header 편의 조회 start ---
[Host 편의 조회]
request.getServerName() = localhost
request.getServerPort() = 8080

[Accept-Language 편의 조회]
locale = ko_KR
locale = ko
locale = en_US
locale = en
request.getLocale() = ko_KR

[cookie 편의 조회]

[Content 편의 조회]
request.getContentType() = null
request.getContentLength() = -1
request.getCharacterEncoding() = UTF-8
--- Header 편의 조회 end ---
```

**기타 정보**

기타 정보는 HTTP 메시지의 정보는 아니다.

```java
private void printEtc(HttpServletRequest request) {
        System.out.println("--- 기타 조회 start ---");
        System.out.println("[Remote 정보]");
        System.out.println("request.getRemoteHost() = " + request.getRemoteHost()); //
        System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr()); //
        System.out.println("request.getRemotePort() = " + request.getRemotePort()); //
        System.out.println();
        System.out.println("[Local 정보]");
        System.out.println("request.getLocalName() = " + request.getLocalName()); //
        System.out.println("request.getLocalAddr() = " + request.getLocalAddr()); //
        System.out.println("request.getLocalPort() = " + request.getLocalPort()); //
        System.out.println("--- 기타 조회 end ---");
        System.out.println();
    }
```

```text
--- 기타 조회 start ---
[Remote 정보]
request.getRemoteHost() = 0:0:0:0:0:0:0:1
request.getRemoteAddr() = 0:0:0:0:0:0:0:1
request.getRemotePort() = 54728

[Local 정보]
request.getLocalName() = 0:0:0:0:0:0:0:1
request.getLocalAddr() = 0:0:0:0:0:0:0:1
request.getLocalPort() = 8080
--- 기타 조회 end ---

```

### HTTP 요청 데이터 - 개요

주로 다음 3가지 방법을 사용한다.
- **GET - 쿼리 파라미터**
  - /url?username=hello&age=20
  - 메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
  - 예) 검색, 필터, 페이징등에서 많이 사용하는 방식
- **POST - HTML Form**
  - content-type: application/x-www-form-urlencoded
  - 메시지 바디에 쿼리 파리미터 형식으로 전달 username=hello&age=20
  - 예) 회원 가입, 상품 주문, HTML Form 사용
- **HTTP message body에 데이터를 직접 담아서 요청**
  - HTTP API에서 주로 사용, JSON, XML, TEXT
  - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH

**POST- HTML Form 예시**

![image](https://user-images.githubusercontent.com/83503188/201055390-2fa64eaf-97d2-423a-a02d-e0df96b43ab3.png)
- content-type: application/x-www-form-urlencoded: HTML form을 통해서 전달된 정보임을 명시한다.

### HTTP 요청 데이터 - GET 쿼리 파라미터

전달 데이터
- username=hello
- age=20

메시지 바디 없이, URL의 쿼리 파라미터를 사용해서 데이터를 전달하자.

쿼리 파라미터는 URL에 다음과 같이 ? 를 시작으로 보낼 수 있다. 추가 파라미터는 & 로 구분하면 된다.
- http://localhost:8080/request-param?username=hello&age=20

서버에서는 HttpServletRequest 가 제공하는 다음 메서드를 통해 쿼리 파라미터를 편리하게 조회할 수 있다.

**쿼리 파라미터 조회 메서드**

```java
/**
 * 1. 파라미터 전송 기능
 * http://localhost:8080/request-param?username=hello&age=20
 *
 * 2. 동일한 파라미터 전송 가능
 * http://localhost:8080/request-param?username=hello&username=kim&age=20
 */
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class requestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("requestParamServlet.service");

        System.out.println("[전체 파라미터 조회] - start");
        request.getParameterNames().asIterator()
                .forEachRemaining(param -> System.out.println(param + "=" + request.getParameter(param)));

        System.out.println("[전체 파라미터 조회] - end");

        System.out.println("[단일 파라미터 조회]");
        String username = request.getParameter("username");
        System.out.println("request.getParameter(username) = " + username);
        String age = request.getParameter("age");
        System.out.println("request.getParameter(age) = " + age);
        System.out.println();
        System.out.println("[이름이 같은 복수 파라미터 조회]");
        System.out.println("request.getParameterValues(username)");
        String[] usernames = request.getParameterValues("username");
        for (String name : usernames) {
            System.out.println("username=" + name);
        }
    }
}
```


```text
[전체 파라미터 조회] - start
username=hello
age=20
[전체 파라미터 조회] - end
[단일 파라미터 조회]
request.getParameter(username) = hello
request.getParameter(age) = 20

[이름이 같은 복수 파라미터 조회]
request.getParameterValues(username)
username=hello
username=kim
```

**복수 파라미터에서 단일 파라미터 조회**

username=hello&username=kim 과 같이 파라미터 이름은 하나인데, 값이 중복이면 어떻게 될까?

`request.getParameter()` 는 하나의 파라미터 이름에 대해서 단 하나의 값만 있을 때 사용해야 한다.
지금처럼 중복일 때는 `request.getParameterValues()` 를 사용해야 한다.
참고로 이렇게 중복일 때 `request.getParameter()` 를 사용하면 `request.getParameterValues()` 의 첫 번째 값을 반환한다.

하지만 보통 중복된 값을 보내는 경우는 거의 없다.

### HTTP 요청 데이터 - POST HTML Form

이번에는 HTML의 Form을 사용해서 클라이언트에서 서버로 데이터를 전송해보자.

주로 회원 가입, 상품 주문 등에서 사용하는 방식이다.

**특징**
- content-type: application/x-www-form-urlencoded
- 메시지 바디에 쿼리 파리미터 형식으로 데이터를 전달한다. username=hello&age=20


POST의 HTML Form을 전송하면 웹 브라우저는 다음 형식으로 HTTP 메시지를 만든다.

![image](https://user-images.githubusercontent.com/83503188/201062952-16cc2fc5-5f13-402a-9b3e-d55da42169c5.png)
![image](https://user-images.githubusercontent.com/83503188/201063241-d9cab9b6-ebd2-48a3-9d81-5f6de051bd74.png)

- 요청 URL: http://localhost:8080/request-param
- content-type: application/x-www-form-urlencoded
- message body: username=hello&age=20

application/x-www-form-urlencoded 형식은 앞서 GET에서 살펴본 쿼리 파라미터 형식과 같다.
따라서 쿼리 파라미터 조회 메서드를 그대로 사용하면 된다.
클라이언트(웹 브라우저) 입장에서는 두 방식에 차이가 있지만, 서버 입장에서는 둘의 형식이 동일하므로, `request.getParameter()` 로 편리하게 구분없이 조회할 수 있다.
정리하면 `request.getParameter()` 는 GET URL 쿼리 파라미터 형식도 지원하고, POST HTML Form 형식도 둘 다 지원한다.

> 참고
> 
> content-type은 HTTP 메시지 바디의 데이터 형식을 지정한다.
> GET URL 쿼리 파라미터 형식으로 클라이언트에서 서버로 데이터를 전달할 때는 HTTP 메시지 바디를 사용하지 않기 때문에 content-type이 없다.
> POST HTML Form 형식으로 데이터를 전달하면 HTTP 메시지 바디에 해당 데이터를 포함해서 보내기 때문에 바디에 포함된 데이터가 어떤 형식인지 content-type을 꼭 지정해야 한다.
> 이렇게 폼으로 데이터를 전송하는 형식을 application/x-www-form-urlencoded 라 한다.

### HTTP 요청 데이터 - API 메시지 바디 - 단순 텍스트

HTTP 요청 데이터를 일반적인 웹 브라우저에서 사용하는 방식이 아닌 HTTP API, REST API에서 사용하는 방식 

- HTTP message body에 데이터를 직접 담아서 요청
  - HTTP API에서 주로 사용, JSON, XML, TEXT
  - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH

- 먼저 가장 단순한 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고, 읽어보자.
- HTTP 메시지 바디의 데이터를 InputStream을 사용해서 직접 읽을 수 있다.

**RequestBodyStringServlet**

```java
@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("messageBody = " + messageBody);
        response.getWriter().write("ok");
    }
}
```

- `request.getInputStream()`: 메시지 바디의 내용을 바이트 코드로 바로 얻을 수 있다.
- `StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);`: 읽어온 바이트 코드를 String으로 변환하기 위해서 스프링이 StreamUtils를 제공한다.


**결과**

```text
Content-Type: text/plain
User-Agent: PostmanRuntime/7.29.2
Accept: */*
Host: 127.0.0.1:8080
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
Content-Length: 5

hello]
messageBody = hello
```

### HTTP 요청 데이터 - API 메시지 바디 - JSON

```java
@Getter
@Setter
public class HelloData {
    private String username;
    private int age;
}
```





```java
/**
 * http://localhost:8080/request-body-json
 *
 * JSON 형식 전송
 * content-type: application/json
 * message body: {"username": "hello", "age": 20}
 *
 */
@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-bodyjson")
public class RequestBodyJsonServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("messageBody = " + messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        System.out.println("helloData.username = " + helloData.getUsername());
        System.out.println("helloData.age = " + helloData.getAge());
        response.getWriter().write("ok");
    }
}
```

```text
Content-Type: application/json
User-Agent: PostmanRuntime/7.29.2
Accept: */*
Host: 127.0.0.1:8080
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
Content-Length: 45

{
    "username": "hello",
    "age": 12
}]
messageBody = {
    "username": "hello",
    "age": 12
}
data.username=hello
data.age=12
```

### HttpServletResponse - 기본 사용법

HttpServletResponse 역할

**HTTP 응답 메시지 생성**
- HTTP 응답코드 지정
- 헤더 생성
- 바디 생성

**편의 기능 제공**
- Content-Type, 쿠키, Redirect

**ResponseHeaderServlet**

```java
@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //[status-line]
        response.setStatus(HttpServletResponse.SC_OK); //200
        //[response-headers]
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, mustrevalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("my-header", "hello");
        //[Header 편의 메서드]
        content(response);
        cookie(response);
        redirect(response);
        //[message body]
        PrintWriter writer = response.getWriter();
        writer.println("ok");
    }

...
}
```

**Content 편의 메서드**

```java
private void content(HttpServletResponse response) {
        //Content-Type: text/plain;charset=utf-8
        //Content-Length: 2
        //response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        //response.setContentLength(2); //(생략시 자동 생성)
    }
```

**쿠키 편의 메서드**

```java
private void cookie(HttpServletResponse response) {
        //Set-Cookie: myCookie=good; Max-Age=600;
        //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); //600초
        response.addCookie(cookie);
    }
```

**redirect 편의 메서드**

```java
private void redirect(HttpServletResponse response) throws IOException {
        //Status Code 302
        //Location: /basic/hello-form.html
        //response.setStatus(HttpServletResponse.SC_FOUND); //302
        //response.setHeader("Location", "/basic/hello-form.html");
        response.sendRedirect("/basic/hello-form.html");
    }
```

![image](https://user-images.githubusercontent.com/83503188/201070531-de10b13a-642d-4efb-8edc-409b9169c6ea.png)


### HTTP 응답 데이터 - 단순 텍스트, HTML

HTTP 응답 메시지는 주로 다음 내용을 담아서 전달한다.

- 단순 텍스트 응답
  - 앞에서 살펴봄 ( writer.println("ok"); )
- HTML 응답
- HTTP API - MessageBody JSON 응답

#### HttpServletResponse - HTML 응답

**ResponseHtmlServlet**

```java
@WebServlet(name = "responseHtmlServlet", urlPatterns = "/response-html")
public class ResponseHtmlServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Content-Type: text/html;charset=utf-8
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<body>");
        writer.println(" <div>안녕?</div>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
```
- HTTP 응답으로 HTML을 반환할 때는 content-type을 text/html 로 지정해야 한다.

**결과**

![image](https://user-images.githubusercontent.com/83503188/201072238-903fc08d-7b1b-43ec-b82f-725e870e8f1c.png)

### HTTP 응답 데이터 - API JSON

**ResponseJsonServlet**

```java
@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Content-Type: application/json
        response.setHeader("content-type", "application/json");
        response.setCharacterEncoding("utf-8");
        HelloData data = new HelloData();
        data.setUsername("kim");
        data.setAge(20);
        //{"username":"kim","age":20}
        String result = objectMapper.writeValueAsString(data);
        response.getWriter().write(result);
    }
}
```
HTTP 응답으로 JSON을 반환할 때는 content-type을 application/json 로 지정해야 한다.

Jackson 라이브러리가 제공하는 `objectMapper.writeValueAsString()` 를 사용하면 객체를 JSON 문자로 변경할 수 있다.

**결과**

![image](https://user-images.githubusercontent.com/83503188/201072587-91808fd6-f202-433f-8582-52fe8a125f31.png)

