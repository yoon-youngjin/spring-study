# 스프링 컨테이너와 스프링 빈

### 스프링 컨테이너 생성

```java
//스프링 컨테이너 생성
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

- `ApplicationContext` 를 스프링 컨테이너라 한다.
- `ApplicationContext` 는 인터페이스이다.
- 스프링 컨테이너는 XML을 기반으로 만들 수 있고, 애노테이션 기반의 자바 설정 클래스로 만들 수 있다.
- 직전에 `AppConfig` 를 사용했던 방식이 애노테이션 기반의 자바 설정 클래스로 스프링 컨테이너를 만든 것이다.
- 자바 설정 클래스를 기반으로 스프링 컨테이너( `ApplicationContext` )를 만들어보자.
  - `new AnnotationConfigApplicationContext`(`AppConfig.class`);
  - 이 클래스는 `ApplicationContext` 인터페이스의 구현체이다.

#### 스프링 컨테이너의 생성 과정

**1. 스프링 컨테이너 생성**

![image](https://user-images.githubusercontent.com/83503188/191009168-576a2d38-0726-435f-9e30-4eb0e72e5226.png)

> key - 빈 이름, value - 빈 객체

- `new AnnotationConfigApplicationContext(AppConfig.class)`
- 스프링 컨테이너를 생성할 때는 구성 정보를 지정해주어야 한다.
- 여기서는 `AppConfig.class` 를 구성 정보로 지정했다.


**2. 스프링 빈 등록**


![image](https://user-images.githubusercontent.com/83503188/191009533-97b30e97-1849-4c40-b426-316d2ea49525.png)

**빈 이름**

- 빈 이름은 메서드 이름을 사용한다.
- 빈 이름을 직접 부여할 수 도 있다.
- `@Bean(name="memberService2")`


>주의: 빈 이름은 항상 다른 이름을 부여해야 한다. 같은 이름을 부여하면, 다른 빈이 무시되거나, 기존 빈을 덮어버리거나 설정에 따라 오류가 발생한다.


3. 스프링 빈 의존관계 설정 - 준비

![image](https://user-images.githubusercontent.com/83503188/191009626-b154db8b-0604-4b54-bb4f-67ddc8c833e1.png)

4. 스프링 빈 의존관계 설정 - 완료

![image](https://user-images.githubusercontent.com/83503188/191009778-e5b61481-3913-4d91-87ce-12f1b6bb06f3.png)

- 현재 단계가 앞에서 설명한 동적인 객체 인스턴스 의존관계를 연결하는 단계이다.
- 스프링 컨테이너는 설정 정보를 참고해서 의존관계를 주입(DI)한다.
- 단순히 자바 코드를 호출하는 것 같지만, 차이가 있다. 이 차이는 뒤에 싱글톤 컨테이너에서 설명한다.

### 컨테이너에 등록된 모든 빈 조회

```java
public class ApplicationContextTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    public void findAllBean() throws Exception {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("name = " + beanDefinitionName + " object = " + bean);
        }
    }
}

```

```text
name = org.springframework.context.annotation.internalConfigurationAnnotationProcessor object = org.springframework.context.annotation.ConfigurationClassPostProcessor@55562aa9
name = org.springframework.context.annotation.internalAutowiredAnnotationProcessor object = org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor@655ef322
name = org.springframework.context.annotation.internalCommonAnnotationProcessor object = org.springframework.context.annotation.CommonAnnotationBeanPostProcessor@7e276594
name = org.springframework.context.event.internalEventListenerProcessor object = org.springframework.context.event.EventListenerMethodProcessor@3401a114
name = org.springframework.context.event.internalEventListenerFactory object = org.springframework.context.event.DefaultEventListenerFactory@5066d65f
name = appConfig object = dev.yoon.core.AppConfig$$EnhancerBySpringCGLIB$$e9a11fdb@4233e892
name = memberService object = dev.yoon.core.member.MemberServiceImpl@77d2e85
name = orderService object = dev.yoon.core.order.OrderServiceImpl@3ecd267f
name = memberRepository object = dev.yoon.core.member.MemoryMemberRepository@58ffcbd7
name = discountPolicy object = dev.yoon.core.discount.RateDiscountPolicy@555cf22

```

위에서 5줄은 스프링 내부적으로 스프링 자체를 확장하기 위한 Bean들


```java
public class ApplicationContextTest {

    
...

    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    public void findApplicationBean() throws Exception {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            //Role ROLE_APPLICATION: 직접 등록한 애플리케이션 빈
            //Role ROLE_INFRASTRUCTURE: 스프링이 내부에서 사용하는 빈
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name=" + beanDefinitionName + " object=" + bean);
            }
        }
    }


}
```

- 모든 빈 출력하기
  - 실행하면 스프링에 등록된 모든 빈 정보를 출력할 수 있다.
  - `ac.getBeanDefinitionNames()` : 스프링에 등록된 모든 빈 이름을 조회한다.
  - `ac.getBean()` : 빈 이름으로 빈 객체(인스턴스)를 조회한다.
- 애플리케이션 빈 출력하기
  - 스프링이 내부에서 사용하는 빈은 제외하고, 내가 등록한 빈만 출력해보자.
  - 스프링이 내부에서 사용하는 빈은 `getRole()` 로 구분할 수 있다.
    - `ROLE_APPLICATION` : 일반적으로 사용자가 정의한 빈
    - `ROLE_INFRASTRUCTURE` : 스프링이 내부에서 사용하는 빈

### 스프링 빈 조회 - 기본
스프링 컨테이너에서 스프링 빈을 찾는 가장 기본적인 조회 방법
- `ac.getBean(빈이름, 타입)`
- `ac.getBean(타입)`
- 조회 대상 스프링 빈이 없으면 예외 발생
  - `NoSuchBeanDefinitionException: No bean named 'xxxxx' available`



```java
public class ApplicationContextBasicFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("빈 이름으로 조회")
    void findBeanByName() {
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        System.out.println("memberService = " + memberService);
        System.out.println("memberService.getClass() = " + memberService.getClass());
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("이름 없이 타입으로만 조회")
    void findBeanByType() {
        MemberService memberService = ac.getBean(MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("구체 타입으로 조회")
    void findBeanByName2() {
        MemberService memberService = ac.getBean("memberService", MemberServiceImpl.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("빈 이름으로 조회X")
    void findBeanByNameX() {
        //ac.getBean("xxxxx", MemberService.class);
        assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("xxxxx", MemberService.class));
    }

}
```

> 참고: 구체 타입으로 조회하면 변경시 유연성이 떨어진다.



### 스프링 빈 조회 - 동일한 타입이 둘 이상

- 타입으로 조회시 같은 타입의 스프링 빈이 둘 이상이면 오류가 발생한다. 이때는 빈 이름을 지정하자.
- `ac.getBeansOfType()` 을 사용하면 해당 타입의 모든 빈을 조회할 수 있다.


![image](https://user-images.githubusercontent.com/83503188/191016266-2b7f3788-8366-40a5-9b64-7a547798aaa8.png)

![image](https://user-images.githubusercontent.com/83503188/191016301-9c281dc9-64ca-45be-acfa-153645248858.png)


```java
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ApplicationContextSameBeanFindTest {

    @Configuration
    static class SameBeanConfig {

        @Bean
        public MemberRepository memberRepository1() {
            return new MemoryMemberRepository();
        }

        @Bean
        public MemberRepository memberRepository2() {
            return new MemoryMemberRepository();
        }

    }

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SameBeanConfig.class);

    @Test
    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 중복 오류가 발생한다.")
    void findBeanByTypeDuplicate() {
        Assertions.assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(MemberRepository.class));
    }

    @Test
    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 빈 이름을 지정하면 된다")
    void findBeanByName() {
        MemberRepository memberRepository = ac.getBean("memberRepository1", MemberRepository.class);
        assertThat(memberRepository).isInstanceOf(MemberRepository.class);
    }

    @Test
    @DisplayName("특정 타입을 모두 조회하기")
    void findAllBeanByType() {
        Map<String, MemberRepository> beansOfType = ac.getBeansOfType(MemberRepository.class);
        for (String key : beansOfType.keySet()) {
            System.out.println("key = " + key + " value = " + beansOfType.get(key));
        }

        System.out.println("beansOfType = " + beansOfType);
        assertThat(beansOfType.size()).isEqualTo(2);
    }
}

```

### 스프링 빈 조회 - 상속 관계

- 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
- 그래서 모든 자바 객체의 최고 부모인 Object 타입으로 조회하면, 모든 스프링 빈을 조회한다.



![image](https://user-images.githubusercontent.com/83503188/191016984-7a52b0a4-916a-44ae-ab01-18348eb88d78.png)




```java
public class ApplicationContextExtendsFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

    // DiscountPolicy로 조회하면 RateDiscountPolicy, FixDiscountPolicy 모두 조회
    @Configuration
    static class TestConfig {

        @Bean
        public DiscountPolicy rateDiscountPolicy() {
            return new RateDiscountPolicy();
        }

        @Bean
        public DiscountPolicy fixDiscountPolicy() {
            return new FixDiscountPolicy();
        }

    }

    @Test
    @DisplayName("부모 타입으로 조회시, 자식이 둘 이상이 있으면, 중복 오류가 발생한다")
    void findBeanByParentTypeDuplicate() {
        assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(DiscountPolicy.class));
    }

    @Test
    @DisplayName("부모 타입으로 조회시, 자식이 둘 이상 있으면, 빈 이름을 지정하면 된다")
    void findBeanByParentTypeBeanName() {
        DiscountPolicy rateDiscountPolicy = ac.getBean("rateDiscountPolicy", DiscountPolicy.class);

        assertThat(rateDiscountPolicy).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("특정 하위 타입으로 조회")
    void findBeanBySubType() {
        RateDiscountPolicy rateDiscountPolicy = ac.getBean(RateDiscountPolicy.class);

        assertThat(rateDiscountPolicy).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("부모 타입으로 모두 조회하기")
    void findAllBeanByParentType() {
        Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);

        for (String key : beansOfType.keySet()) {
            System.out.println("key = " + key + " value=" + beansOfType.get(key));
        }

        assertThat(beansOfType.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("부모 타입으로 모두 조회하기 - Object")
    void findAllBeanByObjectType() {
        Map<String, Object> beansOfType = ac.getBeansOfType(Object.class);

        for (String key : beansOfType.keySet()) {
            System.out.println("key = " + key + " value=" + beansOfType.get(key));
        }

    }


}
```



```text
key = org.springframework.context.annotation.internalConfigurationAnnotationProcessor value=org.springframework.context.annotation.ConfigurationClassPostProcessor@4233e892
key = org.springframework.context.annotation.internalAutowiredAnnotationProcessor value=org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor@77d2e85
key = org.springframework.context.annotation.internalCommonAnnotationProcessor value=org.springframework.context.annotation.CommonAnnotationBeanPostProcessor@3ecd267f
key = org.springframework.context.event.internalEventListenerProcessor value=org.springframework.context.event.EventListenerMethodProcessor@58ffcbd7
key = org.springframework.context.event.internalEventListenerFactory value=org.springframework.context.event.DefaultEventListenerFactory@555cf22
key = applicationContextExtendsFindTest.TestConfig value=dev.yoon.core.beanfind.ApplicationContextExtendsFindTest$TestConfig$$EnhancerBySpringCGLIB$$bebd1f24@6bb2d00b
key = rateDiscountPolicy value=dev.yoon.core.discount.RateDiscountPolicy@3c9bfddc
key = fixDiscountPolicy value=dev.yoon.core.discount.FixDiscountPolicy@1a9c38eb
key = environment value=StandardEnvironment {activeProfiles=[], defaultProfiles=[default], propertySources=[PropertiesPropertySource@832292933 {name='systemProperties', properties={sun.desktop=windows, awt.toolkit=sun.awt.windows.WToolkit, java.specification.version=11, sun.cpu.isalist=amd64, sun.jnu.encoding=MS949, java.class.path=C:\Users\dudwl\.m2\repository\org\junit\platform\junit-platform-launcher\1.8.2\junit-platform-launcher-1.8.2.jar;C:\Users\dudwl\.m2\repository\org\junit\platform\junit-platform-engine\1.8.2\junit-platform-engine-1.8.2.jar;C:\Users\dudwl\.m2\repository\org\opentest4j\opentest4j\1.2.0\opentest4j-1.2.0.jar;C:\Users\dudwl\.m2\repository\org\junit\platform\junit-platform-commons\1.8.2\junit-platform-commons-1.8.2.jar;C:\Users\dudwl\.m2\repository\org\apiguardian\apiguardian-api\1.1.2\apiguardian-api-1.1.2.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\lib\idea_rt.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\plugins\junit\lib\junit5-rt.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\plugins\junit\lib\junit-rt.jar;C:\Users\dudwl\WorkSpace\SSS\core\out\test\classes;C:\Users\dudwl\WorkSpace\SSS\core\out\production\classes;C:\Users\dudwl\WorkSpace\SSS\core\out\production\resources;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter-test\2.7.3\ce5a11117ac6c92d38ce071ff2273799862659b7\spring-boot-starter-test-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter\2.7.3\6b0c093af667bf645cd5f49372e2a2540ae2855f\spring-boot-starter-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-test-autoconfigure\2.7.3\42924dceee5636b5c12ed36011cc333b40e1f756\spring-boot-test-autoconfigure-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-test\2.7.3\34d3fbf68aa72beef1515ab4a439c23c8c41280e\spring-boot-test-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-test\5.3.22\48375b44c82945e12012ec56dc4090c805b4508b\spring-test-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-core\5.3.22\661fc01832716c7eedebf995c6841b2f7117c63d\spring-core-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\com.jayway.jsonpath\json-path\2.7.0\f9d7d9659f2694e61142046ff8a216c047f263e8\json-path-2.7.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\jakarta.xml.bind\jakarta.xml.bind-api\2.3.3\48e3b9cfc10752fba3521d6511f4165bea951801\jakarta.xml.bind-api-2.3.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.assertj\assertj-core\3.22.0\c300c0c6a24559f35fa0bd3a5472dc1edcd0111e\assertj-core-3.22.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.hamcrest\hamcrest\2.2\1820c0968dba3a11a1b30669bb1f01978a91dedc\hamcrest-2.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter\5.8.2\5a817b1e63f1217e5c586090c45e681281f097ad\junit-jupiter-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.mockito\mockito-junit-jupiter\4.5.1\f81fb60bd69b3a6e5537ae23b883326f01632a61\mockito-junit-jupiter-4.5.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.mockito\mockito-core\4.5.1\ed456e623e5afc6f4cee3ae58144e5c45f3b3bf\mockito-core-4.5.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.skyscreamer\jsonassert\1.5.1\6d842d0faf4cf6725c509a5e5347d319ee0431c3\jsonassert-1.5.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.xmlunit\xmlunit-core\2.9.0\8959725d90eecfee28acd7110e2bb8460285d876\xmlunit-core-2.9.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-autoconfigure\2.7.3\4c96169e8d71c9c41f07a40d011dbd41898180ac\spring-boot-autoconfigure-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot\2.7.3\3a8d641077565b7eaec3b2f91d5b83a6800f5895\spring-boot-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter-logging\2.7.3\a1e4a13b656182ba10b4c0c7848f91cd6f854fdf\spring-boot-starter-logging-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\jakarta.annotation\jakarta.annotation-api\1.3.5\59eb84ee0d616332ff44aba065f3888cf002cd2d\jakarta.annotation-api-1.3.5.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.yaml\snakeyaml\1.30\8fde7fe2586328ac3c68db92045e1c8759125000\snakeyaml-1.30.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-jcl\5.3.22\811ace5e5eb379654ed96fd7844809db51af74a5\spring-jcl-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.minidev\json-smart\2.4.8\7c62f5f72ab05eb54d40e2abf0360a2fe9ea477f\json-smart-2.4.8.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.slf4j\slf4j-api\1.7.36\6c62681a2f655b49963a5983b8b0950a6120ae14\slf4j-api-1.7.36.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\jakarta.activation\jakarta.activation-api\1.2.2\99f53adba383cb1bf7c3862844488574b559621f\jakarta.activation-api-1.2.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter-params\5.8.2\ddeafe92fc263f895bfb73ffeca7fd56e23c2cce\junit-jupiter-params-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter-api\5.8.2\4c21029217adf07e4c0d0c5e192b6bf610c94bdc\junit-jupiter-api-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.bytebuddy\byte-buddy\1.12.13\35ffee9c24b1c68b08d9207e1a2d3da1add6166\byte-buddy-1.12.13.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.bytebuddy\byte-buddy-agent\1.12.13\5a4ed1c2eb9e8d7272b36b2b16757e5c653ab650\byte-buddy-agent-1.12.13.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\com.vaadin.external.google\android-json\0.0.20131108.vaadin1\fa26d351fe62a6a17f5cda1287c1c6110dec413f\android-json-0.0.20131108.vaadin1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-context\5.3.22\fdd59bb4795c7a399e95ec4a5c8b91103e3189fd\spring-context-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\ch.qos.logback\logback-classic\1.2.11\4741689214e9d1e8408b206506cbe76d1c6a7d60\logback-classic-1.2.11.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.apache.logging.log4j\log4j-to-slf4j\2.17.2\17dd0fae2747d9a28c67bc9534108823d2376b46\log4j-to-slf4j-2.17.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.slf4j\jul-to-slf4j\1.7.36\ed46d81cef9c412a88caef405b58f93a678ff2ca\jul-to-slf4j-1.7.36.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.minidev\accessors-smart\2.4.8\6e1bee5a530caba91893604d6ab41d0edcecca9a\accessors-smart-2.4.8.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.apiguardian\apiguardian-api\1.1.2\a231e0d844d2721b0fa1b238006d15c6ded6842a\apiguardian-api-1.1.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.platform\junit-platform-commons\1.8.2\32c8b8617c1342376fd5af2053da6410d8866861\junit-platform-commons-1.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.opentest4j\opentest4j\1.2.0\28c11eb91f9b6d8e200631d46e20a7f407f2a046\opentest4j-1.2.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-aop\5.3.22\2f9f00efbff8432f145ccffeb93e6a1819bac362\spring-aop-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-beans\5.3.22\866c2022b5fef05b1702f4a07cfa5598660ce08a\spring-beans-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-expression\5.3.22\c056f9e9994b18c95deead695f9471952d1f21d1\spring-expression-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\ch.qos.logback\logback-core\1.2.11\a01230df5ca5c34540cdaa3ad5efb012f1f1f792\logback-core-1.2.11.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.apache.logging.log4j\log4j-api\2.17.2\f42d6afa111b4dec5d2aea0fe2197240749a4ea6\log4j-api-2.17.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.ow2.asm\asm\9.1\a99500cf6eea30535eeac6be73899d048f8d12a8\asm-9.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter-engine\5.8.2\c598b4328d2f397194d11df3b1648d68d7d990e3\junit-jupiter-engine-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.objenesis\objenesis\3.2\7fadf57620c8b8abdf7519533e5527367cb51f09\objenesis-3.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.platform\junit-platform-engine\1.8.2\b737de09f19864bd136805c84df7999a142fec29\junit-platform-engine-1.8.2.jar, java.vm.vendor=Amazon.com Inc., sun.arch.data.model=64, idea.test.cyclic.buffer.size=1048576, user.variant=, java.vendor.url=https://aws.amazon.com/corretto/, user.timezone=Asia/Seoul, os.name=Windows 10, java.vm.specification.version=11, sun.java.launcher=SUN_STANDARD, user.country=KR, sun.boot.library.path=C:\Users\dudwl\.jdks\corretto-11.0.14.1\bin, sun.java.command=com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 dev.yoon.core.beanfind.ApplicationContextExtendsFindTest,findAllBeanByObjectType, jdk.debug=release, sun.cpu.endian=little, user.home=C:\Users\dudwl, user.language=ko, java.specification.vendor=Oracle Corporation, java.version.date=2022-02-08, java.home=C:\Users\dudwl\.jdks\corretto-11.0.14.1, file.separator=\, java.vm.compressedOopsMode=32-bit, line.separator=
, java.specification.name=Java Platform API Specification, java.vm.specification.vendor=Oracle Corporation, java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment, user.script=, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, java.runtime.version=11.0.14.1+10-LTS, user.name=dudwl, path.separator=;, os.version=10.0, java.runtime.name=OpenJDK Runtime Environment, file.encoding=UTF-8, java.vm.name=OpenJDK 64-Bit Server VM, java.vendor.version=Corretto-11.0.14.10.1, java.vendor.url.bug=https://github.com/corretto/corretto-11/issues/, java.io.tmpdir=C:\Users\dudwl\AppData\Local\Temp\, java.version=11.0.14.1, user.dir=C:\Users\dudwl\WorkSpace\SSS\core, os.arch=amd64, java.vm.specification.name=Java Virtual Machine Specification, java.awt.printerjob=sun.awt.windows.WPrinterJob, sun.os.patch.level=, java.library.path=C:\Users\dudwl\.jdks\corretto-11.0.14.1\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\Python310\Scripts\;C:\Python310\;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\Intel\WiFi\bin\;C:\Program Files\Common Files\Intel\WirelessCommon\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\Git\cmd;C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\nodejs\;C:\ProgramData\chocolatey\bin;C:\Program Files\Java\jdk-11.0.15\bin;C:\Program Files\PuTTY\;C:\Users\dudwl\AppData\Local\Microsoft\WindowsApps;C:\Users\dudwl\AppData\Local\Programs\Microsoft VS Code\bin;C:\Users\dudwl\AppData\Roaming\npm;C:\Program Files\JetBrains\PyCharm 2022.1\bin;;., java.vm.info=mixed mode, java.vendor=Amazon.com Inc., java.vm.version=11.0.14.1+10-LTS, sun.io.unicode.encoding=UnicodeLittle, java.class.version=55.0}}, SystemEnvironmentPropertySource@1280603381 {name='systemEnvironment', properties={USERDOMAIN_ROAMINGPROFILE=LAPTOP-12DDBOR1, LOCALAPPDATA=C:\Users\dudwl\AppData\Local, ChocolateyLastPathUpdate=132918073329451242, PROCESSOR_LEVEL=6, USERDOMAIN=LAPTOP-12DDBOR1, FPS_BROWSER_APP_PROFILE_STRING=Internet Explorer, LOGONSERVER=\\LAPTOP-12DDBOR1, JAVA_HOME=C:\Program Files\Java\jdk-11.0.15, SESSIONNAME=Console, ALLUSERSPROFILE=C:\ProgramData, PROCESSOR_ARCHITECTURE=AMD64, PSModulePath=C:\Program Files\WindowsPowerShell\Modules;C:\WINDOWS\system32\WindowsPowerShell\v1.0\Modules, SystemDrive=C:, OneDrive=C:\Users\dudwl\OneDrive, APPDATA=C:\Users\dudwl\AppData\Roaming, USERNAME=dudwl, ChocolateyInstall=C:\ProgramData\chocolatey, ProgramFiles(x86)=C:\Program Files (x86), VBOX_MSI_INSTALL_PATH=C:\Program Files\Oracle\VirtualBox\, CommonProgramFiles=C:\Program Files\Common Files, Path=C:\Python310\Scripts\;C:\Python310\;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\Intel\WiFi\bin\;C:\Program Files\Common Files\Intel\WirelessCommon\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\Git\cmd;C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\nodejs\;C:\ProgramData\chocolatey\bin;C:\Program Files\Java\jdk-11.0.15\bin;C:\Program Files\PuTTY\;C:\Users\dudwl\AppData\Local\Microsoft\WindowsApps;C:\Users\dudwl\AppData\Local\Programs\Microsoft VS Code\bin;C:\Users\dudwl\AppData\Roaming\npm;C:\Program Files\JetBrains\PyCharm 2022.1\bin;, FPS_BROWSER_USER_PROFILE_STRING=Default, PATHEXT=.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC;.PY;.PYW, PyCharm=C:\Program Files\JetBrains\PyCharm 2022.1\bin;, DriverData=C:\Windows\System32\Drivers\DriverData, OS=Windows_NT, COMPUTERNAME=LAPTOP-12DDBOR1, PROCESSOR_REVISION=8e0b, CommonProgramW6432=C:\Program Files\Common Files, ComSpec=C:\WINDOWS\system32\cmd.exe, ProgramData=C:\ProgramData, ProgramW6432=C:\Program Files, HOMEPATH=\Users\dudwl, SystemRoot=C:\WINDOWS, TEMP=C:\Users\dudwl\AppData\Local\Temp, HOMEDRIVE=C:, PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 142 Stepping 11, GenuineIntel, USERPROFILE=C:\Users\dudwl, TMP=C:\Users\dudwl\AppData\Local\Temp, CommonProgramFiles(x86)=C:\Program Files (x86)\Common Files, ProgramFiles=C:\Program Files, PUBLIC=C:\Users\Public, NUMBER_OF_PROCESSORS=8, windir=C:\WINDOWS, =::=::\, IDEA_INITIAL_DIRECTORY=C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\bin}}]}
key = systemProperties value={sun.desktop=windows, awt.toolkit=sun.awt.windows.WToolkit, java.specification.version=11, sun.cpu.isalist=amd64, sun.jnu.encoding=MS949, java.class.path=C:\Users\dudwl\.m2\repository\org\junit\platform\junit-platform-launcher\1.8.2\junit-platform-launcher-1.8.2.jar;C:\Users\dudwl\.m2\repository\org\junit\platform\junit-platform-engine\1.8.2\junit-platform-engine-1.8.2.jar;C:\Users\dudwl\.m2\repository\org\opentest4j\opentest4j\1.2.0\opentest4j-1.2.0.jar;C:\Users\dudwl\.m2\repository\org\junit\platform\junit-platform-commons\1.8.2\junit-platform-commons-1.8.2.jar;C:\Users\dudwl\.m2\repository\org\apiguardian\apiguardian-api\1.1.2\apiguardian-api-1.1.2.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\lib\idea_rt.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\plugins\junit\lib\junit5-rt.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\plugins\junit\lib\junit-rt.jar;C:\Users\dudwl\WorkSpace\SSS\core\out\test\classes;C:\Users\dudwl\WorkSpace\SSS\core\out\production\classes;C:\Users\dudwl\WorkSpace\SSS\core\out\production\resources;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter-test\2.7.3\ce5a11117ac6c92d38ce071ff2273799862659b7\spring-boot-starter-test-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter\2.7.3\6b0c093af667bf645cd5f49372e2a2540ae2855f\spring-boot-starter-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-test-autoconfigure\2.7.3\42924dceee5636b5c12ed36011cc333b40e1f756\spring-boot-test-autoconfigure-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-test\2.7.3\34d3fbf68aa72beef1515ab4a439c23c8c41280e\spring-boot-test-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-test\5.3.22\48375b44c82945e12012ec56dc4090c805b4508b\spring-test-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-core\5.3.22\661fc01832716c7eedebf995c6841b2f7117c63d\spring-core-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\com.jayway.jsonpath\json-path\2.7.0\f9d7d9659f2694e61142046ff8a216c047f263e8\json-path-2.7.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\jakarta.xml.bind\jakarta.xml.bind-api\2.3.3\48e3b9cfc10752fba3521d6511f4165bea951801\jakarta.xml.bind-api-2.3.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.assertj\assertj-core\3.22.0\c300c0c6a24559f35fa0bd3a5472dc1edcd0111e\assertj-core-3.22.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.hamcrest\hamcrest\2.2\1820c0968dba3a11a1b30669bb1f01978a91dedc\hamcrest-2.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter\5.8.2\5a817b1e63f1217e5c586090c45e681281f097ad\junit-jupiter-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.mockito\mockito-junit-jupiter\4.5.1\f81fb60bd69b3a6e5537ae23b883326f01632a61\mockito-junit-jupiter-4.5.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.mockito\mockito-core\4.5.1\ed456e623e5afc6f4cee3ae58144e5c45f3b3bf\mockito-core-4.5.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.skyscreamer\jsonassert\1.5.1\6d842d0faf4cf6725c509a5e5347d319ee0431c3\jsonassert-1.5.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.xmlunit\xmlunit-core\2.9.0\8959725d90eecfee28acd7110e2bb8460285d876\xmlunit-core-2.9.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-autoconfigure\2.7.3\4c96169e8d71c9c41f07a40d011dbd41898180ac\spring-boot-autoconfigure-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot\2.7.3\3a8d641077565b7eaec3b2f91d5b83a6800f5895\spring-boot-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter-logging\2.7.3\a1e4a13b656182ba10b4c0c7848f91cd6f854fdf\spring-boot-starter-logging-2.7.3.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\jakarta.annotation\jakarta.annotation-api\1.3.5\59eb84ee0d616332ff44aba065f3888cf002cd2d\jakarta.annotation-api-1.3.5.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.yaml\snakeyaml\1.30\8fde7fe2586328ac3c68db92045e1c8759125000\snakeyaml-1.30.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-jcl\5.3.22\811ace5e5eb379654ed96fd7844809db51af74a5\spring-jcl-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.minidev\json-smart\2.4.8\7c62f5f72ab05eb54d40e2abf0360a2fe9ea477f\json-smart-2.4.8.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.slf4j\slf4j-api\1.7.36\6c62681a2f655b49963a5983b8b0950a6120ae14\slf4j-api-1.7.36.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\jakarta.activation\jakarta.activation-api\1.2.2\99f53adba383cb1bf7c3862844488574b559621f\jakarta.activation-api-1.2.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter-params\5.8.2\ddeafe92fc263f895bfb73ffeca7fd56e23c2cce\junit-jupiter-params-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter-api\5.8.2\4c21029217adf07e4c0d0c5e192b6bf610c94bdc\junit-jupiter-api-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.bytebuddy\byte-buddy\1.12.13\35ffee9c24b1c68b08d9207e1a2d3da1add6166\byte-buddy-1.12.13.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.bytebuddy\byte-buddy-agent\1.12.13\5a4ed1c2eb9e8d7272b36b2b16757e5c653ab650\byte-buddy-agent-1.12.13.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\com.vaadin.external.google\android-json\0.0.20131108.vaadin1\fa26d351fe62a6a17f5cda1287c1c6110dec413f\android-json-0.0.20131108.vaadin1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-context\5.3.22\fdd59bb4795c7a399e95ec4a5c8b91103e3189fd\spring-context-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\ch.qos.logback\logback-classic\1.2.11\4741689214e9d1e8408b206506cbe76d1c6a7d60\logback-classic-1.2.11.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.apache.logging.log4j\log4j-to-slf4j\2.17.2\17dd0fae2747d9a28c67bc9534108823d2376b46\log4j-to-slf4j-2.17.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.slf4j\jul-to-slf4j\1.7.36\ed46d81cef9c412a88caef405b58f93a678ff2ca\jul-to-slf4j-1.7.36.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\net.minidev\accessors-smart\2.4.8\6e1bee5a530caba91893604d6ab41d0edcecca9a\accessors-smart-2.4.8.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.apiguardian\apiguardian-api\1.1.2\a231e0d844d2721b0fa1b238006d15c6ded6842a\apiguardian-api-1.1.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.platform\junit-platform-commons\1.8.2\32c8b8617c1342376fd5af2053da6410d8866861\junit-platform-commons-1.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.opentest4j\opentest4j\1.2.0\28c11eb91f9b6d8e200631d46e20a7f407f2a046\opentest4j-1.2.0.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-aop\5.3.22\2f9f00efbff8432f145ccffeb93e6a1819bac362\spring-aop-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-beans\5.3.22\866c2022b5fef05b1702f4a07cfa5598660ce08a\spring-beans-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.springframework\spring-expression\5.3.22\c056f9e9994b18c95deead695f9471952d1f21d1\spring-expression-5.3.22.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\ch.qos.logback\logback-core\1.2.11\a01230df5ca5c34540cdaa3ad5efb012f1f1f792\logback-core-1.2.11.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.apache.logging.log4j\log4j-api\2.17.2\f42d6afa111b4dec5d2aea0fe2197240749a4ea6\log4j-api-2.17.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.ow2.asm\asm\9.1\a99500cf6eea30535eeac6be73899d048f8d12a8\asm-9.1.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.jupiter\junit-jupiter-engine\5.8.2\c598b4328d2f397194d11df3b1648d68d7d990e3\junit-jupiter-engine-5.8.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.objenesis\objenesis\3.2\7fadf57620c8b8abdf7519533e5527367cb51f09\objenesis-3.2.jar;C:\Users\dudwl\.gradle\caches\modules-2\files-2.1\org.junit.platform\junit-platform-engine\1.8.2\b737de09f19864bd136805c84df7999a142fec29\junit-platform-engine-1.8.2.jar, java.vm.vendor=Amazon.com Inc., sun.arch.data.model=64, idea.test.cyclic.buffer.size=1048576, user.variant=, java.vendor.url=https://aws.amazon.com/corretto/, user.timezone=Asia/Seoul, os.name=Windows 10, java.vm.specification.version=11, sun.java.launcher=SUN_STANDARD, user.country=KR, sun.boot.library.path=C:\Users\dudwl\.jdks\corretto-11.0.14.1\bin, sun.java.command=com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 dev.yoon.core.beanfind.ApplicationContextExtendsFindTest,findAllBeanByObjectType, jdk.debug=release, sun.cpu.endian=little, user.home=C:\Users\dudwl, user.language=ko, java.specification.vendor=Oracle Corporation, java.version.date=2022-02-08, java.home=C:\Users\dudwl\.jdks\corretto-11.0.14.1, file.separator=\, java.vm.compressedOopsMode=32-bit, line.separator=
, java.specification.name=Java Platform API Specification, java.vm.specification.vendor=Oracle Corporation, java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment, user.script=, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, java.runtime.version=11.0.14.1+10-LTS, user.name=dudwl, path.separator=;, os.version=10.0, java.runtime.name=OpenJDK Runtime Environment, file.encoding=UTF-8, java.vm.name=OpenJDK 64-Bit Server VM, java.vendor.version=Corretto-11.0.14.10.1, java.vendor.url.bug=https://github.com/corretto/corretto-11/issues/, java.io.tmpdir=C:\Users\dudwl\AppData\Local\Temp\, java.version=11.0.14.1, user.dir=C:\Users\dudwl\WorkSpace\SSS\core, os.arch=amd64, java.vm.specification.name=Java Virtual Machine Specification, java.awt.printerjob=sun.awt.windows.WPrinterJob, sun.os.patch.level=, java.library.path=C:\Users\dudwl\.jdks\corretto-11.0.14.1\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\Python310\Scripts\;C:\Python310\;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\Intel\WiFi\bin\;C:\Program Files\Common Files\Intel\WirelessCommon\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\Git\cmd;C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\nodejs\;C:\ProgramData\chocolatey\bin;C:\Program Files\Java\jdk-11.0.15\bin;C:\Program Files\PuTTY\;C:\Users\dudwl\AppData\Local\Microsoft\WindowsApps;C:\Users\dudwl\AppData\Local\Programs\Microsoft VS Code\bin;C:\Users\dudwl\AppData\Roaming\npm;C:\Program Files\JetBrains\PyCharm 2022.1\bin;;., java.vm.info=mixed mode, java.vendor=Amazon.com Inc., java.vm.version=11.0.14.1+10-LTS, sun.io.unicode.encoding=UnicodeLittle, java.class.version=55.0}
key = systemEnvironment value={USERDOMAIN_ROAMINGPROFILE=LAPTOP-12DDBOR1, LOCALAPPDATA=C:\Users\dudwl\AppData\Local, ChocolateyLastPathUpdate=132918073329451242, PROCESSOR_LEVEL=6, USERDOMAIN=LAPTOP-12DDBOR1, FPS_BROWSER_APP_PROFILE_STRING=Internet Explorer, LOGONSERVER=\\LAPTOP-12DDBOR1, JAVA_HOME=C:\Program Files\Java\jdk-11.0.15, SESSIONNAME=Console, ALLUSERSPROFILE=C:\ProgramData, PROCESSOR_ARCHITECTURE=AMD64, PSModulePath=C:\Program Files\WindowsPowerShell\Modules;C:\WINDOWS\system32\WindowsPowerShell\v1.0\Modules, SystemDrive=C:, OneDrive=C:\Users\dudwl\OneDrive, APPDATA=C:\Users\dudwl\AppData\Roaming, USERNAME=dudwl, ChocolateyInstall=C:\ProgramData\chocolatey, ProgramFiles(x86)=C:\Program Files (x86), VBOX_MSI_INSTALL_PATH=C:\Program Files\Oracle\VirtualBox\, CommonProgramFiles=C:\Program Files\Common Files, Path=C:\Python310\Scripts\;C:\Python310\;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\Intel\WiFi\bin\;C:\Program Files\Common Files\Intel\WirelessCommon\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\Git\cmd;C:\Program Files\Docker\Docker\resources\bin;C:\ProgramData\DockerDesktop\version-bin;C:\Program Files\nodejs\;C:\ProgramData\chocolatey\bin;C:\Program Files\Java\jdk-11.0.15\bin;C:\Program Files\PuTTY\;C:\Users\dudwl\AppData\Local\Microsoft\WindowsApps;C:\Users\dudwl\AppData\Local\Programs\Microsoft VS Code\bin;C:\Users\dudwl\AppData\Roaming\npm;C:\Program Files\JetBrains\PyCharm 2022.1\bin;, FPS_BROWSER_USER_PROFILE_STRING=Default, PATHEXT=.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC;.PY;.PYW, PyCharm=C:\Program Files\JetBrains\PyCharm 2022.1\bin;, DriverData=C:\Windows\System32\Drivers\DriverData, OS=Windows_NT, COMPUTERNAME=LAPTOP-12DDBOR1, PROCESSOR_REVISION=8e0b, CommonProgramW6432=C:\Program Files\Common Files, ComSpec=C:\WINDOWS\system32\cmd.exe, ProgramData=C:\ProgramData, ProgramW6432=C:\Program Files, HOMEPATH=\Users\dudwl, SystemRoot=C:\WINDOWS, TEMP=C:\Users\dudwl\AppData\Local\Temp, HOMEDRIVE=C:, PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 142 Stepping 11, GenuineIntel, USERPROFILE=C:\Users\dudwl, TMP=C:\Users\dudwl\AppData\Local\Temp, CommonProgramFiles(x86)=C:\Program Files (x86)\Common Files, ProgramFiles=C:\Program Files, PUBLIC=C:\Users\Public, NUMBER_OF_PROCESSORS=8, windir=C:\WINDOWS, =::=::\, IDEA_INITIAL_DIRECTORY=C:\Program Files\JetBrains\IntelliJ IDEA 2021.3.2\bin}
key = applicationStartup value=org.springframework.core.metrics.DefaultApplicationStartup@2f4205be
key = org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry value=[]
key = messageSource value=Empty MessageSource
key = applicationEventMulticaster value=org.springframework.context.event.SimpleApplicationEventMulticaster@54e22bdd
key = lifecycleProcessor value=org.springframework.context.support.DefaultLifecycleProcessor@3bd418e4

```


### BeanFactory와 ApplicationContext

![image](https://user-images.githubusercontent.com/83503188/191019130-50c9e17a-bd19-4342-ae86-957904f310b2.png)


**BeanFactory**

- 스프링 컨테이너의 최상위 인터페이스다.
- 스프링 빈을 관리하고 조회하는 역할을 담당한다.
- `getBean()` 을 제공한다.
- 지금까지 우리가 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능이다.

**ApplicationContext**

- BeanFactory 기능을 모두 상속받아서 제공한다.
- 빈을 관리하고 검색하는 기능을 BeanFactory가 제공해주는데, 그러면 둘의 차이가 뭘까?
- 애플리케이션을 개발할 때는 빈을 관리하고 조회하는 기능은 물론이고, 수 많은 부가기능이 필요하다.


**ApplicatonContext가 제공하는 부가기능**

![image](https://user-images.githubusercontent.com/83503188/191019637-340964f4-fe2a-49c9-bd13-2cb61a57cae6.png)

![image](https://user-images.githubusercontent.com/83503188/191020400-0b0b7167-a500-4f5a-950a-97170bdbb9b7.png)


- 메시지소스를 활용한 국제화 기능
  - 예를 들어서 한국에서 들어오면 한국어로, 영어권에서 들어오면 영어로 출력
- 환경변수
  - 로컬, 개발, 운영등을 구분해서 처리
- 애플리케이션 이벤트
  - 이벤트를 발행하고 구독하는 모델을 편리하게 지원
- 편리한 리소스 조회
  - 파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회



**정리**
- ApplicationContext는 BeanFactory의 기능을 상속받는다.
- ApplicationContext는 빈 관리기능 + 편리한 부가 기능을 제공한다.
- BeanFactory를 직접 사용할 일은 거의 없다. 부가기능이 포함된 ApplicationContext를 사용한다.
- BeanFactory나 ApplicationContext를 스프링 컨테이너라 한다.

### 다양한 설정 형식 지원 - 자바 코드, XML

- 스프링 컨테이너는 다양한 형식의 설정 정보를 받아드릴 수 있게 유연하게 설계되어 있다.
  - 자바 코드, XML, Groovy 등등


![image](https://user-images.githubusercontent.com/83503188/191021021-7dd82e34-3481-4b6f-9f8f-abbae04e955e.png)

**애노테이션 기반 자바 코드 설정 사용**

- 지금까지 했던 것이다.
- `new AnnotationConfigApplicationContext(AppConfig.class)`
- `AnnotationConfigApplicationContext` 클래스를 사용하면서 자바 코드로된 설정 정보를 넘기면 된다.

**XML 설정 사용**

- 최근에는 스프링 부트를 많이 사용하면서 XML기반의 설정은 잘 사용하지 않는다. 아직 많은 레거시 프로젝트 들이 XML로 되어 있고, 또 XML을 사용하면 컴파일 없이 빈 설정 정보를 변경할 수 있는 장점도
  있으므로 한번쯤 배워두는 것도 괜찮다.
- `GenericXmlApplicationContext` 를 사용하면서 xml 설정 파일을 넘기면 된다.



**XmlAppConfig 사용 자바 코드**

```java
public class XmlAppContext {

    @Test
    void xmlAppContext() {
        ApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberService.class);
    }

}
```

**xml 기반의 스프링 빈 설정 정보**

`src/main/resources/appConfig.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="memberService" class="dev.yoon.core.member.MemberServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository"/>
    </bean>

    <bean id="memberRepository" class="dev.yoon.core.member.MemoryMemberRepository"/>

    <bean id="orderService" class="dev.yoon.core.order.OrderServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository"/>
        <constructor-arg name="discountPolicy" ref="discountPolicy"/>
    </bean>
    
    <bean id="discountPolicy" class="dev.yoon.core.discount.RateDiscountPolicy"/>

</beans>
```


- xml 기반의 `appConfig.xml` 스프링 설정 정보와 자바 코드로 된 `AppConfig.java` 설정 정보를 비교해보면 거의 비슷하다는 것을 알 수 있다.
- xml 기반으로 설정하는 것은 최근에 잘 사용하지 않는다.

### 스프링 빈 설정 메타 정보 - BeanDefinition

- 스프링은 어떻게 이런 다양한 설정 형식을 지원하는 것일까? 그 중심에는 `BeanDefinition` 이라는 추상화가 있다.
- 쉽게 이야기해서 역할과 구현을 개념적으로 나눈 것이다!
  - XML을 읽어서 `BeanDefinition`을 만들면 된다.
  - 자바 코드를 읽어서 `BeanDefinition`을 만들면 된다.
  - 스프링 컨테이너는 자바 코드인지, XML인지 몰라도 된다. 오직 `BeanDefinition만` 알면 된다.
- `BeanDefinition` 을 빈 설정 메타정보라 한다.
  - `@Bean` , `<bean>` 당 각각 하나씩 메타 정보가 생성된다.
- 스프링 컨테이너는 이 메타정보를 기반으로 스프링 빈을 생성한다.


![image](https://user-images.githubusercontent.com/83503188/191022544-86be5342-7f15-413a-ab06-8d5cb691e760.png)

코드 레벨로 조금 더 깊이 있게 들어가보자.

![image](https://user-images.githubusercontent.com/83503188/191022613-67d39e25-96c5-448c-9dcf-03f222de4eb1.png)

- `AnnotationConfigApplicationContext` 는 `AnnotatedBeanDefinitionReader` 를 사용해서 `AppConfig.class` 를 읽고 `BeanDefinition` 을 생성한다.
- `GenericXmlApplicationContext` 는 `XmlBeanDefinitionReader` 를 사용해서 `appConfig.xml` 설정 정보를 읽고 `BeanDefinition` 을 생성한다.
- 새로운 형식의 설정 정보가 추가되면, XxxBeanDefinitionReader를 만들어서 `BeanDefinition` 을 생성하면 된다.

**BeanDefinition 정보**

- BeanClassName: 생성할 빈의 클래스 명(자바 설정 처럼 팩토리 역할의 빈을 사용하면 없음)
- factoryBeanName: 팩토리 역할의 빈을 사용할 경우 이름, 예) appConfig
- factoryMethodName: 빈을 생성할 팩토리 메서드 지정, 예) memberService
- Scope: 싱글톤(기본값)
- lazyInit: 스프링 컨테이너를 생성할 때 빈을 생성하는 것이 아니라, 실제 빈을 사용할 때 까지 최대한 생성을 지연처리 하는지 여부
- InitMethodName: 빈을 생성하고, 의존관계를 적용한 뒤에 호출되는 초기화 메서드 명
- DestroyMethodName: 빈의 생명주기가 끝나서 제거하기 직전에 호출되는 메서드 명
- Constructor arguments, Properties: 의존관계 주입에서 사용한다. (자바 설정 처럼 팩토리 역할의 빈을 사용하면 없음)

```java
public class BeanDefinitionTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("빈 설정 메타정보 확인")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                System.out.println("beanDefinitionName = " + beanDefinitionName + " beanDefinition = " + beanDefinition);
            }
        }

    }
}
```

```text

beanDefinitionName = appConfig beanDefinition = Generic bean: class [dev.yoon.core.AppConfig$$EnhancerBySpringCGLIB$$cff445fa]; scope=singleton; abstract=false; lazyInit=null; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null
beanDefinitionName = memberService beanDefinition = Root bean: class [null]; scope=; abstract=false; lazyInit=null; autowireMode=3; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=appConfig; factoryMethodName=memberService; initMethodName=null; destroyMethodName=(inferred); defined in dev.yoon.core.AppConfig
beanDefinitionName = orderService beanDefinition = Root bean: class [null]; scope=; abstract=false; lazyInit=null; autowireMode=3; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=appConfig; factoryMethodName=orderService; initMethodName=null; destroyMethodName=(inferred); defined in dev.yoon.core.AppConfig
beanDefinitionName = memberRepository beanDefinition = Root bean: class [null]; scope=; abstract=false; lazyInit=null; autowireMode=3; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=appConfig; factoryMethodName=memberRepository; initMethodName=null; destroyMethodName=(inferred); defined in dev.yoon.core.AppConfig
beanDefinitionName = discountPolicy beanDefinition = Root bean: class [null]; scope=; abstract=false; lazyInit=null; autowireMode=3; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=appConfig; factoryMethodName=discountPolicy; initMethodName=null; destroyMethodName=(inferred); defined in dev.yoon.core.AppConfig

```

**정리**

- BeanDefinition을 직접 생성해서 스프링 컨테이너에 등록할 수 도 있다. 하지만 실무에서 BeanDefinition을 직접 정의하거나 사용할 일은 거의 없다. 
- BeanDefinition에 대해서는 너무 깊이있게 이해하기 보다는, 스프링이 다양한 형태의 설정 정보를 BeanDefinition으로 추상화해서 사용하는 것 정도만 이해하면 된다.
- 가끔 스프링 코드나 스프링 관련 오픈 소스의 코드를 볼 때, BeanDefinition 이라는 것이 보일 때가 있다. 이때 이러한 메커니즘을 떠올리면 된다.

