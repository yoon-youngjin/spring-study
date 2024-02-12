package hello.aop.member.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodAop(
    val value: String,
)