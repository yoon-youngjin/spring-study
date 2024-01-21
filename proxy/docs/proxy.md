# 프록시, 프록시 패턴과 데코레이터 패턴 소개

요청하는 객체를 클라이언트라고 하며, 요청을 처리하는 객체는 서버라고 표현할 수 있다.

**직접 호출과 간접 호출**

<img width="617" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/c3453735-ec14-4089-8213-6cc0f581bcab">

클라이언트와 서버 개념에서 일반적으로 클라이언트가 서버를 직접 호출하고, 처리 결과를 직접 받는다.
이것을 직접 호출이라 한다.

<img width="603" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/60e8c231-00e9-4695-9870-97cbd62276ec">

그런데 클라이언트가 요청한 결과를 서버에 직접 요청하는 것이 아니라 어떤 대리자를 통해서 대신 간접적으로 서버에 요청할 수 있다.
이때 대리자를 영어로 프록시라 한다.

**이렇게 직접 호출과 다르게 간접 호출을 하면 대리자(프록시)가 중간에 여러가지 일을 할 수 있다.**

**예시**

- 엄마에게 라면을 사달라고 부탁 했는데, 엄마는 그 라면이 집에 있다고 할 수도 있다. 그러면 기대한 것 보다 더 빨리 라면을 먹을 수 있다. (**접근 제어, 캐싱**)
- 아버지께 자동차 주유를 부탁했는데, 아버지가 주유 뿐만 아니라 세차까지 하고 왔다. 클라이언트가 기대한 것 외에 세차라는 부가 기능까지 얻게 되었다. (**부가 기능 추가**)
- 대리가가 또 다른 대리자를 부를 수도 있다. 예를 들어 내가 동생에게 라면을 사달라고 했는데, 동생은 또 다른 누군가에게 라면을 사달라고 다시 요청할 수도 있다. 중요한 점은 클라이언트는 대리자를 통해서 요청했기
  때문에 그 이후 과정은 모른다는 점이다. 동생을 통해서 라면이 나에게 도착하기만 하면 된다. (**프록시 체인**)

**대체 가능**

<img width="659" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/e915f58b-70d0-4e33-b3c6-7c2cd85f3e11">

객체에서 프록시가 되려면, 클라이언트는 서버에게 요청을 한 것인지, 프록시에게 요청을 한 것인지 조차 몰라야 한다.
쉽게 이야기해서 서버와 프록시는 같은 인터페이스를 사용해야 한다. 그리고 클라이언트가 사용하는 서버 객체를 프록시 객체로 변경해도 클라이언트 코드를 변경하지 않고 동작할 수 있어야 한다.

클래스 의존관계를 보면 클라이언트는 서버 인터페이스(Server Interface)에만 의존한다. 그리고 서버와 프록시가 같은 인터페이스를 사용한다. 따라서 DI를 사용해서 대체가 가능하다.

<img width="621" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/23babb2f-33f5-4d44-ac44-501b54967f82">

<img width="624" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/f74715c8-8750-4776-9daf-793c7e69be4a">

런타임 의존 관계를 보면, 클라이언트 객체에 DI를 사용해서 Client -> Server에서 Client -> Proxy로 객체 의존관계를 변경해도 클라이언트 코드를 전혀 변경하지 않아도 된다. (클라이언트
입장에서는 변경 사실 조차 모름)

**프록시의 주요 기능**

- 접근 제어
    - 권한에 따른 접근 차단
    - 캐싱
    - 지연 로딩
- 부가 기능 추가
    - 원래 서버가 제공하는 기능에 더해서 부가 기능을 수행한다.
    - 예) 요청 값이나, 응답 값을 중간에 변형한다.
    - 예) 실행 시간을 측정해서 추가 로그를 남긴다.

> GOP 디자인 패턴에서는 의도에 따라서 프록시 패턴과 데코레이터 패턴으로 구분한다.
>
> 프록시 패턴: 접근 제어가 목적 / 데코레이터 패턴: 새로운 기능 추가가 목적

> 참고로 프록시 패턴은 프록시를 사용하는 여러 패턴 중 하나일뿐이다

### 프록시 패턴 예제

```kotlin
interface Subject {
    fun operation(): String
}

class RealSubject : Subject {
    override fun operation(): String {
        logger.info("실제 객체 호출")
        Thread.sleep(1000)
        return "test"
    }
}

class ProxyPatternClient(
    private val subject: Subject,
) {
    fun execute() {
        subject.operation()
    }
}
```

```kotlin
class CacheProxy(
    private val target: Subject,
) : Subject {
    private var cacheValue: String? = null
    override fun operation(): String {
        logger.info("프록시 호출")
        if (cacheValue == null) {
            cacheValue = target.operation()
        }
        return cacheValue!!
    }
}
```

```kotlin
internal class CacheProxyPatternTest {

    @Test
    fun noProxyTest() {
        val realSubject = RealSubject()
        val client = ProxyPatternClient(realSubject)
        client.execute()
        client.execute()
        client.execute()
    }

    @Test
    fun proxyTest() {
        val cacheProxy = CacheProxy(RealSubject())
        val client = ProxyPatternClient(cacheProxy)
        client.execute()
        client.execute()
        client.execute()
    }
}
```

**결과**

```text
18:03:52.581 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy -- 프록시 호출
18:03:52.588 [Test worker] INFO hello.proxy.pureproxy.proxy.code.RealSubject -- 실제 객체 호출
18:03:53.593 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy -- 프록시 호출
18:03:53.594 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy -- 프록시 호출
```

클라이언트 입장에서는 코드 변경없이 프록시를 사용할 수 있다.

### 데코레이터 패턴 예제

```kotlin
interface Component {
    fun operation(): String
}

class RealComponent : Component {
    override fun operation(): String {
        logger.info("RealComponent 실행")
        return "data"
    }
}

class DecoratorPatternClient(
    private val component: Component,
) {
    fun execute() {
        val result = component.operation()
        logger.info("result=${result}")
    }
}
```

```kotlin
class MessageDecorator(
    private val component: Component,
) : Component {
    override fun operation(): String {
        logger.info("MessageDecorator 실행");
        val result = component.operation()
        val decoResult = "*****$result*****"

        logger.info(
            "MessageDecorator 꾸미기 적용 전=$result, 적용 후=$decoResult"
        )
        return decoResult
    }
}
```

```kotlin
internal class DecoratorPatternTest {

    @Test
    fun noDecoratorTest() {
        val realComponent = RealComponent()
        val client = DecoratorPatternClient(realComponent)
        client.execute()
    }

    @Test
    fun decoratorTest() {
        val messageDecorator = MessageDecorator(RealComponent())
        val client = DecoratorPatternClient(messageDecorator)
        client.execute()
    }
}
```

**결과**

```text
18:18:41.506 [Test worker] INFO hello.proxy.pureproxy.decorator.code.RealComponent -- MessageDecorator 실행
18:18:41.510 [Test worker] INFO hello.proxy.pureproxy.decorator.code.RealComponent -- RealComponent 실행
18:18:41.511 [Test worker] INFO hello.proxy.pureproxy.decorator.code.RealComponent -- MessageDecorator 꾸미기 적용 전=data, 적용 후=*****data*****
18:18:41.512 [Test worker] INFO hello.proxy.pureproxy.decorator.code.DecoratorPatternClient -- result=*****data*****
```

**프록시 체인 적용**

```kotlin
class TimeDecorator(
    private val component: Component,
) : Component {
    override fun operation(): String {
        logger.info("TimeDecorator 실행")
        val startTime = System.currentTimeMillis()
        val result = component.operation()
        val endTime = System.currentTimeMillis()
        logger.info("TimeDecorator 종료 resultTime=${endTime - startTime}ms")
        return result
    }
}
```

```kotlin
internal class DecoratorPatternTest {

    ...

    @Test
    fun decoratorTest2() {
        val messageDecorator = MessageDecorator(RealComponent())
        val timeDecorator = TimeDecorator(messageDecorator)
        val client = DecoratorPatternClient(timeDecorator)
        client.execute()
    }
}
```

<img width="623" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/e631ef6b-e6ca-4479-a17b-65cb90b9970c">

위 코드를 보면 Decorator 기능에 일부 중복이 있다. 꾸며주는 역할을 하는 Decorator들은 스스로 존재할 수 없다. 항상 꾸며줄 대상이 존재해야 한다.
따라서 내부 호출 대상인 component를 가지고 있어야 한다. 그리고 component를 항상 호출해야 한다. 이 부분이 중복이다.
이런 중복을 제거하기 위해 component를 속성으로 가지고 있는 Decorator라는 추상 클래스를 만드는 방법을 고민할 수 있다.
이렇게 하면 추가로 클래스 다이어그램에서 어떤 것이 실제 컴포넌트인지, 데코레이터인지 명확하게 구분할 수 있다.

> 프록시 패턴과 데코레이터 패턴은 굉장히 유사하다. 디자인 패턴에서 중요한 것은 해당 패턴의 겉모양이 아니라 그 패턴을 만든 의도(Intent)가 중요하다.

- 프록시 패턴의 의도: 다른 개체에 대한 **접근을 제어**하기 위해 대리자를 제공
- 데코레이터 패턴의 의도: **객체에 추가 책임(기능)을 동적으로 추가**하고, 기능 확장을 위한 유연한 대안 제공

## 동적 프록시 기술

### 리플렉션

프록시를 사용해서 기존 코드를 변경하지 않고, 로그 추적기라는 부가 기능을 적용할 수 있었다.
그런데 문제는 대상 클래스 수 만큼 로그 추적을 위한 프록시 클래스를 만들어야 한다는 점이다. 로그 추적을 위한 프록시 클래스들의 소스코드는 거의 같은 모양을 하고 있다.

자바가 기본으로 제공하는 JDK 동적 프록시 기술이나 CGLIB 같은 프록시 생성 오픈소스 기술을 활용하면 프록시 객체를 동적으로 만들어낼 수 있다.
프록시를 적용할 코드를 하나만 만들어두고 동적 프록시 기술을 사용해서 프록시 객체를 찍어내면 된다.

JDK 동적 프록시 기술을 이해하기 위해서는 리플렉션 기술을 이해해야 한다.

```kotlin
internal class ReflectionTest {

    @Test
    fun reflectionTest0() {
        val target = Hello()

        // 공통 로직1 시작
        logger.info("start")
        val result1 = target.callA()
        logger.info("result=$result1")
        // 공통 로직1 종료

        // 공통 로직2 시작
        logger.info("start")
        val result2 = target.callB()
        logger.info("result=$result2")
        // 공통 로직2 종료
    }

    @Test
    fun reflectionTest1() {
        // 클래스 정보
//        val classHello = Class.forName("hello.proxy.jdkdynamic.Hello")
        val classHello = Hello::class.java

        val target = Hello()

        // callA 메서드 정보
        val methodCallA = classHello.getMethod("callA")
        val result1 = methodCallA(target)
        logger.info("result1=$result1")

        // callB 메서드 정보
        val methodCallB = classHello.getMethod("callB")
        val result2 = methodCallB(target)
        logger.info("result2=$result2")
    }

    @Test
    fun reflectionTest2() {
        // 클래스 정보
        val classHello = Hello::class.java

        val target = Hello()

        val methodCallA = classHello.getMethod("callA")
        dynamicCall(methodCallA, target)

        val methodCallB = classHello.getMethod("callB")
        dynamicCall(methodCallB, target)
    }

    private fun dynamicCall(method: Method, target: Any) {
        // callA 메서드 정보
        logger.info("start")
        val result = method.invoke(target)
        logger.info("result=$result")
    }
}

class Hello {
    fun callA(): String {
        logger.info("callA")
        return "A"
    }

    fun callB(): String {
        logger.info("callB")
        return "B"
    }
}
```

- 리플렉션을 사용함으로써 Method라는 추상화된 객체로 공통화를 할 수 있도록 변경되었다.

**주의**

리플렉션을 사용하면 클래스와 메서드의 메타정보를 사용해서 애플리케이션을 동적으로 유연하게 만들 수 있다.
하지만 리플렉션 기술은 런타임에 동작하기 때문에, 컴파일 시점에 오류를 잡을 수 없다.

따라서 리플렉션은 일반적으로 사용하면 안된다. 리플렉션은 프레임워크 개발이나 또는 매우 일반적인 공통 처리가 필요할 때 부분적으로 주의해서 사용해야 한다.

## JDK 동적 프록시

> 자바가 기본으로 제공

프록시 클래스의 기본 코드와 흐름은 거의 같고, 프록시를 어떤 대상에 적용하는가 정도만 차이가 있었다. (중복 코드가 너무 많음, 적용 대상 만큼 많은 프록시 클래스 필요)
이러한 문제를 해결하는 것이 바로 동적 프록시 기술이다.

동적 프록시 기술을 사용하면 개발자가 직접 프록시 클래스를 만들지 않아도 된다. 이름 그대로 프록시 객체를 동적으로 런타임에 개발자 대신 만들어준다.
그리고 동적 프록시에 원하는 실행 로직을 지정할 수 있다.

> JDK 동적 프록시는 인터페이스 기반으로 프록시를 동적으로 만들어준다. 따라서 인터페이스가 필수이다.

### JDK 동적 프록시 InvocationHandler

```java
package java.lang.reflect;

public interface InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable;
}
```

- Object proxy: 프록시 자신
- Method method: 호출한 메섣,
- Object[] args: 메서드를 호출할 때 전달한 인수

```kotlin
class TimeInvocationHandler(
    private val target: Any,
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        logger.info("TimeProxy 실행")
        val startTime = System.currentTimeMillis()

        val result = method(target)

        val endTime = System.currentTimeMillis()
        logger.info("TimeProxy 종료 resultTime=${endTime - startTime}ms")
        return result
    }
}
```

- target: 프록시가 호출할 대상

```kotlin
internal class JdkDynamicProxyTest {

    @Test
    fun dynamicA() {
        val target = AImpl()
        val handler = TimeInvocationHandler(target)

        val proxy = Proxy.newProxyInstance(
            AInterface::class.java.getClassLoader(),
            arrayOf(AInterface::class.java),
            handler,
        ) as AInterface

        proxy.call() // call() 메서드를 TimeInvocationHandler invoke 메서드의 method 파라미터로 넘겨준다
        logger.info("targetClass=${target::class.java}")
        logger.info("proxyClass=${proxy::class.java}")
    }

    @Test
    fun dynamicB() {
        val target = BImpl()
        val handler = TimeInvocationHandler(target)

        val proxy = Proxy.newProxyInstance(
            BInterface::class.java.getClassLoader(),
            arrayOf(BInterface::class.java),
            handler,
        ) as BInterface

        proxy.call()
        logger.info("targetClass=${target::class.java}")
        logger.info("proxyClass=${proxy::class.java}")
    }
}
```

```text
23:29:12.333 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 실행
23:29:12.342 [Test worker] INFO hello.proxy.jdkdynamic.code.AImpl -- A 호출
23:29:12.343 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 종료 resultTime=4ms
23:29:12.344 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- targetClass=class hello.proxy.jdkdynamic.code.AImpl
23:29:12.345 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- proxyClass=class jdk.proxy3.$Proxy15
```

**위 코드를 보면 TimeInvocationHandler 하나만 만들고 별도의 프록시 객체를 만들지 않았다.**

<img width="555" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/f063a693-008f-405e-87b1-58ae4c9ec4f5">

1. 클라이언트는 JDK 동적 프록시의 `call()` 을 실행한다.
2. JDK 동적 프록시는 `InvocationHandler.invoke()` 를 호출한다. `TimeInvocationHandler` 가 구현체로 있으로 `TimeInvocationHandler.invoke()`가 호출된다.
3. `TimeInvocationHandler` 가 내부 로직을 수행하고, `method.invoke(target, args)` 를 호출해서 `target` 인 실제 객체( `AImpl` )를 호출한다.
4. `AImpl` 인스턴스의 `call()` 이 실행된다.
5. `AImpl` 인스턴스의 `call()` 의 실행이 끝나면 `TimeInvocationHandler` 로 응답이 돌아온다. 시간 로그를 출력하고 결과를 반환한다.

## JDK 동적 프록시 적용

```kotlin
class LogTraceBasicHandler(
  private val target: Any,
  private val logTrace: LogTrace,
) : InvocationHandler {
  override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
    var status: TraceStatus? = null
    return try {
      val message = "${method.declaringClass.getSimpleName()}.${method.name}()"
      status = logTrace.begin(message)
      // target 호출
      val result = if (args == null) {
        method.invoke(target)
      } else {
        method.invoke(target, *args)
      }
      logTrace.end(status)
      result
    } catch (e: Exception) {
      logTrace.exception(status, e)
      throw e
    }
  }
}
```

```kotlin
@Configuration
class DynamicProxyBasicConfig {
    @Bean
    fun orderControllerV1(logTrace: LogTrace): OrderControllerV1 {
        val orderController = OrderControllerV1Impl(orderServiceV1(logTrace))
        return Proxy.newProxyInstance(
            OrderControllerV1::class.java.getClassLoader(),
            arrayOf(OrderControllerV1::class.java),
            LogTraceBasicHandler(orderController, logTrace)
        ) as OrderControllerV1
    }

    @Bean
    fun orderServiceV1(logTrace: LogTrace): OrderServiceV1 {
        val orderService = OrderServiceV1Impl(orderRepositoryV1(logTrace))
        return Proxy.newProxyInstance(
            OrderServiceV1::class.java.getClassLoader(),
            arrayOf(OrderServiceV1::class.java),
            LogTraceBasicHandler(orderService, logTrace)
        ) as OrderServiceV1
    }

    @Bean
    fun orderRepositoryV1(logTrace: LogTrace): OrderRepositoryV1 {
        val orderRepository = OrderRepositoryV1Impl()
        return Proxy.newProxyInstance(
            OrderRepositoryV1::class.java.getClassLoader(),
            arrayOf(OrderRepositoryV1::class.java),
            LogTraceBasicHandler(orderRepository, logTrace)
        ) as OrderRepositoryV1
    }
}
```

### 클래스 의존 관계

<img width="558" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/84d178a8-d8bb-4c2e-8ec7-7ce211f0bb6a">

### 런타임 객체 의존 관계

<img width="571" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/395af990-f37c-4989-93d4-d3d20f25ae83">

**JDK 동적 프록시 - 한계**

JDK 동적 프록시는 인터페이스가 필수이므로 V2 애플리케이션처럼 인터페이스가 없이 클래스만 있는 경우에는 프록시 적용이 어려울 수 있다.
따라서 이러한 부분을 해결할 수 있는 **CGLIB**라는 바이트코드를 조작하는 특별한 라이브러리를 사용해야 한다.

### CGLIB 소개

> CGLIB : Code Generator Library

- CGLIB는 바이트코드를 조작해서 동적으로 클래스를 생성하는 기술을 제공하는 라이브러리이다.
- CGLIB를 사용하면 인터페이스가 없어도 구체 클래스만 가지고 동적 프록시를 만들어낼 수 있다.
- CGLIB는 원래는 외부 라이브러리인데, 스프링 프레임워크가 스프링 내부 소스 코드에 포함했다. 따라서 스프링을 사용한다면 별도의 외부 라이브러리를 추가하지 않아도 사용할 수 있다.

참고로 우리가 CGLIB를 직접 사용하는 경우는 거의 없다. 이후에 설명할 스프링의 ProxyFactory라는 것이 이 기술을 편리하게 사용하게 도와주기 때문에, 너무 깊이있게 파기 보다는 CGLIB가 무엇인지 대략 개념만 잡으면 된다.

JDK 동적 프록시에서 실행 로직을 위해 `InvocationHandler`를 제공했듯이, CGLIB는 `MethodInvocation`을 제공한다.

`MethodInterceptor`

```kotlin
class TimeMethodInterceptor: MethodInterceptor {
    override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy?): Any {
        TODO("Not yet implemented")
    }
}
```
- obj: CGLIB가 적용된 객체
- method: 호출된 메서드
- args: 메서드를 호출하면서 전달된 인수
- proxy: 메서드 호출에 사용

```kotlin
open class ConcreteService {
    fun call() {
        logger.info("ConcreteService 호출")
    }
}
```

```kotlin
class TimeMethodInterceptor(
    private val target: Any,
) : MethodInterceptor {
    override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy): Any? {
        logger.info("TimeProxy 실행")
        val startTime = System.currentTimeMillis()

        val result = proxy(target, args)

        val endTime = System.currentTimeMillis()
        logger.info("TimeProxy 종료 resultTime=${endTime - startTime}ms")
        return result

    }
}
```

```kotlin
class CglibTest {

    @Test
    fun cglib() {
        val target = ConcreteService()

        val enhancer = Enhancer().apply {
            setSuperclass(ConcreteService::class.java)
            setCallback(TimeMethodInterceptor(target))
        }
        val proxy = enhancer.create() as ConcreteService
        logger.info("targetClass=${target.javaClass}")
        logger.info("proxyClass=${proxy.javaClass}")

        proxy.call()
    }
}
```
- Enhancer: CGLIB는 Enhancer를 사용해서 프록시를 생성한다.
- enhancer.setSuperclass(ConcreteService.class): CGLIB는 구체 클래스를 상속 받아서 프록시를 생성할 수 있다. 어떤 구체 클래스를 상속 받을지 지정
- enhancer.setCallback(TimeMethodInterceptor(target)): 프록시에 적용할 실행 로직을 할당
- enhancer.create(): 프록시를 생성 setSuperclass에서 지정한 클래스를 상속 받아서 프록시가 만들어진다.

<img width="582" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/354c0fb4-4413-4eda-9b1d-2cd1628eb577">

**CGLIB 제약**

- 클래스 기반 프록시는 상속을 사용하기 때문에 몇가지 제약이 존재한다.
  - 부모 클래스의 생성자를 체크해야한다. -> 기본 생성자가 필요
  - 클래스에 final 키워드가 붙으면 상속이 불가능 
  - 메서드에 final 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없다.

> 참고
> 
> CGLIB를 사용하면 인터페이스가 없는 V2 애플리케이션에 동적 프록시를 적용할 수 있다. 그런데 지금 당장 적용
> 하기에는 몇가지 제약이 있다. V2 애플리케이션에 기본 생성자를 추가하고, 의존관계를 `setter` 를 사용해서 주
> 입하면 CGLIB를 적용할 수 있다. 하지만 다음에 학습하는 `ProxyFactory` 를 통해서 CGLIB를 적용하면 이런
> 단점을 해결하고 또 더 편리하기 때문에, 애플리케이션에 CGLIB로 프록시를 적용하는 것은 조금 뒤에 알아보겠다.

## 프록시 팩토리

**문제점**
- 인터페이스가 있는 경우에는 JDK 동적 프록시, 없는 경우에는 CGLIB를 적용하려면 어떻게?
- 두 기술을 함께 사용할 때 부가 기능을 제공하기 위해 InvocationHandler와 MethodInterceptor를 각각 중복으로 만들어서 관리?
- 특정 조건에 맞을 때 프록시 로직을 적용하는 기능도 공통으로 제공되었으면? -> no-log

### 인터페이스가 있는 경우에는 JDK 동적 프록시, 없는 경우에는 CGLIB를 적용하려면 어떻게?

스프링은 동적 프록시를 통합해서 편리하게 만들어주는 프록시 팩토리라는 기능을 제공한다. 프록시 팩토리는 인터페이스가 있다면 JDK 동적 프록시를 사용하고, 구체 클래스만 있다면 CGLIB를 사용한다.
그리고 이 설정을 변경할 수도 있다.

<img width="634" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/69646d56-d0c4-46ab-9fbc-5574d6eb769e">

### 두 기술을 함께 사용할 때 부가 기능을 제공하기 위해 InvocationHandler와 MethodInterceptor를 각각 중복으로 만들어서 관리?

스프링은 위와 같은 문제를 해결하기 위해 부가 기능을 적용할 때 **Advice**라는 새로운 개념을 도입했다. 개발자는 InvocationHandler와 MethodInterceptor를 신경쓰지 않고, Advice만 만들면 된다.
결과적으로 InvocationHandler나 MethodInterceptor는 Advice를 호출하게 된다.
프록시 팩토리를 사용하면 Advice를 호출하는 전용 InvocationHandler, MethodInterceptor를 내부에서 사용한다. (adviceInvocationHandler, adviceMethodInterceptor)

**Advice 도입**

<img width="607" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/c7d25bfa-ace4-4de5-bc3b-41803ed325b2">

즉 개발자는 InvocationHandler, MethodInterceptor를 고려하지 않고 Advice만 만들면 된다.

### 특정 조건에 맞을 때 프록시 로직을 적용하는 기능도 공통으로 제공되었으면?

스프링은 **PointCut**이라는 개념을 도입해서 이 문제를 일관성 있게 해결한다.

### ProxyFactory - JDK 동적 프록시

```kotlin
class TimeAdvice : MethodInterceptor {
  override fun invoke(invocation: MethodInvocation): Any? {
    logger.info("TimeProxy 실행")
    val startTime = System.currentTimeMillis()

    val result = invocation.proceed()

    val endTime = System.currentTimeMillis()
    logger.info("TimeProxy 종료 resultTime=${endTime - startTime}ms")
    return result
  }
}
```
- TimeAdvice는 MethodInterceptor 인터페이스를 구현한다. (패키지 이름에 주의, spring-aop 패키지에 존재하는 MethodInterceptor)
- invocation.proceed()
  - target 클래스를 호출하고 그 결과를 받는다.
  - 이전과 다르게 target 클래스가 보이지 않는다. target 클래스의 정보는 이미 invocation안에 포함
    - 프록시 팩토리로 프록시를 생성하는 단계에서 이미 target 정보를 파라미터로 전달받기 때문
    - val proxyFactory = ProxyFactory(target)

```kotlin
internal class ProxyFactoryTest {
    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    fun interfaceProxy() {
        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        proxyFactory.addAdvice(TimeAdvice())
        val proxy = proxyFactory.proxy as ServiceInterface
        logger.info("targetClass=${target.javaClass}")
        logger.info("proxyClass=${proxy.javaClass}")
      
        proxy.save()
    }
}
```

```text
17:45:53.710 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest -- targetClass=class hello.proxy.common.service.ServiceImpl
17:45:53.713 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest -- proxyClass=class jdk.proxy3.$Proxy16
17:45:53.716 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 실행
17:45:53.716 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
17:45:53.717 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료 resultTime=0ms
```

### ProxyFactory - CGLIB

```kotlin
@Test
@DisplayName("구체 클래스만 있으면 CGLIB 사용")
fun concreteProxy() {
  val target = ConcreteService()
  val proxyFactory = ProxyFactory(target)
  proxyFactory.addAdvice(TimeAdvice())
  val proxy = proxyFactory.proxy as ConcreteService
  logger.info("targetClass=${target.javaClass}")
  logger.info("proxyClass=${proxy.javaClass}")

  proxy.call()

  Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue()
  Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse()
  Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
}
```

```text
17:56:51.619 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest -- targetClass=class hello.proxy.common.service.ConcreteService
17:56:51.621 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest -- proxyClass=class hello.proxy.common.service.ConcreteService$$SpringCGLIB$$0
17:56:51.623 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 실행
17:56:51.623 [Test worker] INFO hello.proxy.common.service.ConcreteService -- ConcreteService 호출
17:56:51.624 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료 resultTime=0ms
```

```kotlin
@Test
@DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용한다")
fun proxyTargetTest() {
  val target = ServiceImpl()
  val proxyFactory = ProxyFactory(target)
  proxyFactory.addAdvice(TimeAdvice())
  proxyFactory.isProxyTargetClass = true
  val proxy = proxyFactory.proxy as ServiceInterface
  logger.info("targetClass=${target.javaClass}")
  logger.info("proxyClass=${proxy.javaClass}")

  proxy.save()

  Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue()
  Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse()
  Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
}
```

```text
18:00:16.596 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest -- targetClass=class hello.proxy.common.service.ServiceImpl
18:00:16.599 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest -- proxyClass=class hello.proxy.common.service.ServiceImpl$$SpringCGLIB$$0
18:00:16.601 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 실행
18:00:16.601 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
18:00:16.602 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료 resultTime=0ms
```

- isProxyTargetClass을 통해서 interface가 존재하지만 CGLIB를 사용하도록 할 수 있다.

> 참고
> 
> 스프링 부트는 AOP를 적용할 때 기본적으로 `proxyTargetClass=true`로 설정해서 사용한다.
> 따라서 인터페이스가 있어도 항상 CGLIB를 사용해서 구체 클래스를 기반으로 프록시를 생성한다.

### 포인트컷, 어드바이스, 어드바이저 소개

- 포인트컷(PointCut): 어디에 부가 기능을 적용할지, 어디에 부가 기능을 적용하지 않을지 판단하는 필터링 로직이다. 주로 클래스와 메서드 이름으로 필터링 한다.
  - 이름 그대로 어떤 포인트에 기능을 적용할지 하지 않을지 잘라서(cut) 구분하는 것이다.
- 어드바이스(Advice): 이전에 본 것 처럼 프록시가 내부에서 호출하는 부가 기능이다. 단순하게 프록시 로직이라고 생각하면 된다.
- 어드바이저(Advisor): 단순하게 하나의 포인트컷과 하나의 어드바이스를 가지고 있는 것이다. (포인터컷1 + 어드바이스1)

정리하면 부가 기능 로직을 적용해야 하는데, **포인트컷**으로 어디에? 적용할지 선택하고, **어드바이스**로 어떤 로직을 적용할지 선택하는 것이다. 그리고 어디에? 어떤 로직?을 모두 알고 있는 것이 **어드바이저**다.

> 이렇게 함으로써 역할과 책임을 명확하게 분리할 수 있다. 이전 코드인 LogTraceFilterHandler는 포인트컷과 어드바이스를 모두 포함하고 있는데 이러한 부분에 대한 책임을 스프링 AOP를 사용하면 명확하게 분리할 수 있다.

<img width="612" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/336f4aaa-3319-4580-b895-f2021d5e5625">

```kotlin
internal class AdvisorTest {

  @Test
  fun advisorTest1() {
    val target = ServiceImpl()
    val proxyFactory = ProxyFactory(target)
    val advisor = DefaultPointcutAdvisor(Pointcut.TRUE, TimeAdvice())
    proxyFactory.addAdvisor(advisor)
    val proxy = proxyFactory.proxy as ServiceInterface

    proxy.find()
  }
}
```
- DefaultPointcutAdvisor: Advisor 인터페이스의 가장 일반적인 구현체이다. 생성자를 통해 하나의 포인트컷과 하나의 어드바이스를 넣어주면 된다.
- Pointcut.TRUE: 항상 true를 반환하는 포인트컷
- 그런데 생각해보면 이전에 분명히 `proxyFactory.addAdvice(TimeAdvice())` 이렇게 어드바이저가 아닌 어드바이스를 바로 적용했다. 이것은 단순히 편의 메서드이고 결과적으로 해당 메서드 내부에서 지금 코드와 똑같은 다음 어드바이저가 생성된다. (DefaultPointCoutAdvisor(PointCut.TRUE, TimeAdvice()))

**PointCut 구현**

```kotlin
internal class AdvisorTest {
    @Test
    @DisplayName("직접 만든 포인트컷")
    fun advisorTest2() {
        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        val advisor = DefaultPointcutAdvisor(MyPointCut(), TimeAdvice())
        proxyFactory.addAdvisor(advisor)
        val proxy = proxyFactory.proxy as ServiceInterface

        proxy.save()
        proxy.find()
    }

    companion object {
        class MyPointCut : Pointcut {
            override fun getClassFilter(): ClassFilter {
                return ClassFilter.TRUE
            }

            override fun getMethodMatcher(): MethodMatcher {
                return MyMethodMatcher()
            }

        }

        class MyMethodMatcher : MethodMatcher {
            private val matchName = "save"

            override fun matches(method: Method, targetClass: Class<*>): Boolean {
                val result = method.name.equals(matchName)
                logger.info("PointCut 호출 method=${method.name} targetClass=${targetClass}")
                logger.info("PointCut 결과 result=$result")
                return result
            }

            override fun matches(method: Method, targetClass: Class<*>, vararg args: Any?): Boolean {
                return false
            }

            override fun isRuntime(): Boolean {
                return false
            }

        }
    }
}
```
- isRuntime(): 해당 메서드가 참이면 matches(... args) 메서드가 대신 호출된다. (false이면 matchers())
  - 동적으로 넘어오는 매개변수를 판단 로직으로 사용할 수 있다.
  - isRuntime()이 false인 경우에는 정적 정보만 사용하기 떄문에 스프링 내부에서 캐싱을 통해 성능 향상이 가능하지만, isRuntime()이 true인 경우 매개변수가 동적으로 변경된다고 가정하기 때문에 캐싱하지 않는다.

```text
19:48:39.597 [Test worker] INFO hello.proxy.advisor.AdvisorTest -- PointCut 호출 method=save targetClass=class hello.proxy.common.service.ServiceImpl
19:48:39.602 [Test worker] INFO hello.proxy.advisor.AdvisorTest -- PointCut 결과 result=true
19:48:39.603 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 실행
19:48:39.603 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
19:48:39.604 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료 resultTime=1ms
19:48:39.605 [Test worker] INFO hello.proxy.advisor.AdvisorTest -- PointCut 호출 method=find targetClass=class hello.proxy.common.service.ServiceImpl
19:48:39.605 [Test worker] INFO hello.proxy.advisor.AdvisorTest -- PointCut 결과 result=false
19:48:39.605 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
```

**save 호출**

<img width="612" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/3f08a744-243e-42e2-a4ce-8f4861fe7169">

**find 호출**

<img width="606" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/9dbea683-53c3-4017-a946-4b2a92f216e6">

### 스프링이 제공하는 포인트컷

```kotlin
@Test
@DisplayName("스프링이 제공하는 포인트컷")
fun advisorTest3() {
  val target = ServiceImpl()
  val proxyFactory = ProxyFactory(target)
  val pointCut = NameMatchMethodPointcut()
  pointCut.setMappedNames("save")
  val advisor = DefaultPointcutAdvisor(pointCut, TimeAdvice())
  proxyFactory.addAdvisor(advisor)
  val proxy = proxyFactory.proxy as ServiceInterface

  proxy.save()
  proxy.find()
}
```

```text
19:54:40.101 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 실행
19:54:40.104 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
19:54:40.105 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료 resultTime=0ms
19:54:40.106 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
```

- NameMatchMethodPointCut: 메서드 이름을 기반으로 매칭한다. `PatternMatchUtils`를 사용한다.
  - 예) `*xxx*`허용
- JdkRegexMethodPointCut: JDK 정규 표현식을 기반으로 포인트컷을 매칭한다.
- TruePointCut: 항상 참을 반환한다.
- AnnotationMatchingPointcut: 어노테이션으로 매칭한다.
- AspectJExpressPointcut: aspectJ 표현식으로 매칭한다.

**가장 중요한 것은 aspectJ 표현식**

사실 다른 것은 크게 중요하지 않다. 실무에서는 사용하기도 편리하고 기능도 가장 많은 aspectJ 표현식을 기반으로 사용하는 AspectJExpressPointcut을 사용하게 된다.

### 여러 어드바이저 함께 적용

어드바이저는 하나의 포인트컷과 하나의 어드바이스를 가지고 있다.
만약 여러 어드바이저를 하나의 `target`에 적용하려면 어떻게 해야할까? (쉽게 하나의 target에 여러 어드바이스를 적용하기 위해서)

```kotlin
internal class MultiAdvisorTest {

  @Test
  @DisplayName("여러 프록시")
  fun multiAdvisorTest1() {
    // client -> proxy2(advisor2) -> proxy1(advisor1) -> target

    // 프록시1 생성
    val target = ServiceImpl()
    val proxyFactory1 = ProxyFactory(target)
    val advisor1 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice1())
    proxyFactory1.addAdvisor(advisor1)
    val proxy1 = proxyFactory1.proxy as ServiceInterface

    // 프록시2 생성
    val proxyFactory2 = ProxyFactory(proxy1)
    val advisor2 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice2())
    proxyFactory2.addAdvisor(advisor2)
    val proxy2 = proxyFactory2.proxy as ServiceInterface

    proxy2.save()
  }

  companion object {
    class Advice1 : MethodInterceptor {
      override fun invoke(invocation: MethodInvocation): Any? {
        logger.info("advice1 호출")
        return invocation.proceed()
      }
    }

    class Advice2 : MethodInterceptor {
      override fun invoke(invocation: MethodInvocation): Any? {
        logger.info("advice2 호출")
        return invocation.proceed()
      }
    }
  }
}
```

```text
20:08:03.356 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest -- advice2 호출
20:08:03.359 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest -- advice1 호출
20:08:03.359 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
```

<img width="587" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/13e0f764-60b2-4daf-994a-8405649af364">

위와 같이 구현한다면 프록시를 2번 생성해야 한다는 문제가 있다. 만약 적용해야 하는 어드바이저가 10개라면 10개의 포록시를 생성해야한다.

### 하나의 프록시, 여러 어드바이저

스프링은 이 문제를 해결하기 위해 하나의 프록시에 여러 어드바이저를 적용할 수 있도록 만들어두었다.

<img width="593" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/dc3cfae6-4d0b-4d79-b1ac-82b71971d967">

```kotlin
@Test
@DisplayName("하나의 프록시, 여러 어드바이저")
fun multiAdvisorTest2() {
  val advisor2 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice2())
  val advisor1 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice1())

  val target = ServiceImpl()
  val proxyFactory = ProxyFactory(target)
  proxyFactory.addAdvisor(advisor2)
  proxyFactory.addAdvisor(advisor1)
  val proxy = proxyFactory.proxy as ServiceInterface

  proxy.save()
}
```
- 등록하는 순서대로 advisor가 호출된다. 여기서는 advisor2, advisor1 순서로 등록

<img width="601" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/f5e30620-fbab-463b-a6cc-31e4c37a3fba">

> 중요
> 
> 스프링 AOP를 처음 공부하거나 사용하면, AOP 적용 수 만큼 프록시가 생성된다고 착각하게 된다. 하지만 스프링은 AOP를 적용할 때, 최적화를 진행해서 위와 같이 프록시는 하나만 만들고, 하나의 프록시에 여러 어드바이저를 적용한다.

