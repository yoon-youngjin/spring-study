package hello.proxy.config.v5_autoproxy

import hello.proxy.config.AppV1Config
import hello.proxy.config.AppV2Config
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice
import hello.proxy.trace.logtrace.LogTrace
import org.springframework.aop.Advisor
import org.springframework.aop.aspectj.AspectJExpressionPointcut
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.NameMatchMethodPointcut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(AppV1Config::class, AppV2Config::class)
class AutoProxyConfig {
    //    @Bean
    fun getAdvisor1(logTrace: LogTrace): Advisor {
        // pointcut
        val pointcut = NameMatchMethodPointcut()
        pointcut.setMappedNames("request*", "order*", "save*")
        // advice
        val advice = LogTraceAdvice(logTrace)
        // advisor = pointcut + advice
        return DefaultPointcutAdvisor(pointcut, advice)
    }

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