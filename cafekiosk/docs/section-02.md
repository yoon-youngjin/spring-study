# 단위 테스트

## 수동테스트 vs. 자동화된 테스트

```java
class CafeKioskTest {

    @Test
    void add() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        System.out.println(">>> 담긴 음료 수 : " + cafeKiosk.getBeverages().size());
        System.out.println(">>> 담긴 음료 : " + cafeKiosk.getBeverages().get(0).getName());
    }

}
```
테스트 코드를 작성했지만 결과적으로 개발자가 직접 콘솔을 통해 수동적으로 결과를 확인한다. 자동화 테스트 도구인 JUnit5를 사용해보자.

## JUnit5로 테스트하기

**단위 테스트** 

- **작은** 코드 단위(클래스 or 메서드)를 **독립적**으로 검증하는 테스트
- 검증 속도가 빠르고, 안정적이다.

**JUnit5**

- 단위 테스트를 위한 테스트 프레임워크
- Kent Back이 개발한 SUnit 시리즈 중 Java를 위한 툴

**AssertJ**

- 테스트 코드 작성을 원활하게 돕는 테스트 라이브러리
- 풍부한 API, 메서드 체이닝((xxx).(xxx)) 지원

`testImplementation 'org.springframework.boot:spring-boot-starter-test'`

해당 의존성에 JUnit5, AssertJ가 포함되어있다.

```java
class CafeKioskTest {

  ...

    @Test
    void add() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        assertThat(cafeKiosk.getBeverages()).hasSize(1);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }

    @Test
    void remove() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);
        assertThat(cafeKiosk.getBeverages().size()).isEqualTo(1);

        cafeKiosk.remove(americano);
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

    @Test
    void clear() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();

        cafeKiosk.add(americano);
        cafeKiosk.add(latte);

        assertThat(cafeKiosk.getBeverages().size()).isEqualTo(2);

        cafeKiosk.clear();
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }



}
```

JUnit5, AssertJ를 사용한다면 자동으로 테스트가 성공하는지 실패하는지를 확인할 수 있다. 위와 같은 단위 테스트가 꼼꼼히 작성된다면 실제 구현 내용이 변경되더라도 테스트 코드를 통해 현재 애플리케이션이 정상적으로 동작하고 있는지 빠르게 파악할 수 있다.

## 테스트 케이스 세분화하기

**요구사항**

- 한 종류의 음료 여러 잔을 한 번에 담는 기능

### 테스트 케이스 세분화

- 해피 케이스 : 요구사항을 그대로 만족하는 케이스
- 예외 케이스 : 요구사항에 드러나지 않은 예외 케이스

이러한 테스트를 할 때는 **경계값 테스트**(범위 - (이상, 이하, 초과, 미만), 구간, 날짜 …)가 중요하다. 

```java
// 해피 케이스
    @Test
    void addSeveralBeverages() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano, 2);

        assertThat(cafeKiosk.getBeverages()).hasSize(2);
        assertThat(cafeKiosk.getBeverages().get(0)).isEqualTo(americano);
        assertThat(cafeKiosk.getBeverages().get(1)).isEqualTo(americano);
    }

    // 예외 케이스
    @Test
    void addZeroBeverages() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        assertThatThrownBy(() -> cafeKiosk.add(americano, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔 이상 주문하실 수 있습니다.");

    }
```

## 테스트하기 어려운 영역을 분리하기

**요구사항**

- 가게 운영 시간(10:00~22:00) 외에는 주문을 생성할 수 없다.

```java
public Order createOrder() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime currentTime = currentDateTime.toLocalTime();
        if (currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의하세요.");
        }
        
        return new Order(currentDateTime, beverages);
    }
```

```java
@Test
    void createOrder() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);

        Order order = cafeKiosk.createOrder();

        assertThat(order.getBeverages()).hasSize(1);
        assertThat(order.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }
```

위와 같은 테스트는 항상 통과하는 테스트가 아니다. 현재 테스트하는 시간을 가지고 예외를 던지기 때문이다. 예를 들어 테스트하는 시간이 10:00 ~ 22:00가 아니라면 테스트에 성공할 수 없다. 

이러한 문제를 해결하기 위해서는 파라미터로 시간을 받을 수 있다.

```java
public Order createOrder(LocalDateTime currentDateTime) {
        LocalTime currentTime = currentDateTime.toLocalTime();
        if (currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의하세요.");
        }

        return new Order(currentDateTime, beverages);
    } 
```

```java
@Test
void createOrderWithCurrentTime() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);

        Order order = cafeKiosk.createOrder(LocalDateTime.of(2023, 1, 17, 10, 0));

        assertThat(order.getBeverages()).hasSize(1);
        assertThat(order.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }

    @Test
    void createOrderOutsideOpenTime() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);

        assertThatThrownBy(() -> cafeKiosk.createOrder(LocalDateTime.of(2023, 1, 17, 9, 59)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 시간이 아닙니다. 관리자에게 문의하세요.");
    }
```

### 테스트하기 어려운 영역을 구분하고 분리하기

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7f40d740-2314-45a6-a7f0-29747144e5e3/Untitled.png)

- 위의 예제에서 실질적으로 테스트 하고자 하는 영역은 현재 시간이 아닌 특정 시간이 주어졌을때 조건에 만족하는지 안하는지 여부이다.

**테스트하기 어려운 영역**

- 관측할 때마다 다른 값에 의존하는 코드 - 현재 날짜/시간, 랜덤 값, 전역 변수/함수, 사용자 입력 등, ..
- 외부 세계에 영향을 주는 코드 - 표준 출력(로깅), 메시지 발송, 데이터베이스에 기록하기 등

**순수함수(테스트하기 쉬운 영역)**

- 같은 입력에는 항상 같은 결과
- 외부 세상과 단절된 형태
