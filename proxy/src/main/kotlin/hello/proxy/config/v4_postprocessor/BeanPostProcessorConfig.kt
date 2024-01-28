package hello.proxy.config.v4_postprocessor

import hello.proxy.config.AppV1Config
import hello.proxy.config.AppV2Config
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice
import hello.proxy.config.v4_postprocessor.postprocessor.PackageLogTracePostProcessor
import hello.proxy.trace.logtrace.LogTrace
import org.springframework.aop.Advisor
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.NameMatchMethodPointcut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

//@Configuration
//@Import(AppV1Config::class, AppV2Config::class)
class BeanPostProcessorConfig {
//    @Bean
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