package dev.yoon.jpa.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
// 하나의 객체에서 하나의 역할을 하는 것이 좋음
// 하나의 advice를 하나의 객체에서 정의하는 것이 좋음
public class LoggingAspect {

    // PointCut : 문자열로 표현 가능
    @Around(value = "@annotation(LogExecutionTime)")
    // ProceedingJoinPoint : JoinPoint(프로그램의 한시점), ProceedingJoinPoint는
    // Advice
    public Object logExcutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        /**
         * joinPoint.proceed() 매서드를 실행시, 대상 객체의 매서드가 실행되므로 이 코드 전후로 공통 기능을 위한 코드를 위치시킴
         */
        Object proceed = joinPoint.proceed();
        long execTime = System.currentTimeMillis() - startTime;
        log.trace("method executed in {}", execTime);
        return proceed;
    }

    // Pointcut
    @Before(value = "@annotation(LogArguments)")
    // 해당 어노테이션이 붙은 함수가 실행되기 전에 잠깐 수행하는 함수
    // 특정 시점이전에 일어난 일만 확인하고 지나가기 때문에 반환값이 필요없다.

    public void logParameter(JoinPoint joinPoint) {
        // 오류 : "시그니처가 맞지 않습니다" -> 함수의 인자나 반환값이 기대한 것과 다른 경우 발생
        // MethodSignature : 함수가 가진 모양
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        log.trace("method description: [{}]", methodSignature.getMethod());
        log.trace("method name: {}", methodSignature.getName());
        log.trace("declaring class: [{}]", methodSignature.getDeclaringType());

        Object[] arguments = joinPoint.getArgs();
        if (arguments.length == 0) {
            log.trace("no arguments");
        }
        for (Object argument : arguments) {
            log.trace("argument:[{}]", argument);
        }
    }

    @AfterReturning(value = "@annotation(LogReturn)",returning = "returnValue")
    // AfterReturning : 반환값이 존재하는 경우에 pointcut을 지정
    public void logResults(JoinPoint joinPoint, Object returnValue) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.trace("method name: {}", methodSignature.getName());
        log.trace("return type: [{}]", methodSignature.getReturnType());
        log.trace("return value: [{}]", returnValue);

    }

}
