# 타임리프 - 스프링 통합과 폼

## 타임리프 스프링 통합

타임리프는 크게 2가지 메뉴얼을 제공한다.

- 기본 메뉴얼: https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html
    - section 1 내용
- 스프링 통합 메뉴얼: https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html
    - section 2 내용

**스프링 통합으로 추가되는 기능들**

- 스프링의 SpringEL 문법 통합
- `${@myBean.doSomething()}` 처럼 스프링 빈 호출 지원
- 편리한 폼 관리를 위한 추가 속성
    - `th:object` (기능 강화, 폼 커맨드 객체 선택)
    - `th:field` , `th:errors` , `th:errorclass`
- 폼 컴포넌트 기능
    - checkbox, radio button, List 등을 편리하게 사용할 수 있는 기능 지원
- 스프링의 메시지, 국제화 기능의 편리한 통합
- 스프링의 검증, 오류 처리 통합
- 스프링의 변환 서비스 통합(ConversionService)

**설정 방법**

SpringMVC 1에서 배웠듯이 타임리프를 스프링에서 사용하기 위해서는 타임리프 템플릿 엔진을 스프링 빈에 등록하고, 타임리프용 뷰 리졸버를 스프링 빈으로 등록해야한다.

스프링 부트는 이런 부분을 모두 자동화 해준다. `build.gradle` 에 다음 한줄을 넣어주면 Gradle은 타임리프와 관련된 라이브러리를 다운로드 받고, 스프링 부트는 앞서 설명한 타임리프와 관련된 설정용
스프링 빈을 자동으로 등록해준다.

**build.gradle**

```groovy
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

타임리프 관련 설정을 변경하고 싶으면 다음을 참고해서 `application.properties` 에 추가하면 된다.

- https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-applicationproperties.html#common-application-properties-templating

## 입력 폼 처리

![image](https://user-images.githubusercontent.com/83503188/209515311-89ea392d-7b11-4bb0-9ba1-711e81b486d1.png)

지금부터 타임리프가 제공하는 입력 폼 기능을 적용해서 기존 프로젝트의 폼 코드를 타임리프가 지원하는 기능을 사용해서 효율적으로 개선해보자.

- `th:object` : 커맨드 객체를 지정한다.
- `*{...}` : 선택 변수 식이라고 한다. `th:object` 에서 선택한 객체에 접근한다.
- `th:field`
    - HTML 태그의 id , name , value 속성을 자동으로 처리해준다.

**렌더링 전**

`<input type="text" th:field="*{itemName}" />`

**렌더링 후**

`<input type="text" id="itemName" name="itemName" th:value="*{itemName}" />`

### 등록 폼

`th:object` 를 적용하려면 먼저 해당 오브젝트 정보를 넘겨주어야 한다. 등록 폼이기 때문에 데이터가 비어있는 빈 오브젝트를 만들어서 뷰에 전달하자.

**FormItemController**

```java
@GetMapping("/add")
public String addForm(Model model){
        model.addAttribute("item",new Item());
        return"form/addForm";
        }
```

`form/addForm.html` 변경 코드 부분

```html

<form action="item.html" th:action th:object="${item}" method="post">
    <div>
        <label for="itemName">상품명</label>
        <input type="text" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
    </div>
    <div>
        <label for="price">가격</label>
        <input type="text" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
    </div>
    <div>
        <label for="quantity">수량</label>
        <input type="text" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
    </div>
```

- `th:object="${item}"` : `<form>` 에서 사용할 객체를 지정한다. 선택 변수 식( `*{...}` )을 적용할 수 있다.
- `th:field="*{itemName}"`
    - `*{itemName}` 는 선택 변수 식을 사용했는데, `${item.itemName}` 과 같다. 앞서 `th:object` 로 item 을 선택했기 때문에 선택 변수 식을 적용할 수 있다.
    - `th:field`는 id , name , value 속성을 모두 자동으로 만들어준다.
        - `id` : `th:field` 에서 지정한 변수 이름과 같다. `id="itemName"`
        - `name` : `th:field` 에서 지정한 변수 이름과 같다. `name="itemName"`
        - `value` : `th:field` 에서 지정한 변수의 값을 사용한다. `value=""`

**렌더링 전**

```html
<input type="text" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
```

**렌더링 후**

```html
<input type="text" id="itemName" class="form-control" placeholder="이름을 입력하세요" name="itemName" value="">
```

![image](https://user-images.githubusercontent.com/83503188/209517513-e0cb1d14-c93c-4fd9-b507-1cef0149605c.png)

만약에 `th:field="*{itemNamexx}"`이와 같이 변수명을 틀린 경우 기존에 `name="itemNamexx""` 는 오류를 찾을 수 없는 반면에 `th:field` 를 사용하면 뷰를 렌더링하는 과정에
변수를 바인딩할 수 없다는 오류로 오타를 검출할 수 있다.

### 수정 폼

**FormItemController**

```java
@GetMapping("/{itemId}/edit")
public String editForm(@PathVariable Long itemId,Model model){
        Item item=itemRepository.findById(itemId);
        model.addAttribute("item",item);
        return"form/editForm";
        }
```

`form/editForm.html` 변경 코드 부분

```html

<form action="item.html" th:action th:object="${item}" method="post">
    <div>
        <label for="id">상품 ID</label>
        <input type="text" id="id" th:field="*{id}" class="form-control" value="1" th:value="${item.id}" readonly>
    </div>
    <div>
        <label for="itemName">상품명</label>
        <input type="text" id="itemName" th:field="*{itemName}" class="form-control" value="상품A"
               th:value="${item.itemName}">
    </div>
    <div>
        <label for="price">가격</label>
        <input type="text" id="price" th:field="*{price}" class="form-control" value="10000" th:value="${item.price}">
    </div>
    <div>
        <label for="quantity">수량</label>
        <input type="text" id="quantity" th:field="*{quantity}" class="form-control" value="10"
               th:value="${item.quantity}">
    </div>
```

**렌더링 전**

```html
<input type="text" id="itemName" th:field="*{itemName}" class="form-control">
```

**렌더링 후**

```html
<input type="text" id="itemName" class="form-control" name="itemName" value="itemA">
```

## 요구사항 추가

타임리프를 사용해서 폼에서 체크박스, 라디오 버튼, 셀렉트 박스를 편리하게 사용하는 방법을 학습해보자.기존 상품 서비스에 다음 요구사항이 추가되었다.

- 판매 여부
    - 판매 오픈 여부
    - 체크 박스로 선택할 수 있다.
- 등록 지역
    - 서울, 부산, 제주
    - 체크 박스로 다중 선택할 수 있다.
- 상품 종류
    - 도서, 식품, 기타
    - 라디오 버튼으로 하나만 선택할 수 있다.
- 배송 방식
    - 빠른 배송
    - 일반 배송
    - 느린 배송
    - 셀렉트 박스로 하나만 선택할 수 있다.

**예시 이미지**

![image](https://user-images.githubusercontent.com/83503188/209518484-b661953d-8831-4019-8c59-7ea4c43451c3.png)

**ItemType - 상품 종류**

```java

@AllArgsConstructor
@Getter
public enum ItemType {

    BOOK("도서"), FOOD("식품"), ETC("기타");

    private final String description;

}
```

**배송 방식 - DeliveryCode**

```java
/**
 * FAST: 빠른 배송
 * NORMAL: 일반 배송
 * SLOW: 느린 배송
 */
@Data
@AllArgsConstructor
public class DeliveryCode {
    private String code;
    private String displayName;
}
```

- 배송 방식은 `DeliveryCode` 라는 클래스를 사용한다. `code` 는 FAST 같은 시스템에서 전달하는 값이고, `displayName` 은 빠른 배송 같은 고객에게 보여주는 값이다.
- DeliveryCode("FAST", "빠른 배송")

**Item - 상품**

```java

@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    private Boolean open; // 판매 여부
    private List<String> regions; // 등록 지역
    private ItemType itemType; // 상품 종류 
    private String deliveryCode;

  ...
}
```

ENUM , 클래스, String 같은 다양한 상황을 준비했다. 각각의 상황에 어떻게 폼의 데이터를 받을 수 있는지 하나씩 알아보자.

## 체크 박스 - 단일1

**단순 HTML 체크 박스**

`resources/templates/form/addForm.html` 추가

```html
        <!-- single checkbox -->
<div>판매 여부</div>
<div>
    <div class="form-check">
        <input type="checkbox" id="open" name="open" class="form-check-input">
        <label for="open" class="form-check-label">판매 오픈</label>
    </div>
</div>
```

상품이 등록되는 곳에 다음과 같이 로그를 남겨서 값이 잘 넘어오는지 확인해보자.

**FormItemController**

```java
@PostMapping("/add")
public String addItem(Item item,RedirectAttributes redirectAttributes){
        log.info("item.open={}",item.getOpen());
        ...
        }
```

**실행 로그**

![image](https://user-images.githubusercontent.com/83503188/209520081-3faa5c78-ca0b-4ce6-92ba-f34f9ae367cd.png)
- 체크 박스를 체크하면 HTML Form에서 `open=on` 이라는 값이 넘어간다. 스프링은 on 이라는 문자를 true 타입으로 변환해준다.
- HTML에서 체크 박스를 선택하지 않고 폼을 전송하면 open 이라는 필드 자체가 서버로 전송되지 않는다.

**HTTP 요청 메시지 로깅**

HTTP 요청 메시지를 서버에서 보고 싶으면 다음 설정을 추가하면 된다.

`application.properties`

```text
logging.level.org.apache.coyote.http11=debug
```

![image](https://user-images.githubusercontent.com/83503188/209520500-7fef47f4-6a4c-4687-8df2-6f4d56f26150.png)
- HTTP 메시지 바디를 보면 open 의 이름도 전송이 되지 않는 것을 확인할 수 있다.


HTML checkbox는 선택이 안되면 클라이언트에서 서버로 값 자체를 보내지 않는다. 수정의 경우에는 상황에 따라서 이 방식이 문제가 될 수 있다.
사용자가 의도적으로 체크되어 있던 값을 체크를 해제해도 저장시 아무 값도 넘어가지 않기 때문에, 서버 구현에 따라서 값이 오지 않은 것으로 판단해서 값을 변경하지 않을 수도 있다.

이런 문제를 해결하기 위해서 스프링 MVC는 약간의 트릭을 사용하는데, 히든 필드를 하나 만들어서,`_open` 처럼 기존 체크 박스 이름 앞에 언더스코어( `_` )를 붙여서 전송하면 체크를 해제했다고 인식할 수 있다.
히든 필드는 항상 전송된다. 따라서 체크를 해제한 경우 여기에서 `open` 은 전송되지 않고, `_open` 만 전송되는데, 이 경우 스프링 MVC는 체크를 해제했다고 판단한다.

**체크 해제를 인식하기 위한 히든 필드**

`<input type="hidden" name="_open" value="on"/>`

**기존 코드에 히든 필드 추가**

```html
        <!-- single checkbox -->
        <div>판매 여부</div>
        <div>
            <div class="form-check">
                <input type="checkbox" id="open" name="open" class="form-check-input">
                <input type="hidden" name="_open" value="on"> <!-- 히든 필드 추가 -->
                <label for="open" class="form-check-label">판매 오픈</label>
            </div>
        </div>
```

**실행 로그**

![image](https://user-images.githubusercontent.com/83503188/209521110-5a72de7b-977d-4d9e-a710-dd48829a2059.png)

**체크 박스 체크**

![image](https://user-images.githubusercontent.com/83503188/209521423-ce9009f0-2097-45f4-9f54-1e749900e2fa.png)
- `open=on&_open=on`
- 체크 박스를 체크하면 스프링 MVC가 `open` 에 값이 있는 것을 확인하고 사용한다. 이때 `_open` 은 무시한다.

**체크 박스 미체크**

![image](https://user-images.githubusercontent.com/83503188/209521529-acbac664-5bc5-4714-80e4-7282fd1fa86a.png)
- `_open=on`
- 체크 박스를 체크하지 않으면 스프링 MVC가 `_open` 만 있는 것을 확인하고, `open` 의 값이 체크되지 않았다고 인식한다.
- 이 경우 서버에서 `Boolean` 타입을 찍어보면 결과가 `null` 이 아니라 `false` 인 것을 확인할 수 있다.

## 체크 박스 - 단일2

개발할 때 마다 이렇게 히든 필드를 추가하는 것은 상당히 번거롭다. 타임리프가 제공하는 폼 기능을 사용하면 이런 부분을 자동으로 처리할 수 있다.

**타임리프 - 체크 박스 코드 추가**

```html
        <!-- single checkbox -->
        <div>판매 여부</div>
        <div>
            <div class="form-check">
                <input type="checkbox" id="open" th:field="*{open}" class="form-check-input">
                <label for="open" class="form-check-label">판매 오픈</label>
            </div>
        </div>
```

**타임리프 체크 박스 HTML 생성 결과**

![image](https://user-images.githubusercontent.com/83503188/209522801-1862c8ee-85ec-4194-b509-232375704625.png)
- `<input type="hidden" name="_open" value="on"/>`

**상품 상세에 적용**

`item.html`

```html
    <!-- single checkbox -->
    <div>판매 여부</div>
    <div>
        <div class="form-check">
            <input type="checkbox" id="open" th:field="${item.open}" class="form-check-input" disabled>
            <label for="open" class="form-check-label">판매 오픈</label>
        </div>
    </div>
```
- `item.html` 에는 `th:object` 를 사용하지 않았기 때문에 `th:field` 부분에 `${item.open}` 으로 적어주어야 한다.
- `disabled` 를 사용해서 상품 상세에서는 체크 박스가 선택되지 않도록 했다.

**결과**
![image](https://user-images.githubusercontent.com/83503188/209523164-9079f91d-8379-4c56-976b-3724cfb100bc.png)

![image](https://user-images.githubusercontent.com/83503188/209523185-af41d84e-04f4-4b51-a4e1-b3cf0353b8f9.png)
- `checked="checked"`
- 체크 박스에서 판매 여부를 선택해서 저장하면, 조회시에 `checked` 속성이 추가된 것을 확인할 수 있다.
- 이런 부분을 개발자가 직접 처리하려면 상당히 번거롭다. 타임리프의 `th:field` 를 사용하면, 값이 `true` 인 경우 체크를 자동으로 처리해준다.

**상품 수정에 적용**

`editForm.html`

```html
        <!-- single checkbox -->
        <div>판매 여부</div>
        <div>
            <div class="form-check">
                <input type="checkbox" id="open" th:field="*{open}" class="form-check-input">
                <label for="open" class="form-check-label">판매 오픈</label>
            </div>
        </div>
```

**ItemRepository - update()**

```java
public void update(Long itemId, Item updateParam) {
    Item findItem = findById(itemId);
    findItem.setItemName(updateParam.getItemName());
    findItem.setPrice(updateParam.getPrice());
    findItem.setQuantity(updateParam.getQuantity());
    findItem.setOpen(updateParam.getOpen());
    findItem.setRegions(updateParam.getRegions());
    findItem.setItemType(updateParam.getItemType());
    findItem.setDeliveryCode(updateParam.getDeliveryCode());
}
```

## 체크 박스 - 멀티

체크 박스를 멀티로 사용해서, 하나 이상을 체크할 수 있도록 해보자.

- 등록 지역
  - 서울, 부산, 제주
  - 체크 박스로 다중 선택할 수 있다.

**FormItemController**

```java
@ModelAttribute("regions")
public Map<String, String> regions() {
    Map<String, String> regions = new LinkedHashMap<>();
    regions.put("SEOUL", "서울");
    regions.put("BUSAN", "부산");
    regions.put("JEJU", "제주");
    return regions;
}
```
- LinkedHashMap 을 사용해야지 순서를 보장할 수 있다.

**@ModelAttribute의 특별한 사용법**

등록 폼, 상세화면, 수정 폼에서 모두 서울, 부산, 제주라는 체크 박스를 반복해서 보여주어야 한다. 이렇게 하려면 각각의 컨트롤러에서 `model.addAttribute(...)` 을 사용해서 체크 박스를 구성하는 데이터를 반복해서 넣어주어야 한다.

`@ModelAttribute` 는 이렇게 컨트롤러에 있는 별도의 메서드에 적용할 수 있다.

이렇게하면 해당 컨트롤러를 요청할 때 `regions` 에서 반환한 값이 자동으로 모델( `model` )에 담기게 된다.
물론 이렇게 사용하지 않고, 각각의 컨트롤러 메서드에서 모델에 직접 데이터를 담아서 처리해도 된다.

성능 최적화하는 부분을 고민해봐야 한다. 미리 static 영역에 만들어놓고 불러쓰는 방식


`addForm.html`

```html
        <!-- multi checkbox -->
        <div>
            <div>등록 지역</div>
            <div th:each="region : ${regions}" class="form-check form-check-inline">
                <input type="checkbox" th:field="*{regions}" th:value="${region.key}" class="form-check-input">
                <label th:for="${#ids.prev('regions')}" th:text="${region.value}" class="form-check-label">서울</label>
            </div>
        </div>
```
- `th:for="${#ids.prev('regions')}"`
- - 멀티 체크박스는 같은 이름의 여러 체크박스를 만들 수 있다. 그런데 문제는 이렇게 반복해서 HTML 태그를 생성할 때, 생성된 HTML 태그 속성에서 `name` 은 같아도 되지만, `id` 는 모두 달라야 한다.
  - 타임리프는 체크박스를 `each` 루프 안에서 반복해서 만들 때 임의로 1 , 2 , 3 숫자를 뒤에 붙여준다.

**결과**

![image](https://user-images.githubusercontent.com/83503188/209525498-e0c6fd04-6e7a-4b98-bfd0-b890c7381d06.png)

![image](https://user-images.githubusercontent.com/83503188/209525527-bb405073-41a0-496f-a9fd-e86098c99906.png)
- 타임리프는 `ids.prev(...)` , `ids.next(...)` 을 제공해서 동적으로 생성되는 id 값을 사용할 수 있도록 한다.

**로그 출력**

**FormItemController.addItem()**

```java
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        log.info("item.open={}", item.getOpen());
        log.info("item.region={}", item.getRegions());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/form/items/{itemId}";
    }

```

**서울, 부산 선택**

![image](https://user-images.githubusercontent.com/83503188/209526185-fb726d3f-9e81-4c4d-a430-4c6656fe4da5.png)

**지역 선택X**

![image](https://user-images.githubusercontent.com/83503188/209526318-9e2408ee-0171-4751-b44f-0c7c723f2d85.png)

**item.html - 추가**

```html
    <!-- multi checkbox -->
    <div>
        <div>등록 지역</div>
        <div th:each="region : ${regions}" class="form-check form-check-inline">
            <input type="checkbox" th:field="${item.regions}" th:value="${region.key}" class="form-check-input" disabled>
            <label th:for="${#ids.prev('regions')}" th:text="${region.value}" class="form-check-label">서울</label>
        </div>
    </div>
```

![image](https://user-images.githubusercontent.com/83503188/209526481-839cb625-f6c8-4443-93c5-7cd592075ed6.png)

![image](https://user-images.githubusercontent.com/83503188/209526515-f675bc15-c71f-42ee-a572-d553ff71d3d6.png)
- `checked="checked"`
- 타임리프는 `th:field` 에 지정한 값과 `th:value` 의 값을 비교해서 체크를 자동으로 처리해준다.
  - item.regions=[SEOUL, BUSAN]
  - th:field -> SEOUL, BUSAN / th:value -> SEOUL, BUSAN, JEJU

**editForm.html - 추가**

```html
        <!-- multi checkbox -->
        <div>
            <div>등록 지역</div>
            <div th:each="region : ${regions}" class="form-check form-check-inline">
                <input type="checkbox" th:field="${item.regions}" th:value="${region.key}" class="form-check-input">
                <label th:for="${#ids.prev('regions')}"
                       th:text="${region.value}" class="form-check-label">서울</label>
            </div>
        </div>
```

## 라디오 버튼 

- 상품 종류
  - 도서, 식품, 기타
  - 라디오 버튼으로 하나만 선택할 수 있다.

**FormItemController**

```java
    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        return ItemType.values();
    }
```
- `ItemType.values()` 를 사용하면 해당 ENUM의 모든 정보를 배열로 반환한다. 
  - 예) [BOOK, FOOD, ETC]

**addForm.html - 추가**

```html
        <!-- radio button -->
        <div>
            <div>상품 종류</div>
            <div th:each="type : ${itemTypes}" class="form-check form-check-inline">
                <input type="radio" th:field="*{itemType}" th:value="${type.name()}" class="form-check-input">
                <label th:for="${#ids.prev('itemType')}" th:text="${type.description}" class="form-check-label">BOOK</label>
            </div>
        </div>
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/209539786-bf8f44fa-8075-4444-ac14-9c23da142502.png)

![image](https://user-images.githubusercontent.com/83503188/209539933-a652a0df-0712-4d9f-998e-20baf72a13a6.png)

![image](https://user-images.githubusercontent.com/83503188/209540024-9b69b274-5412-4c08-8cfe-04dfbe3ece7d.png)
- 히든 필드가 필요한 이유는 기존에 선택되었던 값이 수정시에 변경 여부를 파악하기 위함인데,
- 라디오 버튼은 이미 선택이 되어 있다면, 수정시에도 항상 하나를 선택하도록 되어 있으므로 체크 박스와 달리 별도의 히든 필드를 사용할 필요가 없다. -> 반드시 하나의 값이 넘어가기 때문 

**item.html**

```html
    <!-- radio button -->
    <div>
        <div>상품 종류</div>
        <div th:each="type : ${itemTypes}" class="form-check form-check-inline">
            <input type="radio" th:field="${item.itemType}" th:value="${type.name()}" class="form-check-input" disabled>
            <label th:for="${#ids.prev('itemType')}" th:text="${type.description}"
                   class="form-check-label">
                BOOK
            </label>
        </div>
    </div>
```

**editForm.html**

```html
        <!-- radio button -->
        <div>
            <div>상품 종류</div>
            <div th:each="type : ${itemTypes}" class="form-check form-check-inline">
                <input type="radio" th:field="*{itemType}" th:value="${type.name()}"
                       class="form-check-input">
                <label th:for="${#ids.prev('itemType')}" th:text="${type.description}"
                       class="form-check-label">
                    BOOK
                </label>
            </div>
        </div>
```

### 타임리프에서 ENUM 직접 사용하기

**타임리프에서 ENUM 직접 접근**

```html
<div th:each="type : ${T(hello.itemservice.domain.item.ItemType).values()}">
```

- `${T(hello.itemservice.domain.item.ItemType).values()}` 스프링EL 문법으로 ENUM을 직접 사용할 수 있다. ENUM에 `values()` 를 호출하면 해당 ENUM의 모든 정보가 배열로 반환된다.
- 그런데 이렇게 사용하면 ENUM의 패키지 위치가 변경되거나 할때 자바 컴파일러가 타임리프까지 컴파일 오류를 잡을 수 없으므로 추천하지는 않는다.

## 셀릭트 박스

- 배송 방식
  - 빠른 배송
  - 일반 배송
  - 느린 배송
  - 셀렉트 박스로 하나만 선택할 수 있다.

**FormItemController - 추가**

```java
    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }
```

**addForm.html - 추가**

```html
        <!-- SELECT -->
        <div>
            <div>배송 방식</div>
            <select th:field="*{deliveryCode}" class="form-select">
                <option value="">==배송 방식 선택==</option>
                <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                        th:text="${deliveryCode.displayName}">FAST</option>
            </select>
        </div>
        <hr class="my-4">
```

![image](https://user-images.githubusercontent.com/83503188/209540839-cdfd7082-e7d4-4b37-a5c4-50ccf2f34169.png)

**타임리프로 생성된 HTML**

![image](https://user-images.githubusercontent.com/83503188/209540870-82406499-4455-4fcf-8097-7576be1ede82.png)

**item.html**

```html
    <!-- SELECT -->
    <div>
        <div>배송 방식</div>
        <select th:field="${item.deliveryCode}" class="form-select" disabled>
            <option value="">==배송 방식 선택==</option>
            <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                    th:text="${deliveryCode.displayName}">FAST</option>
        </select>
    </div>
```

**editForm.html**

```html
        <!-- SELECT -->
        <div>
            <div>배송 방식</div>
            <select th:field="*{deliveryCode}" class="form-select">
                <option value="">==배송 방식 선택==</option>
                <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                        th:text="${deliveryCode.displayName}">FAST</option>
            </select>
        </div>
        <hr class="my-4">
```

![image](https://user-images.githubusercontent.com/83503188/209541040-d52e8777-c267-4bf6-b9b2-67f1edf4ebb2.png)
- `selected="selected"`

