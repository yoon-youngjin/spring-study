# API Call Guide

## Vendor 마다 다르게 Bean 등록

```java
@Bean
public RestTemplate localTestTemplate() {
return restTemplateBuilder.rootUri("http://localhost:8899")
    .additionalInterceptors(new RestTemplateClientHttpRequestInterceptor())
    .errorHandler(new RestTemplateErrorHandler())
    .setConnectTimeout(Duration.ofMinutes(3))
    .build();
}


@Bean
public RestTemplate xxxPaymentTemplate() {
return restTemplateBuilder.rootUri("http://xxxx")
    .additionalInterceptors(new RestTemplateClientHttpRequestInterceptor())
    .errorHandler(new RestTemplateErrorHandler())
    .setConnectTimeout(Duration.ofMinutes(3))
    .build();
}
```

RestTemplate를 외부 API 특성에 맞는 Bean을 생성한다. 여기서 중요한 점은 각 API API Vendor사 별로 각각 Bean으로 관리하는 것입니다.

Vendor사 별로 다르게 Bean을 적용하는 이유
1. connection timeout 설정이 각기 다르다.
2. 로깅을 각기 다르게 설정 할 수 있다.
3. 예외 처리가 각기 다르다. 
4. API에 대한 권한 인증이 각기 다르다. 

## Logging

restTemplateBuilder의 `additionalInterceptors()` 메서드를 이용하면 로깅을 쉽게 구현할 수 있고, 특정 Vendor의 Bean에는 더 구체적인 로깅, 그 이외의 작업을 Interceptors을 편리하게 등록할 수 있다.

### Code

```java
@Slf4j
public class RestTemplateClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  @NonNull
  @Override
  public ClientHttpResponse intercept(@NonNull final HttpRequest request,
      @NonNull final byte[] body, final @NonNull ClientHttpRequestExecution execution)
      throws IOException {
    final ClientHttpResponse response = execution.execute(request, body);

    loggingResponse(response);
    loggingRequest(request, body);
    return execution.execute(request, body);
  }
}
```

Request, Response의 Logging을 저장하는 Interceptor 코드, 결제와 같은 중요한 API 호출은 모든 요청과 응답을 모두 로깅 하는 것이 바람직하다. 

상대적으로 덜 중요한 API 호출 같은 경우에는 Interceptor 등록하지 않아도 된다. 이처럼 Vendor 사마다 Bean으로 지정해서 관리하는 것이 효율적

### API Call
```java
public class SampleApi {

private final RestTemplate localTestTemplate;

@PostMapping("/local-sign-up")
public Member test(@RequestBody @Valid final SignUpRequest dto){
final ResponseEntity<Member> responseEntity = localTestTemplate
.postForEntity("/members", dto, Member.class);

    final Member member = responseEntity.getBody();
    return member;
}
}
```

위에서 등록한 localTestTemplate Bean으로 회원가입 API을 호출
![image](https://user-images.githubusercontent.com/83503188/167610524-e3fda96e-c626-4099-a504-fc9be7c10e74.png)

Interceptor를 통해서 요청했던 Request 정보와 응답받은 Response 정보가 모두 정상적으로 로그 되는 것을 확인할 수 있다.

## 예외 처리

외부 API는 Vendor마다 각기 다르기 때문에 통일성 있게 예외 처리를 진행하기 어렵다. 아래는 처리하기 애매한 한 Response

```java
{
  "success": false,
  "result": {
      ....
  }
}
```

RestTemplate는 우선 Http Status Code로 1차적으로 API 유무를 검사한다. 2xxx 이 외의 코드가 넘어오게 되면 RestTemplate 예외를 발생시킨다.

그런데 문제는 2xx http status code를 응답받고 위 JSON 같이 success에 false를 주는 API들이다. 그렇다면 API 호출마다 아래와 같은 코드로 확인해야 한다.

```java
 public Member test(@RequestBody @Valid final SignUpRequest dto){
    final ResponseEntity<Member> responseEntity = localTestTemplate
        .postForEntity("/members", dto, Member.class);

    if(responseEntity.getBody().isSuccess(){
      // 성공...
    }else{
      // 실패...
    }
    ...
  }
```

모든 API 호출 시에 위와 같은 if else 코드가 있다고 생각하면 끔찍하다. 이처럼 Vendor마다 다른 예외 처리를 Interceptor처럼 등록해서 Vendor에 알맞은 errorHandler를 지정할 수 있다.


### code

```java
public class RestTemplateErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(@NonNull final ClientHttpResponse response) throws IOException {
    final HttpStatus statusCode = response.getStatusCode();
//    response.getBody() 넘겨 받은 body 값으로 적절한 예외 상태 확인 이후 boolean return
    return !statusCode.is2xxSuccessful();
  }

  @Override
  public void handleError(@NonNull final ClientHttpResponse response) throws IOException {
//    hasError에서 true를 return하면 해당 메서드 실행.
//    상황에 알맞는 Error handling 로직 작성....
  }
```

Bean을 등록할 때 ResponseErrorHandler 객체를 추가할 수 있다. Response 객체에 `"success": false`를 `hasError()` 메서드에서 확인하고, false가 return 되면 `handleError()` 에서 추가적인 에러 핸들링 작업을 이어 나갈 수 있다. 이렇게 ResponseErrorHandler 등록을 하면 위처럼 반복 적인 if-else 문을 작성하지 않아도 된다.

![image](https://user-images.githubusercontent.com/83503188/167616944-b613deee-8566-4dd5-9bfc-06997f6c39dd.png)

