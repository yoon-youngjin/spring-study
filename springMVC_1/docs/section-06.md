# 스프링 MVC - 기본 기능

### 프로젝트 생성

WAS(Tomcat)을 별로도 설치하고, 거기에 빌드된 파일을 넣을 때 war를 사용한다.

또한 JSP를 사용하기 위해서 war를 사용한다.

반면에 Jar는 별도의 Tomcat 서버를 설치하지 않고 내장 톰캣을 사용할때 사용한다.

**Welcome 페이지 만들기**

스프링 부트에 Jar 를 사용하면 `/resources/static/` 위치에 `index.html` 파일을 두면 Welcome 페이지로 처리해준다. (스프링 부트가 지원하는 정적 컨텐츠 위치에 /index.html 이 있으면 된다.)

### 로깅 간단히 알아보기

**로깅 라이브러리**

스프링 부트 라이브러리를 사용하면 스프링 부트 로깅 라이브러리(spring-boot-starter-logging)가 함께 포함된다.

스프링 부트 로깅 라이브러리는 기본으로 다음 로깅 라이브러리를 사용한다.

- SLF4J - http://www.slf4j.org
- Logback - http://logback.qos.ch

로그 라이브러리는 Logback, Log4J, Log4J2 등등 수 많은 라이브러리가 있는데, 그것을 통합해서 인터페이스로 제공하는 것이 바로 SLF4J 라이브러리다.
쉽게 이야기해서 SLF4J는 인터페이스이고, 그 구현체로 Logback 같은 로그 라이브러리를 선택하면 된다.
실무에서는 스프링 부트가 기본으로 제공하는 Logback을 대부분 사용한다.

**로그 선언**
- `private Logger log = LoggerFactory.getLogger(getClass());`
- `private static final Logger log = LoggerFactory.getLogger(Xxx.class);`
- `@Slf4j` : 롬복 사용 가능

```java
@RestController
public class LogController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        log.trace("trace log={}", name);
        log.debug("trace log={}", name);
        log.info("trace log={}", name);
        log.warn("trace log={}", name);
        log.error("trace log={}", name);

        log.debug("String concat log=" + name);
        return "ok";
    }
}

```

**매핑 정보**
- `@RestController`
  - `@Controller` 는 반환 값이 String 이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 랜더링 된다.
  - `@RestController` 는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다. 따라서 실행 결과로 ok 메세지를 받을 수 있다. 

**테스트**
- 로그가 출력되는 포멧 확인
  - 시간, 로그 레벨, 프로세스 ID, 쓰레드 명, 클래스명, 로그 메시지
- 로그 레벨 설정을 변경해서 출력 결과를 보자.
  - LEVEL: TRACE > DEBUG > INFO > WARN > ERROR
  - 개발 서버는 debug 출력
  - 운영 서버는 info 출력
- `@Slf4j` 로 변경


**로그 레벨 설정**

`application.properties`

```properties
#전체 로그 레벨 설정(기본 info)
logging.level.root=info

#hello.springmvc 패키지와 그 하위 로그 레벨 설정
#logging.level.hello.springmvc=debug
```
- trace로 설정하면 모든 레벨의 로그를 볼 수 있다.

**올바른 로그 사용법**
- `log.debug("data="+data)`
  - 로그 출력 레벨을 info로 설정해도 해당 코드에 있는 `"data="+data`가 실제 실행이 되어 버린다. 결과적으로 문자 더하기 연산이 발생한다.
- `log.debug("data={}", data)`
  - 로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않는다. 따라서 앞과 같은 의미없는 연산이 발생하지 않는다.

**로그 사용시 장점**
- 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
- **애플리케이션 코드를 건드리지 않고 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그를 상황에 맞게 조절할 수 있다.**
- **시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있다.**
- 특히 파일로 남길 때는 일별, 특정 용량에 따라 로그를 분할하는 것도 가능하다.
- 성능도 일반 System.out보다 좋다. (내부 버퍼링, 멀티 쓰레드 등등) 그래서 실무에서는 꼭 로그를 사용해야 한다.

### 요청 매핑

**MappingController**

```java
@Slf4j
@RestController
public class MappingController {

    /**
     * 기본 요청
     * 둘다 허용 /hello-basic, /hello-basic/
     * HTTP 메서드 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping("/hello-basic")
    public String helloBasic() {
        log.info("helloBasic");
        return "ok";
    }
}
```

- `@RequestMapping("/hello-basic")`
  - `/hello-basic` URL 호출이 오면 이 메서드가 실행되도록 매핑한다.
  - 대부분의 속성을 배열[] 로 제공하므로 다중 설정이 가능하다. `{"/hello-basic", "/hello-go"}`

**둘다 허용**
다음 두가지 요청은 다른 URL이지만, 스프링은 다음 URL 요청들을 같은 요청으로 매핑한다.
- 매핑: `/hello-basic`
- URL 요청: `/hello-basic` , `/hello-basic/`

**HTTP 메서드**

`@RequestMapping` 에 `method` 속성으로 HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게 호출된다.
모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE


**HTTP 메서드 매핑**

```java
@Slf4j
@RestController
public class MappingController {

    ...

    /**
     * method 특정 HTTP 메서드 요청만 허용
     * GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
        log.info("mappingGetV1");
        return "ok";
    }
}

```
- 만약 여기에 POST 요청을 하면 스프링 MVC는 HTTP 405 상태코드(Method Not Allowed)를 반환한다.

**HTTP 메서드 매핑 축약**

```java
@Slf4j
@RestController
public class MappingController {

    ...

    /**
     * 편리한 축약 애노테이션 (코드보기)
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        log.info("mapping-get-v2");
        return "ok";
    }
}
```
- HTTP 메서드를 축약한 애노테이션을 사용하는 것이 더 직관적이다. 코드를 보면 내부에서 `@RequestMapping` 과 `method` 를 지정해서 사용하는 것을 확인할 수 있다.

**PathVariable(경로 변수) 사용**

```java
@Slf4j
@RestController
public class MappingController {

     ...

    /**
     * PathVariable 사용
     * 변수명이 같으면 생략 가능
     *
     * @PathVariable("userId") String userId -> @PathVariable userId
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data) {
        log.info("mappingPath={}", data);
        return "ok";
    }

}
```


최근 HTTP API는 다음과 같이 리소스 경로에 식별자를 넣는 스타일을 선호한다.
- `/mapping/userA`
- `/users/1`

- `@RequestMapping` 은 URL 경로를 템플릿화(/mapping/{userId}) 할 수 있는데, `@PathVariable` 을 사용하면 매칭 되는 부분을 편리하게 조회할 수 있다.
- `@PathVariable` 의 이름과 파라미터 이름이 같으면 생략할 수 있다.


**PathVariable 사용 - 다중**

```java
@RestController
public class MappingController {

     ...

    /**
     * PathVariable 사용 다중
     */
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable("userId") String userId, @PathVariable("orderId") String orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }

}
```

**특정 파라미터 조건 매핑**

```java
@RestController
public class MappingController {

     ...

    /**
     * 파라미터로 추가 매핑
     * params="mode",
     * params="!mode"
     * params="mode=debug"
     * params="mode!=debug" (! = )
     * params = {"mode=debug","data=good"}
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }

}
```

- `http://127.0.0.1:8080/mapping-param?mode=debug`
- 파라미터에 반드시 `mode=debug` 가 존재해야지 호출된다.
- 특정 파라미터가 있거나 없는 조건을 추가할 수 있다. 잘 사용하지는 않는다.


**특정 헤더 조건 매핑**

```java
@RestController
public class MappingController {

     ...

    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",
     * headers="!mode"
     * headers="mode=debug"
     * headers="mode!=debug" (! = )
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }


}
```

- header에 반드시 `mode=debug` 가 존재해야한다.


**미디어 타입 조건 매핑 - HTTP 요청 Content-Type, consume**

```java
@RestController
public class MappingController {

     ...

    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * MediaType.APPLICATION_JSON_VALUE
     */
    @PostMapping(value = "/mapping-consume", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }


}
```

- HTTP 요청의 Content-Type 헤더를 기반으로 미디어 타입으로 매핑한다.
- 만약 맞지 않으면 HTTP 415 상태코드(Unsupported Media Type)을 반환한다.

- 예시 `consumes`
```text
consumes = "text/plain"
consumes = {"text/plain", "application/*"}
consumes = MediaType.TEXT_PLAIN_VALUE
```

**미디어 타입 조건 매핑 - HTTP 요청 Accept, produce**

```java
@RestController
public class MappingController {

     ...

    /**
     * Accept 헤더 기반 Media Type
     * produces = "text/html"
     * produces = "!text/html"
     * produces = "text/*"
     * produces = "*\/*"
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }


}
```
- HTTP 요청의 Accept 헤더를 기반으로 미디어 타입으로 매핑한다.
- 만약 맞지 않으면 HTTP 406 상태코드(Not Acceptable)을 반환한다.

- 예시 `produces`

```text
produces = "text/plain"
produces = {"text/plain", "application/*"}
produces = MediaType.TEXT_PLAIN_VALUE
produces = "text/plain;charset=UTF-8"
```


![image](https://user-images.githubusercontent.com/83503188/201664825-391870d0-c159-4e76-9d53-332709b50154.png)

- 클라이언트에서 위와 같이 보내면 "반드시 application/json만 처리할 수 있어" 라는 의미이다.
- 하지만 서버에서는 produce를 통해 text/plain을 생성한다고 처리하였으므로 요청이 거절되는 것

### 요청 매핑 - API 예시

**회원 관리 API**
- 회원 목록 조회: GET `/users`
- 회원 등록: POST `/users`
- 회원 조회: GET `/users/{userId}`
- 회원 수정: PATCH `/users/{userId}`
- 회원 삭제: DELETE `/users/{userId}`

**MappingClassController**

```java
@RestController
@RequestMapping("/mapping/users")
public class MappingClassController {

    @GetMapping
    public String users() {
        return "get users";
    }

    @PostMapping
    public String addUser() {
        return "post user";
    }

    @GetMapping("/{userId}")
    public String findUser(@PathVariable String userId) {
        return "get userId=" + userId;
    }

    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable String userId) {
        return "update userId=" + userId;
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId) {
        return "delete userId=" + userId;
    }


}

```

### HTTP 요청 - 기본, 헤더 조회

**RequestHeaderController**

```java
@Slf4j
@RestController
public class RequestHeaderController {

    @RequestMapping("/headers")
    public String headers(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod, Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap, @RequestHeader("host") String host, @CookieValue(value = "myCookie", required = false) String cookie) {

        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);

        return "ok";
    }
}
```


- `HttpServletRequest`
- `HttpServletResponse`
- `HttpMethod` : HTTP 메서드를 조회한다. `org.springframework.http.HttpMethod`
- `Locale` : Locale 정보를 조회한다.
- `@RequestHeader MultiValueMap<String, String> headerMap`
  - 모든 HTTP 헤더를 MultiValueMap 형식으로 조회한다.
- `@RequestHeader("host") String host`
  - 특정 HTTP 헤더를 조회한다.
  - 속성
    - 필수 값 여부: `required`
    - 기본 값 속성: `defaultValue`
  - `@CookieValue(value = "myCookie", required = false) String cookie`
    - 특정 쿠키를 조회한다.
    - 속성
      - 필수 값 여부: `required`
      - 기본 값: `defaultValue`


`MultiValueMap`
- Map과 유사한데, 하나의 키에 여러 값을 받을 수 있다.
- HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다.
  - `keyA=value1&keyA=value2`


**결과**

```text
2022-11-14 22:12:08.359  INFO 9028 --- [nio-8080-exec-7] h.s.b.request.RequestHeaderController    : request=org.apache.catalina.connector.RequestFacade@3b997aef
2022-11-14 22:12:08.359  INFO 9028 --- [nio-8080-exec-7] h.s.b.request.RequestHeaderController    : response=org.apache.catalina.connector.ResponseFacade@355e93ec
2022-11-14 22:12:08.359  INFO 9028 --- [nio-8080-exec-7] h.s.b.request.RequestHeaderController    : httpMethod=POST
2022-11-14 22:12:08.359  INFO 9028 --- [nio-8080-exec-7] h.s.b.request.RequestHeaderController    : locale=ko_KR
2022-11-14 22:12:08.359  INFO 9028 --- [nio-8080-exec-7] h.s.b.request.RequestHeaderController    : headerMap={accept=[application/json], cookie=[myCookie=yoon], user-agent=[PostmanRuntime/7.29.2], host=[127.0.0.1:8080], accept-encoding=[gzip, deflate, br], connection=[keep-alive], content-length=[0]}
2022-11-14 22:12:08.359  INFO 9028 --- [nio-8080-exec-7] h.s.b.request.RequestHeaderController    : header host=127.0.0.1:8080
2022-11-14 22:12:08.359  INFO 9028 --- [nio-8080-exec-7] h.s.b.request.RequestHeaderController    : myCookie=yoon
```

### HTTP 요청 파라미터 - 쿼리 파라미터, HTML Form

클라이언트에서 서버로 요청 데이터를 전달할 때는 주로 다음 3가지 방법을 사용한다.
- GET - 쿼리 파라미터
  - /url?username=hello&age=20
  - 메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
  - 예) 검색, 필터, 페이징등에서 많이 사용하는 방식
- POST - HTML Form
  - content-type: application/x-www-form-urlencoded
  - 메시지 바디에 쿼리 파리미터 형식으로 전달 username=hello&age=20
  - 예) 회원 가입, 상품 주문, HTML Form 사용
- HTTP message body에 데이터를 직접 담아서 요청
  - HTTP API에서 주로 사용, JSON, XML, TEXT
  - 데이터 형식은 주로 JSON 사용
  - POST, PUT, PATCH

### 요청 파라미터 - 쿼리 파라미터, HTML Form

HttpServletRequest 의 `request.getParameter()` 를 사용하면 다음 두가지 요청 파라미터를 조회할 수 있다.


**GET, 쿼리 파라미터 전송**

예시

`http://localhost:8080/request-param?username=hello&age=20`

**POST, HTML Form 전송**

예시

```text
POST /request-param ...
content-type: application/x-www-form-urlencoded

username=hello&age=20
```

GET 쿼리 파리미터 전송 방식이든, POST HTML Form 전송 방식이든 둘다 형식이 같으므로 구분없이 조회할 수 있다.

이것을 간단히 요청 파라미터(request parameter) 조회라 한다.


**RequestParamController**

```java
@Slf4j
@Controller
public class RequestParamController {

    /**
     * 반환 타입이 없으면서 이렇게 응답에 값을 직접 집어넣으면, view 조회X
     */
    @RequestMapping("/request-param-v1")
    public void requestParam(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username={}, age={}", username, age);
        
        response.getWriter().write("ok");
    }

}
```

**Post Form 페이지 생성**

`main/resources/static/basic/hello-form.html`

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form action="/request-param-v1" method="post">
    username: <input type="text" name="username" />
    age: <input type="text" name="age" />
    <button type="submit">전송</button>
</form>
</body>
</html>
```

> 참고
>
> Jar 를 사용하면 webapp 경로를 사용할 수 없다. 이제부터 정적 리소스도 클래스 경로에 함께 포함해야 한다.

### HTTP 요청 파라미터 - @RequestParam

**requestParamV2**

```java
@Slf4j
@Controller
public class RequestParamController {

    ...

    /**
     * @RequestParam 사용
     * - 파라미터 이름으로 바인딩
     * @ResponseBody 추가
     * - View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력
     */
    @ResponseBody
    @RequestMapping("/request-param-v2")
    public String requestParamV2(
            @RequestParam("username") String memberName,
            @RequestParam("age") int memberAge) {

        log.info("username={}, age={}", memberName, memberAge);
        return "ok";
    }
}
```

- `@RequestParam` : 파라미터 이름으로 바인딩
- `@ResponseBody` : View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력

**`@RequestParam`의 name(value) 속성이 파라미터 이름으로 사용**
- `@RequestParam("username") String memberName` == `request.getParameter("username")`

**requestParamV3**

```java
@Slf4j
@Controller
public class RequestParamController {

    ...

    /**
     * @RequestParam 사용
     * HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능
     */
    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamV3(
            @RequestParam String username,
            @RequestParam int age
    ) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }
}
```
- HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능

**requestParamV4**

```java
@Slf4j
@Controller
public class RequestParamController {

    ...

    /**
     * @RequestParam 사용
     * String, int 등의 단순 타입이면 @RequestParam 도 생략 가능
     */
    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamV4(
            String username,
            int age
    ) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }
}
```
- String , int , Integer 등의 단순 타입이면 `@RequestParam` 도 생략 가능

> 주의
>
> @RequestParam 애노테이션을 생략하면 스프링 MVC는 내부에서 required=false 를 적용한다.


**파라미터 필수 여부 - requestParamRequired**

```java
@Slf4j
@Controller
public class RequestParamController {

    ...

    /**
     * @RequestParam.required /request-param-required -> username이 없으므로 예외
     * <p>
     * 주의!
     * /request-param-required?username= -> 빈문자로 통과
     * <p>
     * 주의!
     * /request-param-required
     * int age -> null을 int에 입력하는 것은 불가능, 따라서 Integer 변경해야 함(또는 다음에 나오는 defaultValue 사용)
     */
    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) int age
    ) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }
}
```

- `@RequestParam.required`
  - 파라미터 필수 여부
  - 기본값이 파라미터 필수( true )이다.
- `/request-param` 요청
  - `username` 이 없으므로 400 예외가 발생한다.

**주의! - 파라미터 이름만 사용**

`/request-param?username=`
- 파라미터 이름만 있고 값이 없는 경우 -> 빈문자로 통과


**주의! - 기본형(primitive)에 null 입력**

`/request-param?username=yoon` 요청 시 500 에러
- why? int age = null이 되는데 기본형 타입은 null을 받을 수 없기 때문이다. 

null 을 int 에 입력하는 것은 불가능(500 예외 발생)

따라서 null 을 받을 수 있는 Integer 로 변경하거나, 또는 다음에 나오는 defaultValue 사용

**기본 값 적용 - requestParamDefault**

```java
@Slf4j
@Controller
public class RequestParamController {

    ...

    /**
     * @RequestParam - defaultValue 사용
     * <p>
     * 참고: defaultValue는 빈 문자의 경우에도 적용
     * /request-param-default?username=
     */
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username,
            @RequestParam(required = false, defaultValue = "-1") int age
    ) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }
}
```

- 파라미터에 값이 없는 경우 `defaultValue` 를 사용하면 기본 값을 적용할 수 있다.
- 이미 기본 값이 있기 때문에 `required` 는 의미가 없다.
  - `@RequestParam(required = true, defaultValue = "guest")` == `@RequestParam(defaultValue = "guest")`


`defaultValue` 는 빈 문자의 경우에도 설정한 기본 값이 적용된다.
- `/request-param-default?username=`


**파라미터를 Map으로 조회하기 - requestParamMap**

```java
@Slf4j
@Controller
public class RequestParamController {

    ...

    /**
     * @RequestParam Map, MultiValueMap
     * Map(key=value)
     * MultiValueMap(key=[value1, value2, ...]) ex) (key=userIds, value=[id1, id2])
     */
    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
        log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
        return "ok";
    }
}
```

파라미터를 Map, MultiValueMap으로 조회할 수 있다.

- `@RequestParam Map ,`
  - `Map(key=value)`
- `@RequestParam MultiValueMap`
  - `MultiValueMap(key=[value1, value2, ...] ex) (key=userIds, value=[id1, id2])`

### HTTP 요청 파라미터 - @ModelAttribute

실제 개발을 하면 요청 파라미터를 받아서 필요한 객체를 만들고 그 객체에 값을 넣어주어야 한다. 보통 다음과 같이 코드를 작성할 것이다.

```java
@RequestParam String username;
@RequestParam int age;

HelloData data = new HelloData();
data.setUsername(username);
data.setAge(age);
```

- 스프링은 이 과정을 완전히 자동화해주는 `@ModelAttribute` 기능을 제공한다.


**HelloData**

```java
@Data
public class HelloData {
    private String username;
    private int age;
}
```

**@ModelAttribute 적용 - modelAttributeV1**

```java
@Slf4j
@Controller
public class RequestParamController {

    ...

    /**
     * @ModelAttribute 사용
     * 참고: model.addAttribute(helloData) 코드도 함께 자동 적용됨, 뒤에 model을 설명할 때 자세히 설명
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeV1(@ModelAttribute HelloData helloData) {

        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }
}
```

스프링MVC는 `@ModelAttribute` 가 있으면 다음을 실행한다.
- HelloData 객체를 생성한다.
- 요청 파라미터의 이름으로 HelloData 객체의 프로퍼티를 찾는다. 그리고 해당 프로퍼티의 setter를 호출해서 파라미터의 값을 입력(바인딩) 한다.
- 예) 파라미터 이름이 username 이면 setUsername() 메서드를 찾아서 호출하면서 값을 입력한다.

String 을 입력하지 않으면 null, int 를 입력하지 않으면 0이 default로 들어간다.

**바인딩 오류**

`age=abc` 처럼 숫자가 들어가야 할 곳에 문자를 넣으면 BindException 이 발생한다. 


**@ModelAttribute 생략 - modelAttributeV2**

```java
@Slf4j
@Controller
public class RequestParamController {
    ...

    /**
     * @ModelAttribute 생략 가능
     * String, int 같은 단순 타입 = @RequestParam
     * argument resolver 로 지정해둔 타입 외 = @ModelAttribute
     */
    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }

}
```

스프링은 해당 생략시 다음과 같은 규칙을 적용한다.
- String , int , Integer 같은 단순 타입 = `@RequestParam`
- 나머지 = @ModelAttribute (argument resolver 로 지정해둔 타입 외)

argument resolver 로 지정해둔 타입 -> HttpServletRequest, HttpServletResponse, ... 

### HTTP 요청 메시지 - 단순 텍스트

요청 파라미터와 다르게, HTTP 메시지 바디를 통해 데이터가 직접 넘어오는 경우는 @RequestParam, @ModelAttribute 를 사용할 수 없다. (물론 HTML Form 형식으로 전달되는 경우는 요청 파라미터로 인정된다.)

```java
@Slf4j
@Controller
public class RequestBodyStringController {

    @PostMapping("/request-body-string-v1")
    public void requestBodyString(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);
        response.getWriter().write("ok");

    }
}

```

**Input, Output 스트림, Reader - requestBodyStringV2**

```java
@Slf4j
@Controller
public class RequestBodyStringController {

    ...

    /**
     * InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
     * OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
     */
    @PostMapping("/request-body-string-v2")
    public void requestBodyStringV2(
            InputStream inputStream, Writer responseWriter
    ) throws IOException {

        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);
        responseWriter.write("ok");

    }
}
```

**스프링 MVC는 다음 파라미터를 지원한다.**
- InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
- OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력

**HttpEntity - requestBodyStringV3**

```java
@Slf4j
@Controller
public class RequestBodyStringController {

    ...

    /**
     * HttpEntity: HTTP header, body 정보를 편리하게 조회
     * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     * <p>
     * 응답에서도 HttpEntity 사용 가능
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     */
    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringV3(
            HttpEntity<String> httpEntity
    ) {
        String messageBody = httpEntity.getBody();

        log.info("messageBody={}", messageBody);
        return new HttpEntity<>("ok");

    }
}
```

**스프링 MVC는 다음 파라미터를 지원한다.**
- **HttpEntity**: HTTP header, body 정보를 편리하게 조회
  - 메시지 바디 정보를 직접 조회
  - 요청 파라미터를 조회하는 기능과 관계 없음 `@RequestParam` X, `@ModelAttribute` X
- **HttpEntity 는 응답에도 사용 가능**
  - 메시지 바디 정보 직접 반환
  - 헤더 정보 포함 가능
  - view 조회X

HttpEntity 를 상속받은 다음 객체들도 같은 기능을 제공한다.
- **RequestEntity**
  - HttpMethod, url 정보가 추가, 요청에서 사용
- **ResponseEntity**
  - HTTP 상태 코드 설정 가능, 응답에서 사용
  - `return new ResponseEntity<String>("Hello World", responseHeaders, HttpStatus.CREATED)`

> 참고
> 
> 스프링MVC 내부에서 HTTP 메시지 바디를 읽어서 문자나 객체로 변환해서 전달해주는데, 이때 HTTP 메시지 컨버터( HttpMessageConverter )라는 기능을 사용한다.

**@RequestBody - requestBodyStringV4**

```java
@Slf4j
@Controller
public class RequestBodyStringController {

    ...

    /**
     * @RequestBody
     * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     *
     * @ResponseBody
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     */
    @ResponseBody
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringV4(@RequestBody String messageBody) {
        log.info("messageBody={}", messageBody);
        return "ok4";
    }
}
```

**@RequestBody**

`@RequestBody` 를 사용하면 HTTP 메시지 바디 정보를 편리하게 조회할 수 있다. 참고로 헤더 정보가 필요하다면 HttpEntity 를 사용하거나 @RequestHeader 를 사용하면 된다.
이렇게 메시지 바디를 직접 조회하는 기능은 요청 파라미터를 조회하는 `@RequestParam`, `@ModelAttribute` 와는 전혀 관계가 없다.

**요청 파라미터 vs HTTP 메시지 바디**
- 요청 파라미터를 조회하는 기능: `@RequestParam` , `@ModelAttribute`
- HTTP 메시지 바디를 직접 조회하는 기능: `@RequestBody`

**@ResponseBody**

`@ResponseBody` 를 사용하면 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달할 수 있다.

### HTTP 요청 메시지 - JSON

**RequestBodyJsonController**

```java
@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);
        HelloData data = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        response.getWriter().write("ok1");
    }

}
```

- HttpServletRequest를 사용해서 직접 HTTP 메시지 바디에서 데이터를 읽어와서, 문자로 변환한다.
- 문자로 된 JSON 데이터를 Jackson 라이브러리인 objectMapper 를 사용해서 자바 객체로 변환한다.

**requestBodyJsonV2 - @RequestBody 문자 변환**

```java
@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper();

    ...

    /**
     * @RequestBody HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     * @ResponseBody - 모든 메서드에 @ResponseBody 적용
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     */
    @ResponseBody
    @PostMapping("/request-body-json-v2")
    public String requestBodyJsonV2(@RequestBody String messageBody) throws JsonProcessingException {
        HelloData data = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        return "ok2";
    }

}
```

- 이전에 학습했던 `@RequestBody` 를 사용해서 HTTP 메시지에서 데이터를 꺼내고 messageBody에 저장한다.
- 문자로 된 JSON 데이터인 messageBody 를 objectMapper 를 통해서 자바 객체로 변환한다.


**requestBodyJsonV3 - @RequestBody 객체 변환**

```java
@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper();

    ...

    /**
     * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
     * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
     */
    @ResponseBody
    @PostMapping("/request-body-json-v3")
    public String requestBodyJsonV3(@RequestBody HelloData data) {
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        return "ok3";
    }

}
```
**@RequestBody 객체 파라미터**
- `@RequestBody HelloData data`
- `@RequestBody` 에 직접 만든 객체를 지정할 수 있다.

`HttpEntity` , `@RequestBody` 를 사용하면 HTTP 메시지 컨버터가 HTTP 메시지 바디의 내용을 우리가 원하는 문자나 객체 등으로 변환해준다. -> `HttpEntity<HelloData>` == `@RequestBody HelloData`
HTTP 메시지 컨버터는 문자 뿐만 아니라 JSON도 객체로 변환해주는데, 우리가 방금 V2에서 했던 작업을 대신 처리해준다.


**@RequestBody는 생략 불가능**

스프링은 `@ModelAttribute` , `@RequestParam` 과 같은 해당 애노테이션을 생략시 다음과 같은 규칙을 적용한다.
- String , int , Integer 같은 단순 타입 = `@RequestParam`
- 나머지 = `@ModelAttribute` (argument resolver 로 지정해둔 타입 외)

따라서 이 경우 HelloData에 `@RequestBody` 를 생략하면 `@ModelAttribute` 가 적용되어버린다.

`HelloData data` -> `@ModelAttribute HelloData data`

따라서 생략하면 HTTP 메시지 바디가 아니라 요청 파라미터를 처리하게 된다.

> 주의
>
> HTTP 요청시에 content-type이 application/json인지 꼭! 확인해야 한다. 그래야 JSON을 처리할 수 있는 HTTP 메시지 컨버터가 실행된다.


**requestBodyJsonV4**

```java
@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper();

    ...

     /**
     * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
     * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
     *
     * @ResponseBody 적용
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 적용 (Accept: application/json)
     */
    @ResponseBody
    @PostMapping("/request-body-json-v4")
    public HelloData requestBodyJsonV4(@RequestBody HelloData data) {
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        return data;
    }

}
```

**@ResponseBody**

응답의 경우에도 `@ResponseBody` 를 사용하면 해당 객체를 HTTP 메시지 바디에 직접 넣어줄 수 있다.
물론 이 경우에도 `HttpEntity` 를 사용해도 된다.

- `@RequestBody` 요청
  - JSON 요청 -> HTTP 메시지 컨버터 -> 객체
- `@ResponseBody` 응답
  - 객체 -> HTTP 메시지 컨버터 -> JSON 응답

클라이언트의 요청 메시지 header에 Accept가 application/json이기 때문에 서버에서 `MappingJackson2HttpMessageConverter` 를 사용하게된다.

### HTTP 응답 - 정적 리소스, 뷰 템플릿

스프링(서버)에서 응답 데이터를 만드는 방법은 크게 3가지이다.

- 정적 리소스
  - 예) 웹 브라우저에 정적인 HTML, css, js를 제공할 때는, 정적 리소스를 사용한다.
- 뷰 템플릿 사용
  - 예) 웹 브라우저에 동적인 HTML을 제공할 때는 뷰 템플릿을 사용한다.
- HTTP 메시지 사용
  - HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로, HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보낸다.

**정적 리소스**

스프링 부트는 클래스패스의 다음 디렉토리에 있는 정적 리소스를 제공한다.
`/static , /public , /resources , /META-INF/resources`

`src/main/resources` 는 리소스를 보관하는 곳이고, 또 클래스패스의 시작 경로이다.

따라서 다음 디렉토리에 리소스를 넣어두면 스프링 부트가 정적 리소스로 서비스를 제공한다.

**정적 리소스 경로**

`src/main/resources/static`

다음 경로에 파일이 들어있으면
- `src/main/resources/static/basic/hello-form.html`

웹 브라우저에서 다음과 같이 실행하면 된다.
- `http://localhost:8080/basic/hello-form.html`

정적 리소스는 해당 파일을 변경 없이 그대로 서비스하는 것이다.

**뷰 템플릿**

뷰 템플릿을 거쳐서 HTML이 생성되고, 뷰가 응답을 만들어서 전달한다.
일반적으로 HTML을 동적으로 생성하는 용도로 사용하지만, 다른 것들도 가능하다. 뷰 템플릿이 만들 수 있는 것이라면 뭐든지 가능하다.

스프링 부트는 기본 뷰 템플릿 경로를 제공한다.

뷰 템플릿 경로
- `src/main/resources/templates`

뷰 템플릿 생성
- `src/main/resources/templates/response/hello.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<p th:text="${data}">empty</p>
</body>
</html>
```

**ResponseViewController - 뷰 템플릿을 호출하는 컨트롤러**

```java
@Controller
public class ResponseViewController {

    @RequestMapping("response-view-v1")
    public ModelAndView responseViewV1() {
        ModelAndView mav = new ModelAndView("response/hello")
                .addObject("data", "hello!");
        return mav;
    }

    @RequestMapping("response-view-v2")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "hello2!");
        return "response/hello";
    }

}

```

**String을 반환하는 경우 - View or HTTP 메시지**

`@ResponseBody` 가 없으면 `response/hello` 로 뷰 리졸버가 실행되어서 뷰를 찾고, 렌더링 한다.
`@ResponseBody` 가 있으면 뷰 리졸버를 실행하지 않고, HTTP 메시지 바디에 직접 `response/hello` 라는 문자가 입력된다.

여기서는 뷰의 논리 이름인 `response/hello` 를 반환하면 다음 경로의 뷰 템플릿이 렌더링 되는 것을 확인할 수 있다.
- 실행: `templates/response/hello.html`

**Void를 반환하는 경우**
- `@Controller` 를 사용하고, `HttpServletResponse` , `OutputStream(Writer)` 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 요청 URL을 참고해서 논리 뷰 이름으로 사용
  - 요청 URL: `/response/hello`
  - 실행: `templates/response/hello.html`
- 참고로 이 방식은 명시성이 너무 떨어지고 이렇게 딱 맞는 경우도 많이 없어서, 권장하지 않는다.

**HTTP 메시지**

`@ResponseBody` , `HttpEntity` 를 사용하면, 뷰 템플릿을 사용하는 것이 아니라, HTTP 메시지 바디에 직접 응답 데이터를 출력할 수 있다.

### HTTP 응답 - HTTP API, 메시지 바디에 직접 입력


HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로, HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보낸다.

**ResponseBodyController**

```java

@Slf4j
@Controller
public class ResponseBodyController {

    @GetMapping("/response-body-string-v1")
    public void responseBodyV1(HttpServletResponse response) throws IOException {
        response.getWriter().write("ok");
    }

    /**
     * HttpEntity, ResponseEntity(Http Status 추가)
     *
     * @return
     */
    @GetMapping("/response-body-string-v2")
    public ResponseEntity<String> responseBodyV2() {
        return ResponseEntity.ok("ok");
    }

    @ResponseBody
    @GetMapping("/response-body-string-v3")
    public String responseBodyV3() {
        return "ok";
    }

    @GetMapping("/response-body-json-v1")
    public ResponseEntity<HelloData> responseBodyJsonV1() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);

        return ResponseEntity.ok(helloData);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/response-body-json-v2")
    public HelloData responseBodyJsonV2() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);
        
        return helloData;
    }
}

```


**responseBodyJsonV2**

`ResponseEntity` 는 HTTP 응답 코드를 설정할 수 있는데, `@ResponseBody` 를 사용하면 이런 것을 설정하기 까다롭다.

`@ResponseStatus(HttpStatus.OK)` 애노테이션을 사용하면 응답 코드도 설정할 수 있다.

물론 애노테이션이기 때문에 응답 코드를 동적으로 변경할 수는 없다. 프로그램 조건에 따라서 동적으로 변경하려면 `ResponseEntity` 를 사용하면 된다.

**@RestController**

`@Controller` 대신에 `@RestController` 애노테이션을 사용하면, 해당 컨트롤러에 모두 `@ResponseBody` 가 적용되는 효과가 있다.
따라서 뷰 템플릿을 사용하는 것이 아니라, HTTP 메시지 바디에 직접 데이터를 입력한다.
이름 그대로 Rest API(HTTP API)를 만들 때 사용하는 컨트롤러이다.

참고로 `@ResponseBody` 는 클래스 레벨에 두면 전체 메서드에 적용되는데, `@RestController` 에노테이션 안에 `@ResponseBody` 가 적용되어 있다.

### HTTP 메시지 컨버터

뷰 템플릿으로 HTML을 생성해서 응답하는 것이 아니라, HTTP API처럼 JSON 데이터를 HTTP 메시지 바디에서 직접 읽거나 쓰는 경우 HTTP 메시지 컨버터를 사용하면 편리하다.

**@ResponseBody 사용 원리**

![image](https://user-images.githubusercontent.com/83503188/202188049-611163f9-807e-477b-af9e-c6c3aa8010fe.png)

- `@ResponseBody` 를 사용
  - HTTP의 BODY에 문자 내용을 직접 반환
  - `viewResolver` 대신에 `HttpMessageConverter` 가 동작
  - 기본 문자처리: `StringHttpMessageConverter`
  - 기본 객체처리: `MappingJackson2HttpMessageConverter`
  - byte 처리 등등 기타 여러 `HttpMessageConverter` 가 기본으로 등록되어 있음

> 참고
> 
> 응답의 경우 클라이언트의 HTTP Accept 해더와 서버의 컨트롤러 반환 타입 정보 둘을 조합해서 HttpMessageConverter 가 선택된다. 

스프링 MVC는 다음의 경우에 HTTP 메시지 컨버터를 적용한다.

- HTTP 요청: `@RequestBody` , `HttpEntity(RequestEntity)`
- HTTP 응답: `@ResponseBody` , `HttpEntity(ResponseEntity)`


**HTTP 메시지 컨버터 인터페이스**

`org.springframework.http.converter.HttpMessageConverter`

```java
public interface HttpMessageConverter<T> {

	boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);

	boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);

	T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException;

	void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}
```

HTTP 메시지 컨버터는 HTTP 요청, HTTP 응답 둘 다 사용된다.
- `canRead()` , `canWrite()` : 메시지 컨버터가 해당 클래스, 미디어타입을 지원하는지 체크
- `read()` , `write()` : 메시지 컨버터를 통해서 메시지를 읽고 쓰는 기능

**스프링 부트 기본 메시지 컨버터(일부 생략)**

```text
0 = ByteArrayHttpMessageConverter
1 = StringHttpMessageConverter
2 = MappingJackson2HttpMessageConverter
```

스프링 부트는 다양한 메시지 컨버터를 제공하는데, 대상 클래스 타입과 미디어 타입(Content-Type) 둘을 체크해서 사용여부를 결정한다.
만약 만족하지 않으면 다음 메시지 컨버터로 우선순위가 넘어간다.

몇가지 주요한 메시지 컨버터를 알아보자.
- `ByteArrayHttpMessageConverter` : byte[] 데이터를 처리한다.
  - 클래스 타입: byte[] , 미디어타입: */* ,
  - 요청 예) `@RequestBody byte[] data`
  - 응답 예) `@ResponseBody return byte[]` 쓰기 미디어타입 application/octet-stream
- `StringHttpMessageConverter` : String 문자로 데이터를 처리한다.
  - 클래스 타입: String , 미디어타입: */*
  - 요청 예) `@RequestBody String data`
  - 응답 예) `@ResponseBody return "ok"` 쓰기 미디어타입 text/plain
- `MappingJackson2HttpMessageConverter` : application/json
  - 클래스 타입: 객체 또는 HashMap , 미디어타입 application/json 관련
  - 요청 예) `@RequestBody HelloData data`
  - 응답 예) `@ResponseBody return helloData` 쓰기 미디어타입 application/json 관련

**예제 1**

```text
content-type: application/json

@RequestMapping
void hello(@RequestBody String data) {}
```
- StringHttpMessageConverter 사용

**예제 2**

```text
content-type: application/json

@RequestMapping
void hello(@RequestBody HelloData data) {}
```
- MappingJackson2HttpMessageConverter 사용

**오류 예제**

```text
content-type: text/html

@RequestMapping
void hello(@RequestBody HelloData data) {}
```

- 객체 타입은 맞지만 미디어 타입이 application/json 관련이 아니므로 오류 

**HTTP 요청 데이터 읽기**

- HTTP 요청이 오고, 컨트롤러에서 `@RequestBody`, `HttpEntity` 파라미터를 사용한다.
- 우선순위대로 하나씩 확인, 메시지 컨버터가 메시지를 읽을 수 있는지 확인하기 위해 `canRead()` 를 호출한다.
  - 대상 클래스 타입을 지원하는가.
    - 예) `@RequestBody` 의 대상 클래스 ( byte[] , String , HelloData )
  - HTTP 요청의 Content-Type 미디어 타입을 지원하는가.
    - 예) text/plain , application/json , */*
- `canRead()` 조건을 만족하면 `read()` 를 호출해서 객체 생성하고, 반환한다.

**HTTP 응답 데이터 생성**

- 컨트롤러에서 `@ResponseBody` , `HttpEntity` 로 값이 반환된다.
- 메시지 컨버터가 메시지를 쓸 수 있는지 확인하기 위해 `canWrite()` 를 호출한다.
  - 대상 클래스 타입을 지원하는가.
    - 예) return의 대상 클래스 ( byte[] , String , HelloData )
  - HTTP 요청의 Accept 미디어 타입을 지원하는가.(더 정확히는 @RequestMapping 의 produces )
    - 예) text/plain , application/json , */*
- `canWrite()` 조건을 만족하면 `write()` 를 호출해서 HTTP 응답 메시지 바디에 데이터를 생성한다.

### 요청 매핑 헨들러 어뎁터(RequestMappingHandlerAdapter) 구조

그렇다면 HTTP 메시지 컨버터는 스프링 MVC 어디쯤에서 사용되는 것일까? 다음 그림에서는 보이지 않는다.

**SpringMVC 구조**

![image](https://user-images.githubusercontent.com/83503188/202194373-52d19743-ec9f-4124-bba5-8b18a35dff8e.png)

모든 비밀은 애노테이션 기반의 컨트롤러, @RequestMapping 을 처리하는 핸들러 어댑터인 `RequestMappingHandlerAdapter` (요청 매핑 헨들러 어뎁터)에 있다.

**RequestMappingHandlerAdapter 동작 방식**

![image](https://user-images.githubusercontent.com/83503188/202194589-250f0d62-c4f8-4441-a009-855ff5a80d60.png)

**ArgumentResolver**

생각해보면, 애노테이션 기반의 컨트롤러는 매우 다양한 파라미터를 사용할 수 있었다. HttpServletRequest , Model 은 물론이고, `@RequestParam` , `@ModelAttribute` 같은 애노테이션 그리고 `@RequestBody` , `HttpEntity` 같은 HTTP 메시지를 처리하는 부분까지 매우 큰 유연함을 보여주었다.
이렇게 파라미터를 유연하게 처리할 수 있는 이유가 바로 `ArgumentResolver` 덕분이다.

애노테이션 기반 컨트롤러를 처리하는 `RequestMappingHandlerAdapter` 는 바로 이 `ArgumentResolver` 를 호출해서 컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성한다.

정확히는 `HandlerMethodArgumentResolver` 인데 줄여서 `ArgumentResolver` 라고 부른다. 각 예약 파라미터마다 해당 ArgumentResolver 가 존재한다.

```java
public interface HandlerMethodArgumentResolver {

	boolean supportsParameter(MethodParameter parameter);
	
	@Nullable
	Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception;

}
```


**동작 방식**

`ArgumentResolver` 의 `supportsParameter()` 를 호출해서 해당 파라미터를 지원하는지 체크하고, 지원하면 `resolveArgument()` 를 호출해서 실제 객체를 생성한다.
그리고 이렇게 생성된 객체가 컨트롤러 호출시 넘어가는 것이다.

그리고 원한다면 여러분이 직접 이 인터페이스를 확장해서 원하는 `ArgumentResolver` 를 만들 수도 있다.

**ReturnValueHandler**

String 반환?, ModelAndView 반환?, HttpEntity 반환?, ...

`HandlerMethodReturnValueHandler` 를 줄여서 `ReturnValueHandler` 라 부른다.
`ArgumentResolver` 와 비슷한데, 이것은 응답 값을 변환하고 처리한다.

컨트롤러에서 String으로 뷰 이름을 반환해도, 동작하는 이유가 바로 `ReturnValueHandler` 덕분이다.

스프링은 10여개가 넘는 ReturnValueHandler 를 지원한다.
- 예) ModelAndView , @ResponseBody , HttpEntity , String

### HTTP 메시지 컨버터

**HTTP 메시지 컨버터 위치**

![image](https://user-images.githubusercontent.com/83503188/202195586-0f881d92-d596-493e-83e2-df254acda6b1.png)

HTTP 메시지 컨버터는 어디쯤 있을까?

HTTP 메시지 컨버터를 사용하는 `@RequestBody` 도 컨트롤러가 필요로 하는 파라미터의 값에 사용된다.
`@ResponseBody` 의 경우도 컨트롤러의 반환 값을 이용한다.

요청의 경우 `@RequestBody` 를 처리하는 `ArgumentResolver` 가 있고, `HttpEntity` 를 처리하는 `ArgumentResolver` 가 있다.
이 `ArgumentResolver` 들이 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성하는 것이다.

예를 들어, HttpEntity의 경우에

![image](https://user-images.githubusercontent.com/83503188/202200297-11cd40a8-f63b-4ef7-b87f-19de0df4b763.png)

![image](https://user-images.githubusercontent.com/83503188/202200521-f323d8d5-ca51-4299-b12d-3bc96d71a0e1.png)

- 파라미터에 HttpEntity 또는 RequestEntity 가 존재하면 `ArgumentResolver`인 `HttpMethodProcessor`가 동작하고

![image](https://user-images.githubusercontent.com/83503188/202200893-23b687b9-5913-4e4e-b229-a1e8a5ae9248.png)

- `resolveArgument()` 에서 Message Converter 를 이용하여 객체를 만들어서 해당 객체를 `HttpEntity` 에 담아서 반환한다.

응답의 경우 `@ResponseBody` 와 `HttpEntity` 를 처리하는 `ReturnValueHandler` 가 있다.
그리고 여기에서 HTTP 메시지 컨버터를 호출(`write()`)해서 응답 결과를 만든다.

스프링 MVC는 `@RequestBody`, `@ResponseBody` 가 있으면 `RequestResponseBodyMethodProcessor` (ArgumentResolver), `HttpEntity` 가 있으면 `HttpEntityMethodProcessor` (ArgumentResolver)를 사용한다.

**확장**

스프링은 다음을 모두 인터페이스로 제공한다. 따라서 필요하면 언제든지 기능을 확장할 수 있다.
- `HandlerMethodArgumentResolver`
- `HandlerMethodReturnValueHandler`
- `HttpMessageConverter`

스프링이 필요한 대부분의 기능을 제공하기 때문에 실제 기능을 확장할 일이 많지는 않다.
기능 확장은 `WebMvcConfigurer` 를 상속 받아서 스프링 빈으로 등록하면 된다. 

**WebMvcConfigurer 확장**

```java
@Bean
public WebMvcConfigurer webMvcConfigurer() {
	return new WebMvcConfigurer() {
		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		//...
		}

		@Override
		public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		//...
		}
	};
}
```
