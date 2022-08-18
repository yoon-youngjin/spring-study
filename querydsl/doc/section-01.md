# 프로젝트 환경설정

## Querydsl 설정

```yaml
buildscript {
    ext {
        queryDslVersion = "5.0.0"
    }
}

plugins {
    id 'org.springframework.boot' version '2.7.2'
    id 'io.spring.dependency-management' version '1.0.12.RELEASE'
    //querydsl 추가
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"

    id 'java'
}

group = 'dev.yoon'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'

    //querydsl 추가
    implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"
    implementation "com.querydsl:querydsl-apt:${queryDslVersion}"

    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}

//querydsl 추가 시작
def querydslDir = "$buildDir/generated/querydsl"
querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}
sourceSets {
    main.java.srcDir querydslDir
}
compileQuerydsl{
    options.annotationProcessorPath = configurations.querydsl
}
configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
    querydsl.extendsFrom compileClasspath
}
//querydsl 추가 끝
```

Q파일을 생성하기 위해서는 

1. 우측 gradle에서 other -> compileQuerydsl 
2. 터미널 ./gradlew complieQuerydsl

---

## Test에 Transactional 있는 경우 기본적으로 모두 Rollback 처리

Rollback을 막기 위해서는 `@Commit` 어노테이션을 사용해주면 된다.

## Sql의 value를 보기 위한 방법

### 1. logging.level

```yaml
logging.level:
  org.hibernate.SQL: debug
  org.hibernate.type: trace
```

![image](https://user-images.githubusercontent.com/83503188/185354533-4ab5fc7a-d63e-4b08-b51e-38b3a5f922df.png)


### 2. 외부 라이브러리를 추가

```text
implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.8'
```

![image](https://user-images.githubusercontent.com/83503188/185354691-b5f0f731-b6c7-40b8-8f2a-90d58c241e38.png)
