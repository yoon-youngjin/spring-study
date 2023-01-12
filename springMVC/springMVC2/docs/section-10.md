# 스프링 타입 컨버터

## 스프링 타입 컨버터 소개

문자를 숫자로 변환하거나, 반대로 숫자를 문자로 반환해야 하는 것 처럼 애플리케이션을 개발하다 보면 타입을 변환해야 하는 경우가 상당히 많다.

**HelloController - 문자 타입을 숫자 타입으로 변경**

```java
@RestController
public class HelloController {
    
    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest request) {
        String data = request.getParameter("data");
        Integer intValue = Integer.valueOf(data);
        System.out.println("intValue = " + intValue);
        return "ok";
    }
}
```
- HTTP 요청 파라미터는 모두 문자로 처리된다. 따라서 요청 파라미터를 자바에서 타입으로 변환해서 사용하고 싶으면 숫자 타입으로 변환하는 과정을 거쳐야 한다.

이번에는 스프링 MVC가 제공하는 `@RequestParam` 을 사용해보자.

**HelloController - 추가**

```java
    @GetMapping("/hello-v2")
    public String helloV2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
    }
```
- 스프링이 제공하는 `@RequestParam` 을 사용하면 이 문자 10을 Integer 타입의 숫자 10으로 편리하게 받을 수 있다.

**이것이 가능한 이유는 스프링이 중간에서 타입을 변환해주었기 때문이다.**

이러한 예는 `@ModelAttribute` , `@PathVariable` 에서도 확인할 수 있다.

```java
@ModelAttribute UserData data

class UserData {
    Integer data;
}
```

```java
/users/{userId}
@PathVariable("userId") Integer data
```

**스프링의 타입 변환 적용 예**
- 스프링 MVC 요청 파라미터
  - `@RequestParam` , `@ModelAttribute` , `@PathVariable`
- `@Value`등으로 YML 정보 읽기
- XML에 넣은 스프링 비 정보를 반환
- 뷰를 렌더링 할 때

**스프링과 타입 변환**

이렇게 타입을 변환해야 하는 경우는 상당히 많다. 개발자가 직접 하나하나 타입 변환을 해야 한다면, 생각만 해도 괴로울 것이다.
스프링이 중간에 타입 변환기를 사용해서 타입을 `String` -> `Integer` 로 변환해주었기 때문에 개발자는 편리하게 해당 타입을 바로 받을 수 있다.
앞에서는 문자를 숫자로 변경하는 예시를 들었지만, 반대로 숫자를 문자로 변경하는 것도 가능하고, Boolean 타입을 숫자로 변경하는 것도 가능하다.
만약 개발자가 새로운 타입을 만들어서 변환하고 싶으면 어떻게 하면 될까?

**컨버터 인터페이스**

```java
package org.springframework.core.convert.converter;
public interface Converter<S, T> {
    T convert(S source);
}
```

스프링은 확장 가능한 컨버터 인터페이스를 제공한다.
개발자는 스프링에 추가적인 타입 변환이 필요하면 이 컨버터 인터페이스를 구현해서 등록하면 된다.
이 컨버터 인터페이스는 모든 타입에 적용할 수 있다. 필요하면 X -> Y 타입으로 변환하는 컨버터 인터페이스를 만들고, 또 Y -> X 타입으로 변환하는 컨버터 인터페이스를 만들어서 등록하면 된다.

예를 들어서 문자로 `"true"` 가 오면 `Boolean` 타입으로 받고 싶으면 `String` -> `Boolean` 타입으로 변환되도록 컨버터 인터페이스를 만들어서 등록하고,
반대로 적용하고 싶으면 `Boolean` -> `String` 타입으로 변환되도록 컨버터를 추가로 만들어서 등록하면 된다.

## 타입 컨버터 - Converter

타입 컨버터를 사용하려면 `org.springframework.core.convert.converter.Converter` 인터페이스를 구현하면 된다.

**StringToIntegerConverter - 문자를 숫자로 변환하는 타입 컨버터**

```java
@Slf4j
public class StringToIntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        log.info("convert source={}", source);
        return Integer.valueOf(source);
    }
}
```
- `String` -> `Integer` 로 변환하기 때문에 소스가 `String` 이 된다. 이 문자를 `Integer.valueOf(source)` 를 사용해서 숫자로 변경한 다음에 변경된 숫자를 반환하면 된다.

**IntegerToStringConverter - 숫자를 문자로 변환하는 타입 컨버터**


```java
@Slf4j
public class IntegerToStringConverter implements Converter<Integer, String> {
    @Override
    public String convert(Integer source) {
        log.info("convert source={}", source);
        return String.valueOf(source);
    }
}
```

**ConverterTest - 타입 컨버터 테스트 코드**

```java
public class ConverterTest {

    @Test
    @DisplayName("String -> Integer test")
    void stringToInteger() {

        StringToIntegerConverter converter = new StringToIntegerConverter();
        Integer result = converter.convert("10");
        assertThat(result).isEqualTo(10);
    }

    @Test
    @DisplayName("Integer -> String test")
    void integerToString() {

        IntegerToStringConverter converter = new IntegerToStringConverter();
        String result = converter.convert(10);
        assertThat(result).isEqualTo("10");

    }
}
```

이후에 생성한 컨버터를 스프링에 적용하면 스프링 내에서 자동으로 컨버팅 작업을 해준다.

### 사용자 정의 타입 컨버터

`127.0.0.1:8080` 과 같은 IP, PORT를 입력하면 IpPort 객체로 변환하는 컨버터를 만들어보자.

**IpPort**

```java
@Getter
@EqualsAndHashCode
public class IpPort {

    private String ip;
    private int port;
    
    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
```
- 롬복의 `@EqualsAndHashCode` 를 넣으면 모든 필드를 사용해서 `equals()` , `hashcode()` 를 생성한다. 따라서 모든 필드의 값이 같다면 `a.equals(b)` 의 결과가 참이 된다.

**StringToIpPortConverter - 컨버터**

```java
@Slf4j
public class StringToIpPortConverter implements Converter<String, IpPort> {

    @Override
    public IpPort convert(String source) {
        log.info("convert source ={}", source);
        String[] split = source.split(":");
        String ip = split[0];
        Integer port = Integer.valueOf(split[1]);
        return new IpPort(ip, port);
    }
}
```

**IpPortToStringConverter**r

```java
@Slf4j
public class IpPortToStringConverter implements Converter<IpPort, String> {

    @Override
    public String convert(IpPort source) {
        log.info("convert source={}", source);
        return source.getIp() + ":" + source.getPort();
    }
}

```

**ConverterTest - IpPort 컨버터 테스트 추가**

```java
    @Test
    void stringToIpPort() {
        StringToIpPortConverter converter = new StringToIpPortConverter();
        String source = "127.0.0.1:8080";
        IpPort result = converter.convert(source);
        assertThat(result).isEqualTo(new IpPort("127.0.0.1", 8080));
    }

    @Test
    void ipPortToString() {
        IpPortToStringConverter converter = new IpPortToStringConverter();
        IpPort source = new IpPort("127.0.0.1", 8080);
        String result = converter.convert(source);
        assertThat(result).isEqualTo("127.0.0.1:8080");
    }
```

스프링은 용도에 따라 다양한 방식의 타입 컨버터를 제공한다.
- `Converter`: 기본 타입 컨버터
- `ConverterFactory`: 전체 클래스 계층 구조가 필요할 때
- `GenericConverter`: 정교한 구현, 대상 필드의 애노테이션 정보 사용 가능
- `ConditionalGenericConverter`: 특정 조건이 참인 경우에만 실행

https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#coreconvert

> 참고 
> 
> 스프링은 문자, 숫자, 불린, Enum 등 일반적인 타입에 대한 대부분의 컨버터를 기본으로 제공한다.

## 컨버전 서비스 - ConversionService

이렇게 타입 컨버터를 하나하나 직접 찾아서 타입 변환에 사용하는 것은 매우 불편하다.
그래서 스프링은 개별 컨버터를 모아두고 그것들을 묶어서 편리하게 사용할 수 있는 기능을 제공하는데, 이것이 바로 컨버전 서비스( `ConversionService` )이다.

**ConversionService 인터페이스**

```java
package org.springframework.core.convert;
import org.springframework.lang.Nullable;

public interface ConversionService {
    boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);
    boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

    <T> T convert(@Nullable Object source, Class<T> targetType);
    Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
}
```
컨버전 서비스 인터페이스는 단순히 컨버팅이 가능한지 확인하는 기능과 컨버팅 기능을 제공한다.

**ConversionServiceTest - 컨버전 서비스 테스트 코드**

```java
public class ConversionServiceTest {

    @Test
    void conversionService() {

        // 등록
        DefaultConversionService conversionService = new DefaultConversionService();

        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        // 사용
        assertThat(conversionService.convert("10", Integer.class)).isEqualTo(10);
        assertThat(conversionService.convert(10, String.class)).isEqualTo("10");

        IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));
        
        String ipPortString = conversionService.convert(new IpPort("127.0.0.1", 8080), String.class);
        assertThat(ipPortString).isEqualTo("127.0.0.1:8080");

    }
}
```

**등록과 사용 분리**

컨버터를 등록할 때는 `StringToIntegerConverter` 같은 타입 컨버터를 명확하게 알아야 한다.
반면에 컨버터를 사용하는 입장에서는 타입 컨버터를 전혀 몰라도 된다.
타입 컨버터들은 모두 컨버전 서비스 내부에 숨어서 제공된다.
따라서 타입을 변환을 원하는 사용자는 컨버전 서비스 인터페이스에만 의존하면 된다.
물론 컨버전 서비스를 등록하는 부분과 사용하는 부분을 분리하고 의존관계 주입을 사용해야 한다.

**컨버전 서비스 사용**

`Integer value = conversionService.convert("10", Integer.class)`


**인터페이스 분리 원칙 - ISP(Interface Segregation Principle)**

인터페이스 분리 원칙은 클라이언트가 자신이 이용하지 않는 메서드에 의존하지 않아야 한다.

![image](https://user-images.githubusercontent.com/83503188/211772689-7d080ecd-33c5-4b6b-9310-eede4624b0a0.png)

`DefaultConversionService` 는 다음 두 인터페이스를 구현했다.
- `ConversionService` : 컨버터 사용에 초점
- `ConverterRegistry` : 컨버터 등록에 초점

스프링은 내부에서 `ConversionService` 를 사용해서 타입을 변환한다.
예를 들어서 앞서 살펴본 `@RequestParam` 같은 곳에서 이 기능을 사용해서 타입을 변환한다.


## 스프링에 Converter 적용하기

**WebConfig - 컨버터 등록**

```java
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIntegerConverter());
        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());
    }
}
```

스프링은 내부에서 `ConversionService` 를 제공한다. 우리는 `WebMvcConfigurer` 가 제공하는 `addFormatters()` 를 사용해서 추가하고 싶은 컨버터를 등록하면 된다.
이렇게 하면 스프링은 내부에서 사용하는 `ConversionService` 에 컨버터를 추가해준다.

**HelloController - 기존 코드**

```java
@GetMapping("/hello-v2")
public String helloV2(@RequestParam Integer data) {
    System.out.println("data = " + data);
    return "ok";
}
```

![image](https://user-images.githubusercontent.com/83503188/211770361-9e858711-0e36-4b83-bf23-a69424612da8.png)

`StringToIntegerConverter` 를 등록하기 전에도 이 코드는 잘 수행되었다. 그것은 스프링이 내부에서 수 많은 기본 컨버터들을 제공하기 때문이다.
컨버터를 추가하면 추가한 컨버터가 기본 컨버터 보다 높은 우선순위를 가진다.

이번에는 직접 정의한 타입인 `IpPort` 를 사용해보자.

**HelloController - 추가**

```java
    @GetMapping("/ip-port")
    public String ipPort(@RequestParam IpPort ipPort) {
        System.out.println("ipPort IP = " + ipPort.getIp());
        System.out.println("ipPort PORT = " + ipPort.getPort());
        return "ok";
    }
```

![image](https://user-images.githubusercontent.com/83503188/211770987-1d66d610-6725-4b9b-b371-ea34d36eb990.png)

**처리 과정**

`@RequestParam` 은 `@RequestParam` 을 처리하는 `ArgumentResolver` 인 `RequestParamMethodArgumentResolver` 에서 `ConversionService` 를 사용해서 타입을 변환한다.
부모 클래스와 다양한 외부 클래스를 호출하는 등 복잡한 내부 과정을 거치기 때문에 대략 이렇게 처리되는 것으로 이해해도 충분하다.
만약 더 깊이있게 확인하고 싶으면 `IpPortConverter` 에 디버그 브레이크 포인트를 걸어서 확인해보자.

## 뷰 템플릿에 컨버터 적용하기 

타임리프는 렌더링 시에 컨버터를 적용해서 렌더링 하는 방법을 편리하게 지원한다.
이전까지는 문자를 객체로 변환했다면, 이번에는 그 반대로 객체를 문자로 변환하는 작업을 확인할 수 있다.

**ConverterController**

```java
@Controller
public class ConverterController {
    
    @GetMapping("/converter-view")
    public String converterView(Model model) {
        model.addAttribute("number", 10000);
        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
        return "converter-view";
    }
}
```

`converter-view`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Title</title>
</head>
<body>
<ul>
  <li>${number}: <span th:text="${number}" ></span></li>
  <li>${{number}}: <span th:text="${{number}}" ></span></li>
  <li>${ipPort}: <span th:text="${ipPort}" ></span></li>
  <li>${{ipPort}}: <span th:text="${{ipPort}}" ></span></li>
</ul>
</body>
</html>
```
- `${{number}}` : 뷰 템플릿은 데이터를 문자로 출력한다. 따라서 컨버터를 적용하게 되면 Integer 타입인 `10000` 을 `String` 타입으로 변환하는 컨버터인 `IntegerToStringConverter` 를 실행하게 된다.
  이 부분은 컨버터를 실행하지 않아도 타임리프가 숫자를 문자로 자동으로 변환히기 때문에 컨버터를 적용할 때와 하지 않을 때가 같다.
- `${{ipPort}}` : 뷰 템플릿은 데이터를 문자로 출력한다. 따라서 컨버터를 적용하게 되면 `IpPort` 타입을 `String` 타입으로 변환해야 하므로 `IpPortToStringConverter` 가 적용된다. 그 결과 `127.0.0.1:8080` 가 출력된다.

타임리프는 `${{...}}` 를 사용하면 자동으로 컨버전 서비스를 사용해서 변환된 결과를 출력해준다.
물론 스프링과 통합 되어서 스프링이 제공하는 컨버전 서비스를 사용하므로, 우리가 등록한 컨버터들을 사용할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/212083832-7acf49f9-9897-4651-ad5f-eb9f74dcf8d5.png)
- `${ipPort}`는 `String`으로 출력해야 하기 때문에 `ipPort.toString()`의 결과가 출력된다.

### 폼에 적용하기

**ConverterController - 코드 추가**

```java
@Controller
public class ConverterController {

  ...

    @GetMapping("/converter/edit")
    public String converterForm(Model model) {
        IpPort ipPort = new IpPort("127.0.0.1", 8080);
        Form form = new Form(ipPort);

        model.addAttribute("form", form);
        return "converter-form";
    }

    @PostMapping("/converter/edit")
    public String converterEdit(@ModelAttribute Form form, Model model) {
        IpPort ipPort = form.getIpPort();
        model.addAttribute("ipPort", ipPort);
        return "converter-view";
    }

    @Data
    static class Form {
        private IpPort ipPort;
        public Form(IpPort ipPort) {
            this.ipPort = ipPort;
        }
    }
}
```
- `GET /converter/edit` : `IpPort` 를 뷰 템플릿 폼에 출력한다.
- `POST /converter/edit` : 뷰 템플릿 폼의 IpPort 정보를 받아서 출력한다.

타임리프의 `th:field` 는 앞서 설명했듯이 `id` , `name` 를 출력하는 등 다양한 기능이 있는데, 여기에 컨버전 서비스도 함께 적용된다.

**결과**

![image](https://user-images.githubusercontent.com/83503188/212085849-4660e804-f936-4155-8ce1-03371d7182c6.png)
- `GET /converter/edit`
  - `th:field` 가 자동으로 컨버전 서비스를 적용해주어서 `${{ipPort}}` 처럼 적용이 되었다. 따라서 `IpPort` -> `String` 으로 변환된다.

![image](https://user-images.githubusercontent.com/83503188/212086316-0898042f-a8a7-4698-bc31-a49e06adcced.png)
- `POST /converter/edit`
  - `@ModelAttribute`는 내부적으로 컨버전 서비스를 사용해서 `String` -> `IpPort` 로 변환된다.

## 포맷터 - Formatter

`Converter` 는 입력과 출력 타입에 제한이 없는, 범용 타입 변환 기능을 제공한다.
일반적인 웹 애플리케이션 환경을 생각해보자. 불린 타입을 숫자로 바꾸는 것 같은 범용 기능 보다는 개발자 입장에서는 문자를 다른 타입으로 변환하거나, 다른 타입을 문자로 변환하는 상황이 대부분이다.

**웹 애플리케이션에서 객체를 문자로, 문자를 객체로 변환하는 예**

- 화면에 숫자를 출력해야 하는데, `Integer` -> `String` 출력 시점에 숫자 `1000` -> 문자 `"1,000"` 이렇게 `1000` 단위에 쉼표를 넣어서 출력하거나, 또는 `"1,000"` 라는 문자를 `1000` 이라는 숫자로 변경해야 한다.
- 날짜 객체를 문자인 `"2021-01-01 10:50:11"` 와 같이 출력하거나 또는 그 반대의 상황

**Locale**

여기에 추가로 날짜 숫자의 표현 방법은 `Locale` 현지화 정보가 사용될 수 있다.

이렇게 객체를 특정한 포멧에 맞추어 문자로 출력하거나 또는 그 반대의 역할을 하는 것에 특화된 기능이 바로 포맷터( `Formatter` )이다.
포맷터는 컨버터의 특별한 버전으로 이해하면 된다.

**Converter vs Formatter**
- `Converter` 는 범용(객체 -> 객체)
- `Formatter` 는 문자에 특화(`객체` -> `문자`, `문자` -> `객체`) + 현지화(Locale)
  - `Converter` 의 특별한 버전

### 포맷터 - Formatter 만들기

포맷터( `Formatter` )는 객체를 문자로 변경하고, 문자를 객체로 변경하는 두 가지 기능을 모두 수행한다.

**Formatter 인터페이스**

```java
public interface Formatter<T> extends Printer<T>, Parser<T> {
}

public interface Printer<T> {
  String print(T object, Locale locale);
}
public interface Parser<T> {
  T parse(String text, Locale locale) throws ParseException;
}
```
- `String print(T object, Locale locale)` : 객체를 문자로 변경한다.
- `T parse(String text, Locale locale)` : 문자를 객체로 변경한다.

숫자 `1000` 을 문자 `"1,000"`, 1000 단위로 쉼표가 들어가는 포맷을 적용해보자.

**MyNumberFormatter**

```java
@Slf4j
public class MyNumberFormatter implements Formatter<Number> {
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("text={}, locale={}", text, locale);
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(text);
    }

    @Override
    public String print(Number object, Locale locale) {
        log.info("object={}, locale={}", object, locale);
        return NumberFormat.getInstance(locale).format(object);
    }
}
```
- `"1,000"` 처럼 숫자 중간의 쉼표를 적용하려면 자바가 기본으로 제공하는 `NumberFormat` 객체를 사용하면 된다. 이 객체는 `Locale` 정보를 활용해서 나라별로 다른 숫자 포맷을 만들어준다.
- `parse()` 를 사용해서 문자를 숫자로 변환한다. 
- 참고로 `Number` 타입은 `Integer` , `Long` 과 같은 숫자 타입의 부모 클래스이다.

**MyNumberFormatterTest**

```java
class MyNumberFormatterTest {

    MyNumberFormatter formatter = new MyNumberFormatter();

    @Test
    void parse() throws ParseException {
        Number result = formatter.parse("1,000", Locale.KOREA);
        assertThat(result).isEqualTo(1000L); //Long 타입 주의
    }

    @Test
    void print() {
        String result = formatter.print(1000, Locale.KOREA);
        assertThat(result).isEqualTo("1,000");
    }

}
```

![image](https://user-images.githubusercontent.com/83503188/212090937-3e6a719f-bd54-4b1a-9717-c2f5a2685f15.png)

## 포맷터를 지원하는 컨버전 서비스

컨버전 서비스에는 컨버터만 등록할 수 있고, 포맷터를 등록할 수 는 없다.
그런데 생각해보면 포맷터는 객체 -> 문자, 문자 -> 객체로 변환하는 특별한 컨버터일 뿐이다.

포맷터를 지원하는 컨버전 서비스를 사용하면 컨버전 서비스에 포맷터를 추가할 수 있다.
내부에서 어댑터 패턴을 사용해서 `Formatter` 가 `Converter` 처럼 동작하도록 지원한다.

`FormattingConversionService` 는 포맷터를 지원하는 컨버전 서비스이다.
`DefaultFormattingConversionService` 는 `FormattingConversionService` 에 기본적인 통화, 숫자 관련 몇가지 기본 포맷터를 추가해서 제공한다.

**FormattingConversionServiceTest**

```java
public class FormattingConversionServiceTest {

    @Test
    void formattingConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();

        //컨버터 등록
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        //포맷터 등록
        conversionService.addFormatter(new MyNumberFormatter());

        //컨버터 사용
        IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));

        //포맷터 사용
        assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");
        assertThat(conversionService.convert("1,000", Long.class)).isEqualTo(1000L);
    }
}
```

**DefaultFormattingConversionService 상속 관계**

`FormattingConversionService` 는 `ConversionService` 관련 기능을 상속받기 때문에 결과적으로 컨버터도 포맷터도 모두 등록할 수 있다.
그리고 사용할 때는 `ConversionService` 가 제공하는 `convert`를 사용하면 된다.

## 포맷터 적용하기

**WebConfig - 수정**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {

//        registry.addConverter(new StringToIntegerConverter());
//        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());
        
        registry.addFormatter(new MyNumberFormatter());
    }
}
```
- `MyNumberFormatter` 도 숫자 -> 문자, 문자 -> 숫자로 변경하기 때문에 둘의 기능이 겹친다. 우선순위는 컨버터가 우선하므로 포맷터가 적용되지 않고, 컨버터가 적용된다.

**실행 - 객체(`Number`) -> 문자**

![image](https://user-images.githubusercontent.com/83503188/212092484-03dae594-7603-4423-9a21-81f0cbb7fb2c.png)
- `MyNumberFormatter` 가 적용되어서 `10,000` 문자가 출력된 것을 확인할 수 있다.

**실행 - 문자 -> 객체(`Number`)**

![image](https://user-images.githubusercontent.com/83503188/212092877-222361d7-4c89-46e8-8280-e2725f3fde9f.png)

## 스프링이 제공하는 기본 포맷터

스프링은 자바에서 기본으로 제공하는 타입들에 대해 수 많은 포맷터를 기본으로 제공한다.
IDE에서 `Formatter` 인터페이스의 구현 클래스를 찾아보면 수 많은 날짜나 시간 관련 포맷터가 제공되는 것을 확인할 수 있다.

그런데 포맷터는 기본 형식이 지정되어 있기 때문에, 객체의 각 필드마다 다른 형식으로 포맷을 지정하기는 어렵다.

스프링은 이런 문제를 해결하기 위해 애노테이션 기반으로 원하는 형식을 지정해서 사용할 수 있는 매우 유용한 포맷터 두 가지를 기본으로 제공한다.

- `@NumberFormat` : 숫자 관련 형식 지정 포맷터 사용, `NumberFormatAnnotationFormatterFactory`
- `@DateTimeFormat` : 날짜 관련 형식 지정 포맷터 사용, `Jsr310DateTimeFormatAnnotationFormatterFactory`

**FormatterController**

```java
@Controller
public class FormatterController {

    @GetMapping("/formatter/edit")
    public String formatterForm(Model model) {
        Form form = new Form();
        form.setNumber(10000);
        form.setLocalDateTime(LocalDateTime.now());
        model.addAttribute("form", form);
        return "formatter-form";
    }

    @PostMapping("/formatter/edit")
    public String formatterEdit(@ModelAttribute Form form) {
        return "formatter-view";
    }

    @Data
    static class Form {
        @NumberFormat(pattern = "###,###")
        private Integer number;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime localDateTime;
    }
}
```
- `@NumberFormat(pattern = "###,###")`: 000부터 , 

**결과**

![image](https://user-images.githubusercontent.com/83503188/212094069-b59d1e99-74cf-4069-a81f-7a5ef84971e6.png)

![image](https://user-images.githubusercontent.com/83503188/212094526-44721559-25ee-4888-bc03-c82825ce74b2.png)

> 참고
>
> `@NumberFormat` , `@DateTimeFormat` 의 자세한 사용법이 궁금한 분들은 다음 링크를 참고하거나 관련 애노테이션을 검색해보자.
> 
> https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#format-CustomFormatAnnotations

