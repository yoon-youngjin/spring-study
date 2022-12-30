# 검증1 - Validation

**현재상황**

![image](https://user-images.githubusercontent.com/83503188/209931233-95a8432d-fd66-4b30-9bda-fd9b9cb4b8d1.png)
- 숫자 타입이 아닌 문자가 들어오는 경우

![image](https://user-images.githubusercontent.com/83503188/209931250-5461920a-8d6c-4dbe-bfbd-a2158e48ddb1.png)
- 서버에서 `NumberFormatException` 발생
- 사용자는 자신이 입력한 데이터를 모두 잃어버린다.
- 오류가 발생하면 사용자의 입력을 유지하고, 문제가 발생한 원인을 알려줘야한다.

## 검증 요구사항

**요구사항: 검증 로직 추가**
- 타입 검증
  - 가격, 수량에 문자가 들어가면 검증 오류 처리
- 필드 검증
  - 상품명: 필수, 공백X
  - 가격: 1000원 이상, 1백만원 이하
  - 수량: 최대 9999
- 특정 필드의 범위를 넘어서는 검증
  - 가격 * 수량의 합은 10,000원 이상

웹 서비스는 폼 입력시 오류가 발생하면, 고객이 입력한 데이터를 유지한 상태로 어떤 오류가 발생했는지 친절하게 알려주어야 한다.

**컨트롤러의 중요한 역할중 하나는 HTTP 요청이 정상인지 검증하는 것이다.**


> 참고: 클라이언트 검증, 서버 검증
> 
> 클라이언트 검증은 조작할 수 있으므로 보안에 취약하다.(서버로 직접 API 요청) 또한 서버만으로 검증하면, 즉각적인 고객 사용성이 부족해진다.
> 따라서 둘을 적절히 섞어서 사용하되, 최종적으로 서버 검증은 필수이다. 
> API 방식을 사용하면 API 스펙을 잘 정의해서 검증 오류를 API 응답 결과에 잘 남겨주어야 한다.

## 검증 직접 처리 - 소개

**상품 저장 성공**

![image](https://user-images.githubusercontent.com/83503188/209931004-347b05b6-3670-4712-b460-1054df2f9e22.png)
- 사용자가 상품 등록 폼에서 정상 범위의 데이터를 입력하면, 서버에서는 검증 로직이 통과하고, 상품을 저장하고, 상품 상세 화면으로 redirect한다.

**상품 저장 검증 실패**

![image](https://user-images.githubusercontent.com/83503188/209932012-1d10e2cd-3a23-43d5-90d8-13ebda965e55.png)
- 고객이 상품 등록 폼에서 상품명을 입력하지 않거나, 가격, 수량 등이 너무 작거나 커서 검증 범위를 넘어서면, 서버 검증 로직이 실패해야 한다.
- 이렇게 검증에 실패한 경우 고객에게 다시 상품 등록 폼을 보여주고, 어떤 값을 잘못 입력했는지 친절하게 알려주어야 한다.

## 검증 직접 처리 - 개발

**ValidationItemControllerV1 - addItem() 수정**

```java
   @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            errors.put("itemName", "상품 이름은 필수입니다.");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.put("price", "가격은 1,00 ~ 1,000,000 까지 허용합니다.");
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }
        
        // 검증에 실패하면 다시 입력 폼으로
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "validation/v1/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v1/items/{itemId}";
    }
```

**특정 필드의 범위를 넘어서는 검증 로직**
```java
        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }
```
- 특정 필드를 넘어서는 오류를 처리해야 할 수도 있다. 이때는 필드 이름을 넣을 수 없으므로 `globalError` 라는 `key` 를 사용한다.


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
        .field-error {
            border-color: #dc3545;
            color: #dc3545;
        }
    </style>
</head>
<body>

<div class="container">

    <div class="py-5 text-center">
        <h2 th:text="#{page.addItem}">상품 등록</h2>
    </div>

    <form action="item.html" th:action th:object="${item}" method="post">

        <div th:if="${errors?.containsKey('globalError')}">
            <p class="field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
        </div>

        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}"
                   th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'"
                   class="form-control" placeholder="이름을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}">
                상품명 오류
            </div>
        </div>
        <div>
            <label for="price" th:text="#{label.item.price}">가격</label>
            <input type="text" id="price" th:field="*{price}"
                   th:class="${errors?.containsKey('price')} ? 'form-control field-error' : 'form-control'"
                   class="form-control" placeholder="가격을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('price')}" th:text="${errors['price']}">
                가격 오류
            </div>
        </div>

        <div>
            <label for="quantity" th:text="#{label.item.quantity}">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}"
                   th:class="${errors?.containsKey('quantity')} ? 'form-control field-error' : 'form-control'"
                   class="form-control" placeholder="수량을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('quantity')}" th:text="${errors['quantity']}">
                수량 오류
            </div>

        </div>

        <hr class="my-4">

        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit" th:text="#{button.save}">상품 등록</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/validation/v1/items}'|"
                        type="button" th:text="#{button.cancel}">취소</button>
            </div>
        </div>

    </form>

</div> <!-- /container -->
</body>
</html>
```

**글로벌 오류 메시지**

```html
<div th:if="${errors?.containsKey('globalError')}">
<p class="field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
</div>
```

> 참고 Safe Navigation Operator -> ?
> 
> `errors?.` 은 `errors` 가 `null` 일때 `NullPointerException` 이 발생하는 대신, `null` 을 반환하는 문법이다.
> `th:if` 에서 `null` 은 실패로 처리되므로 오류 메시지가 출력되지 않는다.

**필드 오류 처리**

```html
<input type="text" th:classappend="${errors?.containsKey('itemName')} ? 'field-error': _" class="form-control">
```
- `classappend` 를 사용해서 해당 필드에 오류가 있으면 `field-error` 라는 클래스 정보를 더해서 폼의 색깔을 빨간색으로 강조한다.
- 만약 값이 없으면 `_` (No-Operation)을 사용해서 아무것도 하지 않는다.

```html
<input type="text" class="form-control field-error">
```

**필드 오류 처리 - 메시지**

```html
<div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}">
    상품명 오류
</div>
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/209934492-3b9b7167-3df0-473f-abbb-ad1a94332c46.png)

**남은 문제점**

![image](https://user-images.githubusercontent.com/83503188/209935820-c8176945-854a-4d68-8357-1b9cf0419ea1.png)
- 뷰 템플릿에서 중복 처리가 많다. 뭔가 비슷하다.

![image](https://user-images.githubusercontent.com/83503188/209935966-ede9d90e-1d5d-4d1a-b188-b1e9ed76d301.png)
- 타입 오류 처리가 안된다. 숫자 타입에 문자가 들어오면 오류가 발생한다. 

그런데 이러한 오류는 스프링MVC에서 컨트롤러에 진입하기도 전에 (Item 객체에 값이 바인딩이 안된다.) 예외가 발생하기 때문에, 컨트롤러가 호출되지도 않고, 400 예외가 발생하면서 오류 페이지를 띄워준다.

Item 의 price 에 문자를 입력하는 것 처럼 타입 오류가 발생해도 고객이 입력한 문자를 화면에 남겨야 한다. 만약 컨트롤러가 호출된다고 가정해도 Item 의 price 는 Integer 이므로 문자를 보관할 수가 없다.
결국 문자는 바인딩이 불가능하므로 고객이 입력한 문자가 사라지게 되고, 고객은 본인이 어떤 내용을 입력해서 오류가 발생했는지 이해하기 어렵다.

결국 고객이 입력한 값도 어딘가에 별도로 관리가 되어야 한다.

## BindingResult1

**ValidationItemControllerV2 - addItemV1**

```java
@PostMapping("/add")
    public String addItemV1((@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {


        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }


        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }


        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

**주의**

`BindingResult bindingResult` 파라미터 위치는 `@ModelAttribte Item item` 다음에 와야 한다.

**FieldError 생성자 요약**

```java
public FieldError(String objectName, String field, String defaultMessage) {}
```

필드에 오류가 있으면 `FieldError` 객체를 생성해서 `bindingResult` 에 담아두면 된다.
- `objectName` : `@ModelAttribute` 이름 -> item
- `field` : 오류가 발생한 필드 이름 -> itemName
- `defaultMessage` : 오류 기본 메시지 -> "상품 이름은 필수입니다."

**ObjectError 생성자 요약**

```java
public ObjectError(String objectName, String defaultMessage) {}
```

특정 필드를 넘어서는 오류가 있으면 `ObjectError` 객체를 생성해서 `bindingResult` 에 담아두면 된다.
- `objectName` : `@ModelAttribute` 의 이름
- `defaultMessage` : 오류 기본 메시지

`validation/v2/addForm.html`

```html
        <div th:if="${#fields.hasGlobalErrors()}">
            <p class="field-error" th:each="err : ${#fields.globalErrors()}" th:text="${err}">글로벌 오류 메시지</p>
        </div>

        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}"
                   th:errorclass="field-error" class="form-control" placeholder="이름을 입력하세요">
            <div class="field-error" th:errors="*{itemName}">
                상품명 오류
            </div>
        </div>
        <div>
            <label for="price" th:text="#{label.item.price}">가격</label>
            <input type="text" id="price" th:field="*{price}" th:errorclass="field-error" class="form-control" placeholder="가격을 입력하세요">
            <div class="field-error" th:errors="*{price}">
                가격 오류
            </div>
        </div>

        <div>
            <label for="quantity" th:text="#{label.item.quantity}">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}" th:errorclass="field-error" class="form-control"
                   placeholder="수량을 입력하세요">
            <div class="field-error" th:errors="*{quantity}">
                수량 오류
            </div>
        </div>
```

**타임리프 스프링 검증 오류 통합 기능**
- `#fields` : `#fields` 로 `BindingResult` 가 제공하는 검증 오류에 접근할 수 있다.
- `th:errors` : 해당 필드에 오류가 있는 경우에 태그를 출력한다. `th:if` 의 편의 버전이다.
- `th:errorclass` : `th:field` 에서 지정한 필드에 오류가 있으면 `class` 정보를 추가한다.

## BindingResult2

- `BindingResult`는 스프링이 제공하는 검증 오류를 보관하는 객체이다. 검증 오류가 발생하면 여기에 보관하면 된다.
- `BindingResult`가 있으면 `@ModelAttribute` 에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다!

**예) @ModelAttribute에 바인딩 시 타입 오류가 발생하면?**
- `BindingResult` 가 없으면 400 오류가 발생하면서 컨트롤러가 호출되지 않고, 오류 페이지로 이동한다.
- `BindingResult` 가 있으면 오류 정보( `FieldError` )를 `BindingResult` 에 담아서 컨트롤러를 정상 호출한다.

**BindingResult에 검증 오류를 적용하는 3가지 방법**
1. `@ModelAttribute` 의 객체에 타입 오류 등으로 바인딩이 실패하는 경우 스프링이 `FieldError` 생성해서`BindingResult` 에 넣어준다.
2. 개발자가 직접 넣어준다.
3. `Validator` 사용 

**타입 오류 확인**

![image](https://user-images.githubusercontent.com/83503188/209939005-3b93007d-1611-47bd-90bd-82cb0c514b74.png)

**남은문제**

![image](https://user-images.githubusercontent.com/83503188/209940655-b3337395-9cf1-46bb-ac1c-4405152b11cd.png)

![image](https://user-images.githubusercontent.com/83503188/209940662-2f73b9f0-92c9-4f5b-ac3c-f62c6ada56f5.png)

그런데 오류가 발생하는 경우 고객이 입력한 내용이 모두 사라진다. 이 문제를 해결해보자.


## FieldError, ObjectError

**목표**

사용자 입력 오류 메시지가 화면에 남도록 하자.
- 예) 가격을 1000원 미만으로 설정시 입력한 값이 남아있어야 한다.

**ValidationItemControllerV2 - addItemV2**

```java
    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }


        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price",
                    item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",
                    item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }


        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

**FieldError 생성자**

```java
public FieldError(String objectName, String field, String defaultMessage);

public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @NullableObject[] arguments, @Nullable String defaultMessage)
```
- `objectName` : 오류가 발생한 객체 이름
- `field` : 오류 필드
- `rejectedValue` : 사용자가 입력한 값(거절된 값)
- `bindingFailure` : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
- `codes` : 메시지 코드
- `arguments` : 메시지에서 사용하는 인자
- `defaultMessage` : 기본 오류 메시지

`rejectedValue` 가 바로 오류 발생시 사용자 입력 값을 저장하는 필드다. `bindingFailure` 는 타입 오류 같은 바인딩이 실패했는지 여부를 적어주면 된다. 여기서는 바인딩이 실패한 것은 아니기 때문에 `false` 를 사용



**타임리프의 사용자 입력 값 유지**
- `th:field="*{price}"`
- 정상 상황에는 모델 객체의 값을 사용하지만, 오류가 발생하면 `FieldError` 에서 보관한 값( `item.getItemName()`, `item.getPrice()`, ...)을 사용해서 값을 출력한다.

**스프링의 바인딩 오류 처리**

![image](https://user-images.githubusercontent.com/83503188/209942199-2cbab0da-af22-4175-9076-4c50ebec76ee.png)

타입 오류로 바인딩에 실패하면 스프링은 `FieldError` 를 생성하면서 사용자가 입력한 값을 넣어둔다.
그리고 해당 오류를 `BindingResult` 에 담아서 컨트롤러를 호출한다. 따라서 타입 오류 같은 바인딩 실패시에도 사용자의 오류 메시지를 정상 출력할 수 있다.

`bindingResult.addError(new FieldError("item", "price", "qqqq", true, null, null, "..."));`
- 스프링이 컨트롤러를 호출할 때 바인딩에 실패하면 알아서 `BindingResult` 에 담아준다.

**결과**

![image](https://user-images.githubusercontent.com/83503188/209941517-afae1250-4e94-403c-bc05-0b9b87bbdeab.png)

## 오류 코드와 메시지 처리1

**FieldError 생성자**

```java
public FieldError(String objectName, String field, String defaultMessage);

public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @NullableObject[] arguments, @Nullable String defaultMessage)
```

`FieldError` , `ObjectError` 의 생성자는 `codes` , `arguments` 를 제공한다. 이것은 오류 발생시 오류 코드로 메시지를 찾기 위해 사용된다.

**errors 메시지 파일 생성**

오류 메시지를 구분하기 쉽게 `errors.properties` 라는 별도의 파일로 관리

**스프링 부트 메시지 설정 추가**

`application.properties`
```properties
spring.messages.basename=messages,errors
```

**errors.properties 추가**

`src/main/resources/errors.properties`

```properties
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```

> 참고: errors_en.properties 파일을 생성하면 오류 메시지도 국제화 처리를 할 수 있다.

**ValidationItemControllerV2 - addItemV3() 추가**

```java
@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }


        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price",
                    item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",
                    item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }


        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

```java
//range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{"1,000", "1,000,000"}
```
- `codes` : `required.item.itemName` 를 사용해서 메시지 코드를 지정한다. 메시지 코드는 하나가 아니라 배열로 여러 값을 전달할 수 있는데, 순서대로 매칭해서 처음 매칭되는 메시지가 사용된다.
  - `item.getItemName(), false, new String[]{"required.item.itemName", "required.default"}, null, null));`: `required.item.itemName` 를 못찾으면 `required.default` 사용 
  - `required.default` 조차 없으면 오류 발생
- `arguments` : `Object[]{1000, 1000000}` 를 사용해서 코드의 `{0}` , `{1}` 로 치환할 값을 전달한다.

**결과**

![image](https://user-images.githubusercontent.com/83503188/210052760-4165ccc8-73da-4201-8f4a-75fd4ec947f6.png)

## 오류 코드와 메시지 처리2

**목표**
- `FieldError` , `ObjectError` 는 다루기 너무 번거롭다.
- 오류 코드도 좀 더 자동화 할 수 있지 않을까? 예) `item.itemName `처럼?

컨트롤러에서 `BindingResult` 는 검증해야 할 객체인 target 바로 다음에 온다. 따라서 `BindingResult` 는 이미 본인이 검증해야 할 객체인 target 을 알고 있다.

```java
log.info("objectName={}", bindingResult.getObjectName());
log.info("target={}", bindingResult.getTarget());
```

![image](https://user-images.githubusercontent.com/83503188/210053709-dba9d8ad-27f1-444b-a721-a4bf442f7746.png)

### `rejectValue() `, `reject()`

`BindingResult` 가 제공하는 `rejectValue()` , `reject()` 를 사용하면 `FieldError` , `ObjectError` 를 직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.

**ValidationItemControllerV2 - addItemV4() 추가**

```java
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required"); // #required.item.itemName
        }


        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null); // #range.item.price
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }


        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

**rejectValue()**

```java
void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
```
- `field` : 오류 필드명
- `errorCode` : 오류 코드(이 오류 코드는 메시지에 등록된 코드가 아니다. 뒤에서 설명할 messageResolver 를 위한 오류 코드이다.)
- `errorArgs` : 오류 메시지에서 `{0}` 을 치환하기 위한 값
- `defaultMessage` : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지

```java
bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null)
```
- 앞에서 `BindingResult` 는 어떤 객체를 대상으로 검증하는지 `target`을 이미 알고 있다고 했다. 따라서 target(item)에 대한 정보는 없어도 된다.
- 오류 필드명은 동일하게 `price` 를 사용했다. 


**축약된 오류 코드**

`FieldError()` 를 직접 다룰 때는 오류 코드를 `range.item.price` 와 같이 모두 입력했다.
그런데`rejectValue()` 를 사용하고 부터는 오류 코드를 `range` 로 간단하게 입력했다. 이 부분을 이해하려면 `MessageCodesResolver` 를 이해해야 한다.

**reject()**

```java
void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
```
- 앞의 내용과 같다.

## 오류 코드와 메시지 처리3

**오류 코드 생성 2가지**

```text
required.item.itemName : 상품 이름은 필수 입니다.
range.item.price : 상품의 가격 범위 오류 입니다.
```
- 자세히 생성

```text
required : 필수 값 입니다.
range : 범위 오류 입니다.
```
- 단순하게 생성

단순하게 만들면 범용성이 좋아서 여러곳에서 사용할 수 있지만, 메시지를 세밀하게 작성하기 어렵다.
반대로 너무 자세하게 만들면 범용성이 떨어진다.
가장 좋은 방법은 범용성으로 사용하다가, 세밀하게 작성해야 하는 경우에는 세밀한 내용이 적용되도록 **메시지에 단계**를 두는 방법이다.

예를 들어서 `required` 라고 오류 코드를 사용한다고 가정해보자.

다음과 같이 `required` 라는 메시지만 있으면 이 메시지를 선택해서 사용하는 것이다.
```properties
required: 필수 값 입니다.
```

그런데 오류 메시지에 `required.item.itemName` 와 같이 객체명과 필드명을 조합한 세밀한 메시지 코드가 있으면 이 메시지를 높은 우선순위로 사용하는 것이다.
```properties
#Level1
required.item.itemName: 상품 이름은 필수 입니다.

#Level2
required: 필수 값 입니다.
```
- `bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName", "required"}, null, null));`

위와 같이 설계함으로써 개발 코드의 변경없이 `messages.properties`의 수정으로 전체적인 메시지를 관리할 수 있다.

스프링은 `MessageCodesResolver` 라는 것으로 이러한 기능을 지원한다.

## 오류 코드와 메시지 처리4

`MessageCodesResolverTest`

```java
public class MessageCodesResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    @DisplayName("")
    void messageCodeResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        assertThat(messageCodes).containsExactly("required.item.itemName", "required.itemName", "required.java.lang.String", "required");
    }
}
```

**MessageCodesResolver**
- 검증 오류 코드("required", "item", ...)로 메시지 코드("required.item.itemName", "required.itemName", ...)들을 생성한다.
- `MessageCodesResolver` 인터페이스이고 `DefaultMessageCodesResolver` 는 기본 구현체이다.
- 주로 다음과 함께 사용 `ObjectError` , `FieldError`

### DefaultMessageCodesResolver의 기본 메시지 생성 규칙

**객체 오류**

```text
객체 오류의 경우 다음 순서로 2가지 생성
1.: code + "." + object name
2.: code

예) 오류 코드: required, object name: item
1.: required.item
2.: required
```

**필드 오류**

```text
필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
1.: code + "." + object name + "." + field
2.: code + "." + field
3.: code + "." + field type
4.: code
예) 오류 코드: typeMismatch, object name "user", field "age", field type: int
1. "typeMismatch.user.age"
2. "typeMismatch.age"
3. "typeMismatch.int"
4. "typeMismatch"
```

**동작 방식**

1. BindingResult 의 rejectValue() 를 호출할 때 내부적으로 MessageCodesResolver 를 통해 메시지 코드를 생성(MessageCodesResolver 는 가장 디테일한 메시지부터 생성)
2. 생성된 메시지 코드들을 통해 `FieldError` 를 생성 -> `new FieldError("item", "itemName", null, false, new String[]{"required.item.itemName",...}, null, null);`
   - `FieldError` , `ObjectError` 의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있다. MessageCodesResolver 를 통해서 생성된 순서대로 오류 코드를 보관한다.

![image](https://user-images.githubusercontent.com/83503188/210057940-f5436359-bf03-4b29-a14d-ed5826a3f572.png)


**FieldError rejectValue("itemName", "required")**

다음 4가지 오류 코드를 자동으로 생성
- `required.item.itemName`
- `required.itemName`
- `required.java.lang.String`
  - 타입을 가지고 "문자를 반드시 입력해야합니다." 와 같은 공통 메시지를 만들 수 있다.
- `required`

**ObjectError reject("totalPriceMin")**

다음 2가지 오류 코드를 자동으로 생성
- `totalPriceMin.item`
- `totalPriceMin`

## 오류 코드와 메시지 처리5

### 오류 코드 관리 전략

`errors.properties`

```properties
#==ObjectError==
#Level1
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#Level2 - 생략
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}

#==FieldError==
#Level1
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.

#Level2 - 생략

#Level3
required.java.lang.String = 필수 문자입니다.
required.java.lang.Integer = 필수 숫자입니다.
min.java.lang.String = {0} 이상의 문자를 입력해주세요.
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요.
range.java.lang.String = {0} ~ {1} 까지의 문자를 입력해주세요.
range.java.lang.Integer = {0} ~ {1} 까지의 숫자를 입력해주세요.
max.java.lang.String = {0} 까지의 문자를 허용합니다.
max.java.lang.Integer = {0} 까지의 숫자를 허용합니다.

#Level4
required = 필수 값 입니다.
min= {0} 이상이어야 합니다.
range= {0} ~ {1} 범위를 허용합니다.
max= {0} 까지 허용합니다.
```
- 구체적인 것을 먼저 만들고, 덜 구체적인 것을 가장 나중에 만든다.

**Level1 적용**

![image](https://user-images.githubusercontent.com/83503188/210058783-5c5bafd3-9e75-42a9-8cc5-37f9bb5db816.png)

**Level1 주석 처리**

![image](https://user-images.githubusercontent.com/83503188/210058630-1b4b6562-6765-4bd9-ab87-41609374d8a2.png)

**Level3 주석 처리**

![image](https://user-images.githubusercontent.com/83503188/210058706-6385a364-5e3e-4539-b753-8dd2eb7fc944.png)

### ValidationUtils

**ValidationUtils 사용 전**

```java
if (!StringUtils.hasText(item.getItemName())) {bindingResult.rejectValue("itemName", "required", "기본: 상품 이름은 필수입니다.");}
```

**ValidationUtils 사용 후**

```java
ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName","required");
```

조건문없이 간단하게 값이 없거나 공백을 처리할 수 있는 Util 클래스

**정리**
1. `rejectValue()` 호출
2. `MessageCodesResolver` 를 사용해서 검증 오류 코드로 메시지 코드들을 생성
3. `new FieldError()` 를 생성하면서 메시지 코드들을 보관
4. `th:errors` 에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고, 노출

## 오류 코드와 메시지 처리6

### 스프링이 직접 만든 오류 메시지 처리

![image](https://user-images.githubusercontent.com/83503188/210059517-272ae689-aee4-41b8-a33c-48f10869a7bf.png)

검증 오류 코드는 다음과 같이 2가지로 나눌 수 있다.
- 개발자가 직접 설정한 오류 코드 `rejectValue()` 를 직접 호출
- 스프링이 직접 검증 오류에 추가한 경우(주로 타입 정보가 맞지 않음)

`price` 필드에 문자를 입력하는 경우에 로그를 확인해보면 `BindingResult` 에 `FieldError` 가 담겨있고, 다음과 같은 메시지 코드들이 생성된 것을 확인할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/210059638-e3b73c63-2c00-4f7c-afd7-2576f5f4c23e.png)
- `typeMismatch.item.price`
- `typeMismatch.price`
- `typeMismatch.java.lang.Integer`
- `typeMismatch`

스프링은 타입 오류가 발생하면 `typeMismatch` 라는 오류 코드를 사용한다. 이 오류 코드가 `MessageCodesResolver` 를 통하면서 4가지 메시지 코드가 생성된 것이다.

`error.properties` 추가 

```properties
typeMismatch.java.lang.Integer=숫자를 입력해주세요.
typeMismatch=타입 오류입니다.
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/210059825-2176d4ef-e831-4faa-9ead-012fec04133b.png)

## Validator 분리1

**목표**
- 복잡한 검증 로직을 별도로 분리하자.

컨트롤러에서 검증 로직이 차지하는 부분은 매우 크다.
이런 경우 별도의 클래스로 역할을 분리하는 것이 좋다. 그리고 이렇게 분리한 검증 로직을 재사용 할 수도 있다.

**ItemValidator**

```java
@Component
public class ItemValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Item item = (Item) target;


        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required"); // #required.item.itemName
        }


        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null); // #range.item.price
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }


        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
```

스프링은 검증을 체계적으로 제공하기 위해 다음 인터페이스를 제공한다.
```java
public interface Validator {
    boolean supports(Class<?> clazz);
    void validate(Object target, Errors errors);
}
```
- `supports() {}` : 해당 검증기를 지원하는 여부 확인(뒤에서 설명)
  - `Item.class.isAssignableFrom(clazz);`: isAssignableFrom 을 통해서 자식 클래스도 허용
- `validate(Object target, Errors errors)` : 검증 대상 객체와 BindingResult

### ItemValidator 직접 호출하기

**ValidationItemControllerV2 - addItemV5()**

```java
    private final ItemValidator itemValidator;

        ... 

    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        itemValidator.validate(item, bindingResult);

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

## Validator 분리2

스프링이 `Validator` 인터페이스를 별도로 제공하는 이유는 체계적으로 검증 기능을 도입하기 위해서다.

`Validator` 인터페이스를 사용해서 검증기를 만들면 스프링의 추가적인 도움을 받을 수 있다.

### WebDataBinder를 통해서 사용하기

`WebDataBinder` 는 스프링의 파라미터 바인딩의 역할을 해주고 검증 기능도 내부에 포함한다.

**ValidationItemControllerV2**

```java
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        log.info("init binder={}", dataBinder);
        dataBinder.addValidators(itemValidator);
    }
```
- 이렇게 `WebDataBinder` 에 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용할 수 있다.
- `@InitBinder` -> 해당 컨트롤러에만 영향을 준다. 글로벌 설정은 별도로 해야한다.

### @Validated 적용

**ValidationItemControllerV2 - addItemV6()**

```java
@PostMapping("/add")
public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
}
```
- validator 를 직접 호출하는 부분이 사라지고, 대신에 검증 대상 앞에 `@Validated` 가 붙었다.

**동작 방식**

`@Validated` 는 검증기를 실행하라는 애노테이션이다.

이 애노테이션이 붙으면 앞서 `WebDataBinder` 에 등록한 검증기를 찾아서 실행한다.
그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요하다. 이때 `supports()` 가 사용된다.

여기서는 `supports(Item.class)` 호출되고, 결과가 `true` 이므로 `ItemValidator` 의 `validate()` 가 호출된다.

### 글로벌 설정 - 모든 컨트롤러에 다 적용

```java
@SpringBootApplication
public class ItemServiceApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }

    @Override
    public Validator getValidator() {
        return new ItemValidator();
    }
}
```
- 기존 컨트롤러의 `@InitBinder` 를 제거해도 글로벌 설정으로 정상 동작하는 것을 확인할 수 있다.

