# 메시지, 국제화

## 메시지, 국제화 소개 

### 메시지 

**상품명**이라는 단어를 모두 **상품이름**으로 고쳐달라고 하면 어떻게 해야할까?

![image](https://user-images.githubusercontent.com/83503188/209789943-838d7e3c-6c0e-40d9-b232-2847bb3407ea.png)

![image](https://user-images.githubusercontent.com/83503188/209789981-5b4aab25-be4a-44d2-8e0d-7cda43580022.png)

![image](https://user-images.githubusercontent.com/83503188/209790019-b1f4f030-b618-4d40-86b3-5d332b323e88.png)

여러 화면에 보이는 상품명, 가격, 수량 등, label 에 있는 단어를 변경하려면 다음 화면들을 다 찾아가면서 모두 변경해야 한다.
지금처럼 화면 수가 적으면 문제가 되지 않지만 화면이 수십개 이상이라면 수십개의 파일을 모두 고쳐야 한다.
- `addForm.html` , `editForm.html` , `item.html` , `items.html`

이런 다양한 메시지를 한 곳에서 관리하도록 하는 기능을 메시지 기능이라 한다.

예를 들어서 `messages.properties` 라는 메시지 관리용 파일을 만들고

```text
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```

각 HTML들은 다음과 같이 해당 데이터를 key 값으로 불러서 사용하는 것이다.

`addForm.html`

```html
<label for="itemName" th:text="#{item.itemName}"></label>
```

`editForm.html`

```html
<label for="itemName" th:text="#{item.itemName}"></label>
```

### 국제화

메시지에서 설명한 메시지 파일( `messages.properties` )을 각 나라별로 별도로 관리하면 서비스를 국제화 할 수 있다.

`messages_en.properties`

```text
item=Item
item.id=Item ID
item.itemName=Item Name
item.price=price
item.quantity=quantity
```

`messages_ko.properties`

```text
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```
영어를 사용하는 사람이면 `messages_en.properties` 를 사용하고, 한국어를 사용하는 사람이면 `messages_ko.properties` 를 사용하게 개발하면 된다.

한국에서 접근한 것인지 영어에서 접근한 것인지는 인식하는 방법은 HTTP `accept-language` 해더 값을 사용하거나 사용자가 직접 웹 브라우저에서 언어를 선택하도록 하고, 쿠키에 저장하여 처리하면 된다.

메시지와 국제화 기능을 직접 구현할 수도 있겠지만, 스프링은 기본적인 메시지와 국제화 기능을 모두 제공한다.
그리고 타임리프도 스프링이 제공하는 메시지와 국제화 기능을 편리하게 통합해서 제공한다.


## 스프링 메시지 소스 설정

메시지 관리 기능을 사용하려면 스프링이 제공하는 `MessageSource` 를 스프링 빈으로 등록하면 되는데, `MessageSource` 는 인터페이스이다.
따라서 구현체인 `ResourceBundleMessageSource` 를 스프링 빈으로 등록하면 된다.


**직접 등록**

```java
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("messages", "errors");
		messageSource.setDefaultEncoding("utf-8");
		return messageSource;
	}
```
- `basenames` : 설정 파일의 이름을 지정한다.
  - `errors.properties`, `messages.properties`, ...
  - `messages` 로 지정하면 `messages.properties` 파일을 읽어서 사용한다.
  - 추가로 국제화 기능을 적용하려면 `messages_en.properties` , `messages_ko.properties` 와 같이 파일명 마지막에 언어 정보를 주면된다. 만약 찾을 수 있는 국제화 파일이 없으면 `messages.properties` (언어정보가 없는 파일명)를 기본으로 사용한다.
  - 파일의 위치는 `/resources/messages.properties` 에 두면 된다.
  - 여러 파일을 한번에 지정할 수 있다. 여기서는 messages , errors 둘을 지정했다.
- `defaultEncoding` : 인코딩 정보를 지정한다. utf-8 을 사용하면 된다.

**스프링 부트**

스프링 부트를 사용하면 스프링 부트가 `MessageSource` 를 자동으로 스프링 빈으로 등록한다.

**스프링 부트 메시지 소스 설정**

`application.properties`

```properties
spring.messages.basename=messages,config.i18n.messages
```
- resource 폴더 아래에 `messages.properties`, `messages_en.properties`, ...
- resource 폴더 아래에 config 폴더 아래에 i18n 폴더 아래에 `messages.properties`, `messages_en.properties`

**스프링 부트 메시지 소스 기본 값**

`spring.messages.basename=messages`

`MessageSource` 를 스프링 빈으로 등록하지 않고, 스프링 부트와 관련된 별도의 설정을 하지 않으면 `messages` 라는 이름으로 기본 등록된다.
따라서 `messages_en.properties` , `messages_ko.properties` , `messages.properties` 파일만 등록하면 자동으로 인식된다.

### 메시지 파일 만들기

- `messages.properties` :기본 값으로 사용(한글)
- `messages_en.properties` : 영어 국제화 사용

`/resources/messages.properties`

```properties
hello=안녕
hello.name=안녕 {0}
```

`/resources/messages_en.properties`

```properties
hello=hello
hello.name=hello {0}
```

## 스프링 메시지 소스 사용

**MessageSource 인터페이스**

MessageSource 인터페이스를 보면 코드를 포함한 일부 파라미터로 메시지를 읽어오는 기능을 제공한다.

```java
public interface MessageSource {[messages.properties](..%2F..%2F..%2F..%2F..%2FDownloads%2Fmvc2_source_v20210624%2Fmvc2%2Fmessage%2Fsrc%2Fmain%2Fresources%2Fmessages.properties)

    String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

    String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;
    
    ...
}
```

`MessageSource` 인터페이스를 보면 코드를 포함한 일부 파라미터로 메시지를 읽어오는 기능을 제공한다.

`test/java/hello/itemservice/message.MessageSourceTest`

```java
@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource ms;

    @Test
    void helloMessage() {
        String result = ms.getMessage("hello", null, null);
        Assertions.assertThat(result).isEqualTo("안녕");
    }

}
```
- `ms.getMessage("hello", null, null);`
  - code: hello
  - args: null
  - locale: null

가장 단순한 테스트는 메시지 코드로 `hello` 를 입력하고 나머지 값은 `null` 을 입력했다. `locale` 정보가 없으면 `basename` 에서 설정한 기본 이름(`messages.properties`) 메시지 파일을 조회한다.


**MessageSourceTest 추가 - 메시지가 없는 경우, 기본 메시지**

```java
    @Test
    void notFoundMessageCode() {
        assertThatThrownBy(() -> ms.getMessage("no_code", null, null))
                .isInstanceOf(NoSuchMessageException.class);
    }

    @Test
    void notFoundMessageCodeDefaultMessage() {
        String result = ms.getMessage("no_code", null, "기본 메시지", null);
        assertThat(result).isEqualTo("기본 메시지");
    }
```

**MessageSourceTest 추가 - 매개변수 사용**

```java
    @Test
    void argumentMessage() {
        String result = ms.getMessage("hello.name", new Object[]{"Spring"}, null);
        assertThat(result).isEqualTo("안녕 Spring");
    }
```
- 다음 메시지의 {0} 부분은 매개변수를 전달해서 치환할 수 있다.
  - hello.name=안녕 {0} -> Spring 단어를 매개변수로 전달 -> 안녕 Spring

**국제화 파일 선택**

locale 정보를 기반으로 국제화 파일을 선택한다.
- Locale이 `en_US` 의 경우 `messages_en_US` -> `messages_en` -> `messages` 순서로 찾는다.
- Locale 에 맞추어 구체적인 것이 있으면 구체적인 것을 찾고, 없으면 디폴트를 찾는다고 이해하면 된다.

**MessageSourceTest 추가 - 국제화 파일 선택1**

```java
    @Test
    void defaultLang() {
        assertThat(ms.getMessage("hello", null, null)).isEqualTo("안녕");
        assertThat(ms.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
    }
```
- `ms.getMessage("hello", null, null)` : locale 정보가 없으므로 `messages` 를 사용 
- `ms.getMessage("hello", null, Locale.KOREA)` : locale 정보가 있지만, `message_ko` 가 없으므로 `messages` 를 사용

**MessageSourceTest 추가 - 국제화 파일 선택2**

```java
    @Test
    void enLang() {
        assertThat(ms.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
    }
```
- `ms.getMessage("hello", null, Locale.ENGLISH)` : locale 정보가 `Locale.ENGLISH` 이므로`messages_en` 을 찾아서 사용

## 웹 애플리케이션 메시지 적용하기 

`messages.properties`

```properties
label.item=상품
label.item.id=상품 ID
label.item.itemName=상품명
label.item.price=가격
label.item.quantity=수량

page.items=상품 목록
page.item=상품 상세
page.addItem=상품 등록
page.updateItem=상품 수정

button.save=저장
button.cancel=취소
```

**타임리프 메시지 적용**

타임리프의 메시지 표현식 `#{...}` 를 사용하면 스프링의 메시지를 편리하게 조회할 수 있다.

`렌더링 전`

```html
<div th:text="#{label.item}"></h2>
```

`렌더링 후`

```html
<div>상품</h2>
```

**addForm.html**

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link th:href="@{/css/bootstrap.min.css}"
          href="../css/bootstrap.min.css" rel="stylesheet">
    <style>
        .container {
            max-width: 560px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2 th:text="#{page.addItem}">상품 등록</h2>
    </div>
    <h4 class="mb-3">상품 입력</h4>
    <form action="item.html" th:action th:object="${item}" method="post">
        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}"
                   class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price" th:text="#{label.item.price}">가격</label>
            <input type="text" id="price" th:field="*{price}" class="form-control"
                   placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity" th:text="#{label.item.quantity}">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}"
                   class="form-control" placeholder="수량을 입력하세요">
        </div>
        <hr class="my-4">
        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit"
                        th:text="#{button.save}">저장
                </button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/message/items}'|"
                        type="button" th:text="#{button.cancel}">취소
                </button>
            </div>
        </div>
    </form>
</div> <!-- /container -->
</body>
</html>
```

**페이지 이름에 적용**
- `<h2>상품 등록 폼</h2>`
  - `<h2 th:text="#{page.addItem}">상품 등록</h2>`

**레이블에 적용**
- `<label for="itemName">상품명</label>`
  - `<label for="itemName" th:text="#{label.item.itemName}">상품명</label>`
  - `<label for="price" th:text="#{label.item.price}">가격</label>`
  - `<label for="quantity" th:text="#{label.item.quantity}">수량</label>`

**버튼에 적용**
- `<button type="submit">상품 등록</button>`
  - `<button type="submit" th:text="#{button.save}">저장</button>`
  - `<button type="button" th:text="#{button.cancel}">취소</button>`

![image](https://user-images.githubusercontent.com/83503188/209805670-5fe11987-05ec-4e7e-ab60-1cf93ce767fb.png)

참고로 파라미터는 다음과 같이 사용할 수 있다.
- `hello.name=안녕 {0}`
- `<p th:text="#{hello.name(${item.itemName})}"></p>`

## 웹 애플리케이션에 국제화 적용하기

`messages_en.properties`

```properties
label.item=Item
label.item.id=Item ID
label.item.itemName=Item Name
label.item.price=price
label.item.quantity=quantity

page.items=Item List
page.item=Item Detail
page.addItem=Item Add
page.updateItem=Item Update

button.save=Save
button.cancel=Cancel
```

**웹으로 확인하기**

![image](https://user-images.githubusercontent.com/83503188/209806411-cab8556c-8b0b-4587-abfd-d70e327fc27a.png)

![image](https://user-images.githubusercontent.com/83503188/209806671-5cdea3fc-94d5-4829-a6b8-6e0ae38a4122.png)

![image](https://user-images.githubusercontent.com/83503188/209806421-51e18b83-64ce-4125-861f-d6e2ec106903.png)

### 스프링의 국제화 메시지 선택

앞서 `MessageSource` 테스트에서 보았듯이 메시지 기능은 `Locale` 정보를 알아야 언어를 선택할 수 있다.

결국 스프링도 `Locale` 정보를 알아야 언어를 선택할 수 있는데, 스프링은 언어 선택시 기본으로 `Accept-Language` 헤더의 값을 사용한다.

**LocaleResolver**

스프링은 `Locale` 선택 방식을 변경할 수 있도록 `LocaleResolver` 라는 인터페이스를 제공하는데, 스프링 부트는 기본으로 `Accept-Language` 를 활용하는 `AcceptHeaderLocaleResolver` 를 사용한다.

**LocaleResolver 인터페이스**

```java
public interface LocaleResolver {

    Locale resolveLocale(HttpServletRequest request);

    void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale);

}
```

**LocaleResolver 변경**

만약 `Locale` 선택 방식을 변경하려면 `LocaleResolver` 의 구현체를 변경해서 쿠키나 세션 기반의 `Locale` 선택 기능을 사용할 수 있다.
예를 들어서 고객이 직접 `Locale`을 선택하도록 하는 것이다.



