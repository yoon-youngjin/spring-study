package hello.aop.order.aop

import org.aspectj.lang.annotation.Pointcut

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