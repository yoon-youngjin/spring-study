package dev.yoon.jpa.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
// Target : @interface가 어떠한 것에 붙을 수 있는지를 정의
// -> ElementType.METHOD : 함수에 붙을 수 있음
@Retention(RetentionPolicy.RUNTIME)
// Retention : 해당 어노테이션이 실제 기능과 연관되어 있을 수도 있고, 컴파일과정에 필요할 수도 있고,
// 표기용도일 수도 있음, 어느 시점까지 컴퓨터에 존재할 지를 정의
// RetentionPolicy.SOURCE : 소스코드에 작성하기 위한 용도, 컴파일시 사라짐
// RetentionPolicy.CLASS : 컴파일시에는 있지만, 런타임에 사라짐
// RetentionPolicy.RUNTIME : 런타임까지 존재
public @interface LogExecutionTime {

}
