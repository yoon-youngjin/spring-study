# 빈 후처리기

빈 등록 시 빈 후처리기(BeanPostProcessor)를 통해서 조작이 가능하다.

<img width="665" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/928d285e-b2fc-4c4a-91a9-25a8ddce7058">

빈 후처리기는 빈을 조작하고 변경할 수 있는 후킹 포인트다.
빈 객체를 조작하거나 심지어 다른 객체로 바꾸어 버릴 수 있을 정도로 막강하다. 
일반적으로 스프링 컨테이너가 등록하는 특히 컴포넌트의 대상이 되는 빈들은 중간에 조작할 방법이 없는데, 빈 후처리기를 사용하면 개발자가 등록하는 모든 빈을 중간에 조작할 수 있다. 이 말은 **빈 객체를 프록시 객체로 교체**하는 것도 가능하다는 뜻이다.

> 참고: @PostConstruct
> 
> @PostConstruct는 스프링 빈 생성 이후에 빈을 초기화하는 역할을 한다. 빈의 초기화는 단순히 @PostConsturct 애노테이션이 붙은 초기화 메서드를 한번 호출하면 된다. 쉽게 이야기해서 생성된 빈을 한번 조작하는 것이다.
> 스프링은 CommonAnnotationBeanPostProcessor라는 빈 후처리기를 자동으로 등록하는데, 여기서에서 @PostConstruct 애노테이션이 붙은 메서드를 호출한다.

```kotlin
class PackageLogTracePostProcessor(
    private val basePackage: String,
    private val advisor: Advisor,
) : BeanPostProcessor {
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        logger.info("param beanName=$beanName, bean=$bean")

        // 프록시 적용 대상 여부 체크
        // 프록시 적용 대상이 아니면 원본을 그대로 진행
        val packageName = bean.javaClass.packageName
        if (!packageName.startsWith(basePackage)) {
            return bean
        }

        // 프록시 대상이면 프록시를 만들어서 반환
        val proxyFactory = ProxyFactory(bean)
        proxyFactory.addAdvisor(advisor)
        val proxy = proxyFactory.proxy
        logger.info("create proxy: target=${bean.javaClass}, proxy=${proxy.javaClass}")
        return proxy
    }
}
```

```kotlin
@Configuration
@Import(AppV1Config::class, AppV2Config::class)
class BeanPostProcessorConfig {
    @Bean
    fun logTracePostProcessor(logTrace: LogTrace): PackageLogTracePostProcessor {
        return PackageLogTracePostProcessor("hello.proxy.app", getAdvisor(logTrace))
    }

    private fun getAdvisor(logTrace: LogTrace): Advisor {
        //pointcut
        val pointcut = NameMatchMethodPointcut()
        pointcut.setMappedNames("request*", "order*", "save*")
        //advice
        val advice = LogTraceAdvice(logTrace)
        //advisor = pointcut + advice
        return DefaultPointcutAdvisor(pointcut, advice)
    }
}
```

```text
2024-01-22T22:37:35.101+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=mvcValidator, bean=org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport$NoOpValidator@984169e
2024-01-22T22:37:35.158+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration, bean=org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration@7e5843db
2024-01-22T22:37:35.167+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration$StringHttpMessageConverterConfiguration, bean=org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration$StringHttpMessageConverterConfiguration@6d5c2745
2024-01-22T22:37:35.179+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=stringHttpMessageConverter, bean=org.springframework.http.converter.StringHttpMessageConverter@11900483
2024-01-22T22:37:35.180+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration$MappingJackson2HttpMessageConverterConfiguration, bean=org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration$MappingJackson2HttpMessageConverterConfiguration@14a049f9
2024-01-22T22:37:35.181+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperConfiguration, bean=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperConfiguration@5de6cf3a
2024-01-22T22:37:35.182+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperBuilderConfiguration, bean=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperBuilderConfiguration@5a3a1bf9
2024-01-22T22:37:35.183+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$Jackson2ObjectMapperBuilderCustomizerConfiguration, bean=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$Jackson2ObjectMapperBuilderCustomizerConfiguration@58740366
2024-01-22T22:37:35.186+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=spring.jackson-org.springframework.boot.autoconfigure.jackson.JacksonProperties, bean=org.springframework.boot.autoconfigure.jackson.JacksonProperties@4bd51d3e
2024-01-22T22:37:35.186+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$ParameterNamesModuleConfiguration, bean=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$ParameterNamesModuleConfiguration@4b74b35
2024-01-22T22:37:35.189+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=parameterNamesModule, bean=com.fasterxml.jackson.module.paramnames.ParameterNamesModule@757194dc
2024-01-22T22:37:35.189+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonMixinConfiguration, bean=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonMixinConfiguration@5d497a91
2024-01-22T22:37:35.230+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=jsonMixinModuleEntries, bean=org.springframework.boot.jackson.JsonMixinModuleEntries@737d100a
2024-01-22T22:37:35.231+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=jsonMixinModule, bean=org.springframework.boot.jackson.JsonMixinModule@1d1cbd0f
2024-01-22T22:37:35.232+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration, bean=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration@77049094
2024-01-22T22:37:35.241+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=jsonComponentModule, bean=org.springframework.boot.jackson.JsonComponentModule@1e0a864d
2024-01-22T22:37:35.242+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=standardJacksonObjectMapperBuilderCustomizer, bean=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$Jackson2ObjectMapperBuilderCustomizerConfiguration$StandardJackson2ObjectMapperBuilderCustomizer@440e3ce6
2024-01-22T22:37:35.245+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=jacksonObjectMapperBuilder, bean=org.springframework.http.converter.json.Jackson2ObjectMapperBuilder@2c7106d9
2024-01-22T22:37:35.256+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=jacksonObjectMapper, bean=com.fasterxml.jackson.databind.ObjectMapper@235d29d6
2024-01-22T22:37:35.257+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=mappingJackson2HttpMessageConverter, bean=org.springframework.http.converter.json.MappingJackson2HttpMessageConverter@1fdca564
2024-01-22T22:37:35.261+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=messageConverters, bean=org.springframework.boot.autoconfigure.http.HttpMessageConverters@22ebccb9
2024-01-22T22:37:35.269+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=applicationTaskExecutor, bean=org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor@3fba233d
2024-01-22T22:37:35.295+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=requestMappingHandlerAdapter, bean=org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter@2538bc06
2024-01-22T22:37:35.299+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=mvcResourceUrlProvider, bean=org.springframework.web.servlet.resource.ResourceUrlProvider@7d57dbb5
2024-01-22T22:37:35.306+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=welcomePageHandlerMapping, bean=org.springframework.boot.autoconfigure.web.servlet.WelcomePageHandlerMapping@1c9fbb61
2024-01-22T22:37:35.308+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=welcomePageNotAcceptableHandlerMapping, bean=org.springframework.boot.autoconfigure.web.servlet.WelcomePageNotAcceptableHandlerMapping@36c281ed
2024-01-22T22:37:35.310+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=localeResolver, bean=org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver@4bd5849e
2024-01-22T22:37:35.311+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=themeResolver, bean=org.springframework.web.servlet.theme.FixedThemeResolver@37496720
2024-01-22T22:37:35.312+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=flashMapManager, bean=org.springframework.web.servlet.support.SessionFlashMapManager@7593ea79
2024-01-22T22:37:35.346+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=requestMappingHandlerMapping, bean=org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping@34332b8d
2024-01-22T22:37:35.347+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=mvcPatternParser, bean=org.springframework.web.util.pattern.PathPatternParser@724b939e
2024-01-22T22:37:35.348+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=mvcUrlPathHelper, bean=org.springframework.web.util.UrlPathHelper@6f8aba08
2024-01-22T22:37:35.348+09:00  INFO 35533 --- [           main] h.p.c.v.p.PackageLogTracePostProcessor   : param beanName=mvcPathMatcher, bean=org.springframework.util.AntPathMatcher@5626d18c
...
```

- 수 많은 빈이 postProcessor에 들어온다.

## 스프링이 제공하는 빈 후처리기

```groovy
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

해당 라이브러리를 추가하면 aspectjweaver라는 aspectJ 관련 라이브러리를 등록하고, 스프링 부트가 AOP 관련 클래스를 자동으로 스프링 빈에 등록한다.

**자동 프록시 생성기 - AutoProxyCreator**
- 앞서 이야기한 스프링 부트 자동 설정으로 AnnotationAwareAspectJAutoProxyCreator 라는 빈 후처리기가 빈으로 자동 등록된다.
  - 프록시를 생성해주느 빈 후처리기
- 해당 빈 후처리기는 스프링 빈으로 등록된 Advisor들을 자동으로 찾아서 프록시 필요한 곳에 자동으로 프록시를 적용해준다.
- Advisor 안에는 Pointcut과 Advice가 모두 포함되어 있다. 따라서 Advisor만 알고 있으면 그 안에 있는 Pointcut으로 어떤 스프링 빈에 프록시를 적용해야 할지 알 수 있다. 그리고 Advice로 부가 기능을 적용하면 된다.

> 참고
> 
> `AnnotationAwareAspectJAutoProxyCreator` 는 @AspectJ와 관련된 AOP 기능도 자동으로 찾아서 처리해준다.
> `Advisor` 는 물론이고, `@Aspect` 도 자동으로 인식해서 프록시를 만들고 AOP를 적용해준다. `@Aspect` 에 대한 자세한 내용은 뒤에 설명한다.

<img width="545" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/aea4559c-3496-4249-b309-dce80918a95b">

1. 생성: 스프링이 스피링 빈 대상이 되는 객체를 생성한다. (@Bean, 컴포넌트 스캔 모두 포함)
2. 전달: 생성된 객체를 빈 저장소에 등록하기 직전에 빈 후처리기에 전달한다.
3. 모든 Advisor 빈 조회: 자동 프록시 생성기 - 빈 후처리기는 스프링 컨테이너에서 모든 Advisor를 조회한다.
4. 프록시 적용 대상 체크: 앞서 조회한 Advisor에 포함되어 있는 포인트컷을 사용해서 해당 객체가 프록시를 적용할 대상인지 아닌지 판단한다. 이때 객체의 클래스 정보는 물론이고, 해당 객체의 모든 메서드를 포인트컷에 하나하나 매칭해본다. 그래서 조건이 하나라도 만족하면 프록시 적용 대상이 된다.
5. 프록시 생성: 프록시 적용 대상이면 프록시를 생성하고 반환해서 프록시를 스프링 빈으로 등록한다. 만약 프록시 적용 대상이 아니라면 원본 객체를 반환해서 원본 객체를 스프링 빈으로 등록한다.
6. 빈 등록: 반환된 객체는 스프링 빈으로 등록된다.

### 프록시와 어드바이저

**생성된 프록시**

<img width="830" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/f6667757-ce4a-47f4-8d01-8692b670b598">

- 프록시는 내부에 어드바이저와 실제 호출해야할 대상 객체(target)을 알고 있다.
- 실시간으로 호출한 메서드가 포인트컷 조건에 맞을때 advice를 실행한다.

> 참고
> 
> 프록시를 만들때 사용하는 포인트컷과 실행 단계에서 사용하는 포인트컷을 구분해야한다.
> 프록시를 생성할 때는 메서드 중에 하나라도 프록시가 필요한 경우 프록시를 생성한다. 하지만 실제 실행단계에서는 구분해야하기 때문 (orderControllerV1 -> request, no-log)

```kotlin
@Configuration
@Import(AppV1Config::class, AppV2Config::class)
class AutoProxyConfig {
    @Bean
    fun getAdvisor1(logTrace: LogTrace): Advisor {
        //pointcut
        val pointcut = NameMatchMethodPointcut()
        pointcut.setMappedNames("request*", "order*", "save*")
        //advice
        val advice = LogTraceAdvice(logTrace)
        //advisor = pointcut + advice
        return DefaultPointcutAdvisor(pointcut, advice)
    }
}
```

spring-aop를 라이브러리로 등록함으로써 자동 빈 후처리기를 등록해주기 때문에 이전 v4와 달리 별도의 빈 후처리기를 등록하지 않아도 된다.

**AspectJExpressionPointcut**

현재는 메서드 이름에 포인트컷 조건이 일치하면 모두 프록시를 생성하기 때문에 기대하지 않았던 클래스들도 프록시를 생성하고 있다.
이를 해결하기 위해 패키지에 메서드 이름까지 함께 지정할 수 있는 정밀한 포인트컷이 필요하다.

```kotlin
@Configuration
@Import(AppV1Config::class, AppV2Config::class)
class AutoProxyConfig {
  ...
  @Bean
  fun getAdvisor2(logTrace: LogTrace): Advisor {
    // pointcut
    val pointcut = AspectJExpressionPointcut()
    pointcut.expression = "execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))"
    // advice
    val advice = LogTraceAdvice(logTrace)
    // advisor = pointcut + advice
    return DefaultPointcutAdvisor(pointcut, advice)
  }
}
```

- execution(* hello.proxy.app..*(..)) : AspectJ가 제공하는 포인트컷 표현식
  - `*` : 모든 반환 타입
  - `hello.proxy.app..` : 해당 패키지와 그 하위 패키지
  - `*(..)` : `*` 모든 메서드 이름, `(..)` 파라미터는 상관 없음

### 하나의 프록시, 여러 Advisor 적용

스프링 빈이 여러 advisor의 포인트컷 조건에 만족한다면 몇 개의 프록시가 생성될까?
프록시 자동 생성기는 프록시를 하나만 생성한다. 왜냐하면 프록시 팩토리가 생성하는 프록시는 내부에 여러 advisor들을 포함할 수 있기 때문이다.
따라서 프록시를 여러 개 생성해서 비용을 낭비할 이유가 없다.

**프록시 자동 생성기 상황별 정리**
- advisor1의 포인트컷만 만족 -> 프록시 1개 생성, 프록시에 advisor1만 포함
- advisor1, advisor2 포인트컷 모두 만족 -> 프록시 1개 생성, 프록시에 1, 2 모두 포함
- 둘다 만족X -> 프록시 생성X, 원본 객체가 빈 저장소에 등록

<img width="800" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/9f11f631-b8b2-4208-8bd1-e0d110d19aa7">

<img width="803" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/12b8cb3e-90ac-47f5-8fc5-582a910889ae">

## @Aspect AOP

스프링은 @Aspect 어노테이션으로 매우 편리하게 포인트컷과 어드바이스로 구성되어 있는 어드바이저 생성 기능을 지원한다.

```kotlin
@Aspect
class LogTraceAspect(
  private val logTrace: LogTrace,
) {

  @Around("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))") // pointcut
  fun execute(joinPoint: ProceedingJoinPoint): Any? { // advice 로직
    var status: TraceStatus? = null

    logger.info("target=${joinPoint.target}") //실제 호출 대상
    logger.info("getArgs=${joinPoint.args}") //전달인자
    logger.info("getSignature=${joinPoint.signature}") //join point 시그니처

    return try {
      val message = joinPoint.signature.toShortString()
      status = logTrace.begin(message)
      // target 호출
      val result = joinPoint.proceed()
      logTrace.end(status)
      result
    } catch (e: Exception) {
      logTrace.exception(status, e)
      throw e
    }
  }
}
```

- @Aspect: 어노테이션 기반 프록시를 적용할 때 필요
- @Around(...)
  - @Around의 값에 포인트컷 표현식을 넣는다. 표현식은 AspectJ 표현식
  - @Around의 메서드는 어드바이스(Advice)가 된다.
- joinPoint는 이전에 MethodInvocation invocation 과 유사한 기능이다. 내부에 실제 호출 대상, 전달 인자, 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함
- jointPoint.proceed(): 실제 target을 호출

```kotlin
@Configuration
@Import(AppV1Config::class, AppV2Config::class)
class AopConfig {
    @Bean
    fun logTraceAspect(logTrace: LogTrace): LogTraceAspect {
        return LogTraceAspect(logTrace)
    }
}
```

### @Aspect 프록시 설명

자동 프록시 생성기(AnnotationAwareAspectJAutoProxyCreator)는 어드바이저를 자동으로 찾아와서 프록시를 생성하고 적용하는 역할과 더불어 @Aspect를 찾아서 어드바이저를 만들어주는 역할도 한다.

1. @Aspect를 보고 어드바이저로 변환해서 저장한다.
2. 어드바이저를 기반으로 프록시를 생성한다.

**@Aspect를 보고 어드바이저로 변환 과정**

<img width="813" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/896bbdea-14b5-4422-88dc-c12c4166e525">

1. 실행: 스프링 애플리케이션 로딩 시점에 자동 프록시 생성기를 호출
2. 모든 @Aspect 빈 조회: 자동 프록시 생성기는 스프링 컨테이너에서 @Aspect 어노테이션이 붙은 스프링 빈을 모두 조회
3. 어드바이저 생성: @Aspect 어드바이저 빌더를 통해 @Aspect 어노테이션 정보를 기반으로 어드바이저를 생성한다.
4. @Aspect 기반 어드바이저 저장: 생성한 어드바이저를 @Aspect 어드바이저 빌더 내부에 저장한다.

> @Aspect 어드바이저 빌더
> 
> BeanFactoryAspectJAdvisorsBuilder 클래스, @Aspect 정보를 기반으로 포인트컷, 어드바이스, 어드바이저를 생성하고 보관하는 것을 담당한다. @Aspect 정보를 기반으로 어드바이저를 만들고, @Aspect 어드바이저 빌더 내부 저장소에 캐시한다. 캐시에 어드바이저가 이미 만들어져 있는 경우 캐시에 저장된 어드바이저를 반환한다.

**어드바이저를 기반으로 프록시 생성**

<img width="816" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/50f537b8-8504-4404-a55f-17fe1535f124">

1. 생성: 스프링 빈 대상이 되는 객체를 생성
2. 전달: 생성된 객체를 빈 저장소에 등록하기 직전에 빈 후처리기에 전달
3. Advisor 빈 조회: 스프링 컨테이너에서 Advisor 빈을 모두 조회
4. @Aspect Advisor 조회: @Aspect 어드바이저 빌더 내부에 저장된 Advisor를 모두 조회
5. 프록시 적용 대상 체크: 앞서 3, 4에서 조회한 Advisor에 포함되어 있는 포인트컷을 사용해서 해당 객체를 포인터컷에 매칭해본다. (하나라도 만족하면 프록시 적용 대상)
6. 프록시 생성: 프록시 적용 대상이라면 프록시 생성하고 반환한다. (대상이 아니라면 원본 객체가 저장)
7. 빈 등록: 반환된 객체는 스프링 빈으로 등록


