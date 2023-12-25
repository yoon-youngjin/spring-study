## ThreadLocal 주의사항

쓰레드 로컬의 값을 사용 후 제거하지 않고 그냥 두면 WAS(톰캣)처럼 쓰레드 풀을 사용하는 경우 심각한 문제가 발생할 수 있다.

**사용자A 저장 요청**

<img width="662" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/779912da-11cb-47af-9b1f-56ba964486af">

1. 사용자A가 저장 HTTP 요청
2. WAS는 쓰레드 풀에서 쓰레드 하나를 조회
3. 쓰레드(thread-A)가 할당되어 사용자A의 데이터를 쓰레드 로컬에 저장
4. 쓰레드 로컬의 thread-A 전용 보관소에 사용자A 데이터를 보관 (remove X)

**사용자A 저장 요청 종료**

<img width="638" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/625b6583-ebe8-41ea-b092-e25e160d0a4d">

1. 사용자A의 HTTP 응답 완료
2. WAS는 사용이 끝난 thread-A를 쓰레드 풀에 반환한다.
3. thread-A는 쓰레드풀에 아직 존재하고, 따라서 쓰레드 로컬의 thread-A 전용 보관소에 사용자A 데이터도 함께 존재

**사용자B 조회 요청**

<img width="669" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/0988e3f1-7425-4864-9c1f-e3b7aea7e7f5">

1. 사용자B가 조회를 위한 새로운 HTTP 요청
2. WAS는 쓰레드 풀에서 쓰레드를 조회하는데 이때 thread-A가 할당
3. 조회 요청에 의해 thread-A는 쓰레드 로컬에서 데이터를 조회
4. 쓰레드 로컬은 thread-A 전용 보관소에 있는 사용자A 데이터를 반환한다
5. 결과적으로 사용자B는 사용자A의 정보를 조회한다

이런 문제를 예방하려면 사용자A의 요청이 끝날 때 쓰레드 로컬의 값을 `ThreadLocal.remove()` 를 통해서 꼭 제 거해야 한다.

## 전략 패턴

템플릿 메서드 패턴은 부모 클래스에 변하지 않는 템플릿을 두고, 변하는 부분은 자식 클래스에 두어서 상속을 사용해서 문제를 해결했다.
전략 패턴은 변하지 않는 부분은 Context라는 곳에 두고, 변하는 부분을 Strategy라는 인터페이스를 만들고 해당 인터페이스를 구현하도록 해서 문제를 해결한다. (즉, 상속이 아닌 위임으로 문제를 해결)

전략 패턴에서 Context는 변하지 않는 템플릿 역할을 하고, Strategy는 변하는 알고리즘 역할을 한다.

<img width="503" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/ad6aba73-3a6b-4d84-a470-dcdaa4d78355">

```kotlin
interface Strategy {
    fun call()
}

class StrategyLogic1: Strategy {
    override fun call() {
        logger.info("비즈니스 로직1 실행")
    }
}
```

```kotlin
class ContextV1(
    private val strategy: Strategy,
) {
    fun execute() {
        val startTime = System.currentTimeMillis()
        // 비즈니스 로직 실행
        strategy.call() // 위임
        // 비즈니스 로직 종료
        val endTime = System.currentTimeMillis()
        logger.info("resultTime=${endTime - startTime}")
    }
}
```

ContextV1은 변하지 않는 로직을 가지고 있는 템플릿 역할을 하는 코드이다.
전략 패턴에서는 이것을 컨텍스트(문맥)이라 한다.

쉽게 컨텍스트(문맥)는 크게 변하지 않지만, 그 문맥 속에서 strategy를 통해 일부 전략이 변경된다고 생각하면 된다.

Context는 내부에 strategy 필드를 가지고 있다. 이 필드에 변하는 부분인 Strategy 구현체를 주입하면 된다.
전략 패턴의 핵심은 Context는 Strategy 인터페이스에만 의존한다는 점이다. 덕분에 Strategy의 구현체를 변경하거나 새로 만들어도 Context 코드에는 영향을 주지 않는다.

**전략 패턴 실행 그림**

<img width="637" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/430733c9-e131-4815-8019-a189626306ac">

1. Context에 원하는 Strategy 구현체를 주입한다. (조립 완료)
2. 클라이언트는 context를 실행
3. context는 context 로직 수행
4. context 로직 중간에 strategy.call() 호출해서 주입 받은 strategy 로직 실행
5. context는 나머지 로직 실행

템플릿 메서드 패턴은 상속을 사용함으로써 부모 클래스의 변경에 영향을 끼친다. 또한, 자식 클래스는 무거운 부모 클래스의 정보를 모두 아는 상황이다.
반면에 전략 패턴은 해당 인터페이스만 의존함으로써 변경에 영향을 덜 받는다. (Context 와는 완전히 분리) 

이러한 전략 패턴의 단점은 Context와 Strategy를 조립한 이후에는 전략을 변경하기가 번거롭다는 점이다.
물론 Context에 setter를 제공해서 Strategy를 넘겨 받아 변겨하면 되지만, Context를 싱글톤으로 사용할 때는 동시성 이슈 등 고려할 점이 많다.
그래서 전략을 실시간으로 변경해야 하면 차라리 Context를 하나 더 생성하고 그곳에 다른 Strategy를 주입하는 것이 더 나은 선택일 수 있다.

### 전략 패턴 파라미터로 전달 받기

```kotlin
class ContextV2 {
    fun execute(strategy: Strategy) {
        val startTime = System.currentTimeMillis()
        // 비즈니스 로직 실행
        strategy.call() // 위임
        // 비즈니스 로직 종료
        val endTime = System.currentTimeMillis()
        logger.info("resultTime=${endTime - startTime}")
    }
}
```

Context와 Strategy를 선 조립 후 실행하는 방식이 아니라 Context를 실행할 때 마다 전략을 인수로 전달한다.
클라이언트는 Context를 실행하는 시점에 원하는 Strategy를 전달할 수 있다. 따라서 이전 방식과 비교해서 원하는 전략을 더욱 유연하게 변경할 수 있다.

<img width="663" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/85ace9f1-dae4-4159-8735-510f51f35bd8">

1. 클라이언트는 Context를 실행하면서 인수로 Strategy를 전달
2. Context는 execute() 로직을 실행
3. Context는 파라미터로 넘어온 strategy.call() 로직 실행
4. Context의 execute() 로직 종료

디자인 패턴은 전체 그림이 중요한게 아니라 의도가 중요하다.
전략 패턴의 의도는 다음과 같다.

> 알고리즘 제품군을 정의하고 각각을 캡슐화하여 상호 교환 가능하게 만들자. 전략을 사용하면 알고리즘을 사용하는 클라이언트와 독립적으로 알고리즘을 변경할 수 있다.

## 템플릿 콜백 패턴

ContextV2는 변하지 않는 템플릿 역할을 한다. 그리고 변하는 부분은 파라미터로 넘어온 Strategy의 코드를 실행해서 처리한다.
이렇게 다른 코드의 인수로서 넘겨주는 실행 가능한 코드를 콜백(callback)이라 한다. (즉 ContextV2에서 콜백은 Strategy)

- 스프링에서는 ContextV2와 같은 방식의 전략 패턴을 템플릿 콜백 패턴이라 한다. (전략 패턴에서 Context가 템플릿 역할을 하고, Strategy 부분이 콜백으로 넘어온다 생각)
  - 참고로 템플릿 콜백 패턴은 GOF 패턴은 아니고, 스프링 내부에서 이런 방식을 자주 사용하기 땨문에, 스프링 안에서만 이렇게 부른다. 전략 패턴에서 템플릿과 콜백 부분이 강조된 패턴이라고 생각
- 스프링에서는 'JdbcTemplate', 'RestTemplate', 'TransactionTemplate', 'RedisTemplate' 처럼 다양한 템플릿 콜백 패턴이 사용된다.
  - 스프링에서 xxxTemplate은 템플릿 콜백 패턴으로 만들어져 있다 생각하면 된다.

- Context -> Template
- Strategy -> Callback