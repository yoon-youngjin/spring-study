# 테스트는 왜 필요할까?

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/ded4809f-1843-489b-a0a1-c29ccb940058/Untitled.png)

확장되는 애플리케이션에서 사람이 수동적으로 하나씩 테스트를 진행한다면 인적자원은 무한정 늘릴 수 있지 않으며, 사람의 경험과 감에 의존하게 되며, 사람이 테스트하기 때문에 늦은 피드백, 유지보수가 어려워지며 이에 따라 소프트웨어 신뢰가 떨어집니다. 

**테스트를 통해 얻고자 하는 것**

- 빠른 피드백 : 내가 의도한 바대로 동작하는지에 대한
- 테스트 자동화
- 안정감, 신뢰성

**테스트 코드를 작성하지 않는다면**

- 변화가 생기는 매순간마다 발생할 수 있는 모든 Case를 고려해야 한다.
- 변화가 생기는 매순간마다 모든 팀원이 동일한 고민을 해야 한다.
- 빠르게 변화하는 소프트웨어의 안정성을 보장할 수 없다.

### 올바른 테스트 코드

테스트 코드가 잘못된다면 애플리케이션의 안정성을 제공하기 힘들어지며, 테스트 코드 자체가 유지보수하기 어려운, 새로운 짐이 됩니다. 또한 명확하지 않은 테스트로 인해 잘못된 검증이 이루어질 가능성이 생깁니다.

**올바른 테스트 코드는**

- 자동화 테스트로 비교적 빠른 시간 안에 버그를 발견할 수 있고, 수동 테스트에 드는 비용을 크게 절약할 수 있다.
- 소프트웨어의 빠른 변화를 지원한다.
- 내가 고민한 내용을 테스트 코드로 남김으로써 팀내의 공유지식으로 남길 수 있다.
- 가까이 보면 느리지만, 멀리 보면 가장 빠르다.

## 단위 테스트

### 수동테스트 vs. 자동화된 테스트

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

### JUnit5로 테스트하기

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
