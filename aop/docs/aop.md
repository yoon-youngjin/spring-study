# AOP

## 스프링 AOP 구현
> @Aspect를 추가한다고 컴포넌트 스캔의 대상이 되는것은 아니다.

스프링 빈으로 등록하는 방법은 다음과 같다.
- `@Bean` 을 사용해서 직접 등록
- `@Component` 컴포넌트 스캔을 사용해서 자동 등록
- `@Import` 주로 설정 파일을 추가할 때 사용( `@Configuration` )

```kotlin
@Aspect
class AspectV1 {

    @Around("execution(* hello.aop.order..*(..))") // 포인트컷
    fun doLog(joinPoint: ProceedingJoinPoint): Any? { // 어드바이스
        logger.info("[log] ${joinPoint.signature}")
        return joinPoint.proceed()
    }
}
```

<img width="543" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/e4a8a46e-b43b-4e0e-b3a5-bdf4e5ae02c9">

### 포인트컷 분리

@Around에 포인트컷 표현식을 직접 넣을 수도 있지만, @Pointcut을 통해서 별도로 분리할 수도 있다.

```kotlin
@Aspect
class AspectV2 {
    @Pointcut("execution(* hello.aop.order..*(..))")
    fun allOrder() {} // pointcut signature

    @Around("allOrder()") 
    fun doLog(joinPoint: ProceedingJoinPoint): Any? { // 어드바이스
        logger.info("[log] ${joinPoint.signature}")
        return joinPoint.proceed()
    }
}
```

- 위와 같이 구성하면 포인트컷을 재사용할 수 있고, 함수명을 통해 의미를 부여할 수도 있다.

### 어드바이스 추가 

```kotlin
@Aspect
class AspectV3 {
    @Pointcut("execution(* hello.aop.order..*(..))")
    fun allOrder() {
    }

    @Pointcut("execution(* *..*Service.*(..))")
    fun allService() {
    }

    @Around("allOrder()") // 포인트컷
    fun doLog(joinPoint: ProceedingJoinPoint): Any? { // 어드바이스
        logger.info("[log] ${joinPoint.signature}")
        return joinPoint.proceed()
    }

    // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
    @Around("allOrder() && allService()") // 포인트컷
    fun doTransaction(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            logger.info("[트랜잭션 시작] ${joinPoint.signature}")
            val result = joinPoint.proceed()
            logger.info("[트랜잭션 커밋] ${joinPoint.signature}")
            result
        } catch (e: Exception) {
            logger.info("[트랜잭션 롤백] ${joinPoint.signature}")
            throw e
        } finally {
            logger.info("[리소스 릴리즈] ${joinPoint.signature}")
        }
    }
}
```

- 포인트컷은 이렇게 조합할 수 있다. `&&` (AND), `||` (OR), `!` (NOT) 3가지 조합이 가능하다.
- *Service: `XxxService` 처럼 `Service` 로 끝나는 것을 대상으로 한다.

<img width="546" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/181e934b-d0d7-4214-a323-922aaff9ebdc">

### 포인트컷 참조

```kotlin
class Pointcuts {

    @Pointcut("execution(* hello.aop.order..*(..))")
    fun allOrder() {
    }

    @Pointcut("execution(* *..*Service.*(..))")
    fun allService() {
    }

    @Pointcut("allOrder() && allService()")
    fun orderAndService() {
    }
}
```

```kotlin
@Aspect
class AspectV4Pointcut {

    @Around("hello.aop.order.aop.Pointcuts.allOrder()") // 포인트컷
    fun doLog(joinPoint: ProceedingJoinPoint): Any? { // 어드바이스
        logger.info("[log] ${joinPoint.signature}")
        return joinPoint.proceed()
    }

    // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()") // 포인트컷
    fun doTransaction(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            logger.info("[트랜잭션 시작] ${joinPoint.signature}")
            val result = joinPoint.proceed()
            logger.info("[트랜잭션 커밋] ${joinPoint.signature}")
            result
        } catch (e: Exception) {
            logger.info("[트랜잭션 롤백] ${joinPoint.signature}")
            throw e
        } finally {
            logger.info("[리소스 릴리즈] ${joinPoint.signature}")
        }
    }
}
```

### 어드바이스 순서

어드바이스는 기본적으로 순서를 보장하지 않는다. 순서를 지정하고 싶으면 @Aspect 적용 단위로 @Order를 적용해야 한다.
문제는 이것을 어드바이스 단위가 아니라 클래스 단위로 지정할 수 있다는 점이다. 그래서 지금처럼 하나의 애스팩트에 여러 어드바이스가 있으면 순서를 보장 받을 수 없다.
**따라서 애스펙트를 별도의 클래스로 분리해야 한다.**

```kotlin
class AspectV5Order {

    @Aspect
    @Order(2)
    class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()") // 포인트컷
        fun doLog(joinPoint: ProceedingJoinPoint): Any? { // 어드바이스
            logger.info("[log] ${joinPoint.signature}")
            return joinPoint.proceed()
        }
    }

    @Aspect
    @Order(1)
    class TxAspect {
        // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()") // 포인트컷
        fun doTransaction(joinPoint: ProceedingJoinPoint): Any? {
            return try {
                logger.info("[트랜잭션 시작] ${joinPoint.signature}")
                val result = joinPoint.proceed()
                logger.info("[트랜잭션 커밋] ${joinPoint.signature}")
                result
            } catch (e: Exception) {
                logger.info("[트랜잭션 롤백] ${joinPoint.signature}")
                throw e
            } finally {
                logger.info("[리소스 릴리즈] ${joinPoint.signature}")
            }
        }
    }
}
```

<img width="563" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/3fc15c31-6931-412b-a689-3e8041dd03e9">

### 어드바이스 종류

- `@Around` : 메서드 호출 전후에 수행, 가장 강력한 어드바이스, 조인 포인트 실행 여부 선택, 반환 값 변환, 예외 변환 등이 가능
- `@Before` : 조인 포인트 실행 이전에 실행
- `@AfterReturning` : 조인 포인트가 정상 완료후 실행
- `@AfterThrowing` : 메서드가 예외를 던지는 경우 실행
- `@After` : 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)

```kotlin
class AspectV6Advice {
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()") // 포인트컷
    fun doTransaction(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            // @Before
            logger.info("[트랜잭션 시작] ${joinPoint.signature}")
            val result = joinPoint.proceed()
            // @AfterReturning
            logger.info("[트랜잭션 커밋] ${joinPoint.signature}")
            result
        } catch (e: Exception) {
            // @AfterThrowing
            logger.info("[트랜잭션 롤백] ${joinPoint.signature}")
            throw e
        } finally {
            // @After
            logger.info("[리소스 릴리즈] ${joinPoint.signature}")
        }
    }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    fun doBefore(joinPoint: JoinPoint) {
        logger.info("[before] ${joinPoint.signature}")
    }

    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    fun doAfterReturning(joinPoint: JoinPoint, result: Any?) {
        logger.info("[return] ${joinPoint.signature} return=$result")
    }

    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    fun doAfterReturning(joinPoint: JoinPoint, ex: Exception) {
        logger.info("[ex] ${joinPoint.signature} message=${ex.message}")
    }

    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    fun doAfter(joinPoint: JoinPoint) {
        logger.info("[after] ${joinPoint.signature}")
    }
}
```

- 모든 어드바이스는 `org.aspectj.lang.JoinPoint` 를 첫번째 파라미터에 사용할 수 있다. (생략해도 된다.)
  - 단 `@Around` 는 `ProceedingJoinPoint` 을 사용해야 한다.

> ProceedingJoinPoint` 는 `org.aspectj.lang.JoinPoint` 의 하위 타입

**JoinPoint 인터페이스의 주요 기능**
- `getArgs()` : 메서드 인수를 반환합니다.
- `getThis()` : 프록시 객체를 반환합니다.
- `getTarget()` : 대상 객체를 반환합니다.
- `getSignature()` : 조언되는 메서드에 대한 설명을 반환합니다.
- `toString()` : 조언되는 방법에 대한 유용한 설명을 인쇄합니다.

**ProceedingJoinPoint 인터페이스의 주요 기능**
- `proceed()` : 다음 어드바이스나 타켓을 호출한다.

**순서**
- 스프링은 5.2.7 버전부터 동일한 `@Aspect` 안에서 동일한 조인포인트의 우선순위를 정했다.
- 실행 순서: `@Around` , `@Before` , `@After` , `@AfterReturning` , `@AfterThrowing`

<img width="564" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/0d09995b-bc88-4bce-a501-229a02e25f9d">

## 포인트컷

### execution

**execution 문법**

```text
execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
```
- 메소드 실행 조인 포인트를 매칭한다.
- ?는 생략할 수 있다.
- `*` 같은 패턴을 지정할 수 있다.

```kotlin
class ExecutionTest {
  private val pointcut = AspectJExpressionPointcut()
  private lateinit var helloMethod: Method

  @BeforeEach
  fun init() {
    helloMethod = MemberServiceImpl::class.java.getMethod("hello", String::class.java)
  }

  @Test
  fun printMethod() {
    // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
    logger.info("helloMethod=$helloMethod")
  }

  @Test
  fun exactMatch() {
    // execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
    pointcut.expression = "execution(public String hello.aop.member.MemberServiceImpl.hello(String))"
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
  }

  @Test
  fun allMatch() {
    pointcut.expression = "execution(* *(..))"
    assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
  }
}
```
- allMatch
  - 접근제어자?: 생략
  - 반환타입: `*`
  - 선언타입?: 생략
  - 메서드이름: `*`
  - 파라미터: `(..)`
  - 예외?: 없음
- 파라미터에서 `..` 은 파라미터의 타입과 파라미터 수가 상관없다는 뜻이다. ( `0..*` ) 

```kotlin
 @Test
    fun packageMatchFalse() {
        pointcut.expression = "execution(* hello.aop.*.*(..))" // 현재 패키지가 hello.aop.member이기 때문에 실패
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isFalse()
    }

    @Test
    fun packageMatchSubPackage1() {
        pointcut.expression = "execution(* hello.aop.member..*.*(..))" // 하위 패키지 모두 포함
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }
```

- 패키지에서 `.` , `..` 의 차이를 이해해야 한다.
  - `.` : 정확하게 해당 위치의 패키지
  - `..` : 해당 위치의 패키지와 그 하위 패키지도 포함

```kotlin
@Test
    fun typeMatchSuperType() {
        pointcut.expression = "execution(* hello.aop.member.MemberService.*(..))" // 하위 패키지 모두 포함
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }
```
- `execution` 에서는 `MemberService` 처럼 부모 타입을 선언해도 그 자식 타입은 매칭된다.

주의할 점은 부모 타입에 있는 메서드만 허용한다는 점이다.

```kotlin
@Test
    fun typeMatchNoSuperTypeMethodFalse() {
        pointcut.expression = "execution(* hello.aop.member.MemberService.*(..))"
        val internalMethod = MemberServiceImpl::class.java.getMethod("internal", String::class.java)
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl::class.java)).isFalse()
    }
```

```kotlin
@Test
    fun argsMatch() {
        pointcut.expression = "execution(* *(String))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun argsMatchNoArgs() {
        pointcut.expression = "execution(* *())"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isFalse()
    }

    // 정확히 하나의 파라미터 허용, 모든 타입 허용
    // -> 파라미터 두개 (*, *)
    @Test
    fun argsMatchStar() {
        pointcut.expression = "execution(* *(*))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    // 숫자와 무관하게 모든 파라미터, 모든 타입 허용
    @Test
    fun argsMatchAll() {
        pointcut.expression = "execution(* *(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    // String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
    @Test
    fun argsMatchComplex() {
        pointcut.expression = "execution(* *(String, ..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }
```
- `(String)` : 정확하게 String 타입 파라미터
- `()` : 파라미터가 없어야 한다.
- `(*)` : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
- `(*, *)` : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
- `(..)` : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 참고로 파라미터가 없어도 된다. `0..*` 로 이해하면 된다.
- `(String, ..)` : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
  - 예) `(String)` , `(String, Xxx)` , `(String, Xxx, Xxx)` 허용

### within

`within` 지시자는 특정 타입 내의 조인 포인트들로 매칭을 제한한다.

```kotlin
class WithinTest {
    private val pointcut = AspectJExpressionPointcut()
    private lateinit var helloMethod: Method

    @BeforeEach
    fun init() {
        helloMethod = MemberServiceImpl::class.java.getMethod("hello", String::class.java)
    }

    @Test
    fun withinExact() {
        pointcut.expression = "within(hello.aop.member.MemberServiceImpl)"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun withinStar() {
        pointcut.expression = "within(hello.aop.member.*Service*)"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun withinStubPackage() {
        pointcut.expression = "within(hello.aop..*)"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }
}
```

**주의**

그런데 `within` 사용시 주의해야 할 점이 있다. 표현식에 부모 타입을 지정하면 안된다는 점이다. 정확하게 타입이 맞아야 한다. 이 부분에서 `execution` 과 차이가 난다.

```kotlin
@Test
@DisplayName("타겟의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
fun withinSuperTypeFalse() {
  pointcut.expression = "within(hello.aop.member.MemberService)"
  assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isFalse()
}
```

### args

- `args` : 인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭
- 기본 문법은 `execution` 의 `args` 부분과 같다.

**execution과 args의 차이점**
- `execution` 은 파라미터 타입이 정확하게 매칭되어야 한다. `execution` 은 클래스에 선언된 정보를 기반으로 판단한다.
- `args` 는 부모 타입을 허용한다. `args` 는 실제 넘어온 파라미터 객체 인스턴스를 보고 판단한다.

```kotlin
@Test
fun args() {
  //hello(String)과 매칭
  assertThat(
    pointcut("args(String)")
      .matches(helloMethod, MemberServiceImpl::class.java)
  ).isTrue()
  assertThat(
    pointcut("args(Object)")
      .matches(helloMethod, MemberServiceImpl::class.java)
  ).isTrue()
  assertThat(
    pointcut("args()")
      .matches(helloMethod, MemberServiceImpl::class.java)
  ).isFalse()
  assertThat(
    pointcut("args(..)")
      .matches(helloMethod, MemberServiceImpl::class.java)
  ).isTrue()
  assertThat(
    pointcut("args(*)")
      .matches(helloMethod, MemberServiceImpl::class.java)
  ).isTrue()
  assertThat(
    pointcut("args(String,..)")
      .matches(helloMethod, MemberServiceImpl::class.java)
  ).isTrue()
}
```

### @target, @within

- `@target` : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
- `@within` : 주어진 애노테이션이 있는 타입 내 조인 포인트

`@target` , `@within` 은 다음과 같이 타입에 있는 애노테이션으로 AOP 적용 여부를 판단한다.
- `@target(hello.aop.member.annotation.ClassAop)`
- `@within(hello.aop.member.annotation.ClassAop)`

**@target vs @within**
- `@target` 은 인스턴스의 모든 메서드를 조인 포인트로 적용한다.
- `@within` 은 해당 타입 내에 있는 메서드만 조인 포인트로 적용한다.

<img width="594" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/ccbc8102-529f-42b9-b468-9163f6484fac">

**주의**

다음 포인트컷 지시자는 단독으로 사용하면 안된다. `args, @args, @target`
이번 예제를 보면 `execution(* hello.aop..*(..))` 를 통해 적용 대상을 줄여준 것을 확인할 수 있다.
`args` , `@args` , `@target` 은 실제 객체 인스턴스가 생성되고 실행될 때 어드바이스 적용 여부를 확인할 수 있다.
실행 시점에 일어나는 포인트컷 적용 여부도 결국 프록시가 있어야 실행 시점에 판단할 수 있다. 프록시가 없다면 판단 자체가 불가능하다.
그런데 스프링 컨테이너가 프록시를 생성하는 시점은 스프링 컨테이너가 만들어지는 애플리케이션 로딩 시점에 적용할 수 있다.
따라서 `args` , `@args` , `@target` 같은 포인트컷 지시자가 있으면 스프링은 모든 스프링 빈에 AOP를 적용하려고 시도한다. 

앞서 설명한 것 처럼 프록시가 없으면 실행 시점에 판단 자체가 불가능하다.
문제는 이렇게 모든 스프링 빈에 AOP 프록시를 적용하려고 하면 스프링이 내부에서 사용하는 빈 중에는 `final`로 지정된 빈들도 있기 때문에 오류가 발생할 수 있다.
따라서 이러한 표현식은 최대한 프록시 적용 대상을 축소하는 표현식과 함께 사용해야 한다.

### @annotation, @args

**정의**

- `@annotation` : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
- `@args` : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트

```kotlin
@Import(AtAnnotationTest.AtAnnotationAspect::class)
@SpringBootTest
class AtAnnotationTest(
    @Autowired
    private val memberService: MemberService,
) {
    @Test
    fun success() {
        logger.info("memberService Proxy=${memberService.javaClass}")
        memberService.hello("helloA")
    }

    @Aspect
    class AtAnnotationAspect {

        @Around("@annotation(hello.aop.member.annotation.MethodAop)")
        fun doAtAnnotation(joinPoint: ProceedingJoinPoint): Any? {
            logger.info("[@annotation] ${joinPoint.signature}")
            return joinPoint.proceed()
        }
    }
}
```

### bean

**정의**

스프링 전용 포인트컷 지시자, 빈의 이름으로 지정
- 스프리에서만 사용할 수 있는 특별한 지시자.
- `bean(orderService) || bean(*Repository)`

```kotlin
@SpringBootTest
@Import(BeanTest.BeanAspect::class)
class BeanTest(
    @Autowired
    private val orderService: OrderService,
) {

    @Test
    fun success() {
        orderService.orderItem("itemA")
    }

    @Aspect
    class BeanAspect {
        @Around("bean(orderService) || bean(*Repository)")
        fun doLog(joinPoint: ProceedingJoinPoint): Any? {
            logger.info("[bean] ${joinPoint.signature}")
            return joinPoint.proceed()
        }
    }
}
```

## 매개변수 전달

다음 포인트컷 표현식을 사용해서 어드바이스에 매개변수를 전달할 수 있다.
- this, target, args, @target, @within, @annotation, @args

```kotlin
@Before("allMember() && args(arg,..)")
fun logArgs3(arg: String) {
    log.info("[logArgs3] arg=$arg")
}
```
- 포인트컷의 이름과 매개변수의 이름을 맞춰야 한다. - 
  - args
- 타입이 메서드에 지정한 타입으로 제한된다.

```kotlin
@SpringBootTest
@Import(ParameterTest.ParameterAspect::class)
class ParameterTest(
    @Autowired
    private val memberService: MemberService,
) {

    @Test
    fun success() {
        logger.info("memberService Proxy=${memberService.javaClass}")
        memberService.hello("helloA")
    }

    @Aspect
    class ParameterAspect {

        @Pointcut("execution(* hello.aop.member..*.*(..))")
        fun allMember() {
        }

        @Around("allMember()")
        fun logArgs1(joinPoint: ProceedingJoinPoint): Any? {
            val arg1 = joinPoint.args[0]
            logger.info("[logArg1]${joinPoint.signature}, args=$arg1")
            return joinPoint.proceed()
        }

        @Around("allMember() && args(arg, ..)")
        fun logArgs2(joinPoint: ProceedingJoinPoint, arg: Any): Any? {
            logger.info("[logArg2]${joinPoint.signature}, args=$arg")
            return joinPoint.proceed()
        }

        @Before("allMember() && args(arg, ..)")
        fun logArgs3(arg: String) {
            logger.info("[logArg3]args=$arg")
        }

        @Before("allMember() && this(obj)")
        fun thisArgs(obj: MemberService) { // 실제 빈으로 등록된 프록시 객체가 들어옴
            logger.info("[this]obj=${obj.javaClass}")
        }

        @Before("allMember() && target(obj)")
        fun targetArgs(obj: MemberService) { // 실제 대상 구현체가 들어옴
            logger.info("[target]obj=${obj.javaClass}")
        }

        @Before("allMember() && @target(annotation)")
        fun atTarget(annotation: ClassAop) {
            logger.info("[@target]annotation=$annotation")
        }

        @Before("allMember() && @within(annotation)")
        fun atWithin(annotation: ClassAop) {
            logger.info("[@within]annotation=$annotation")
        }

        @Before("allMember() && @annotation(annotation)")
        fun atAnnotation(annotation: MethodAop) {
            logger.info("[@annotation]annotationValue=${annotation.value}")
        }
    }
}
```