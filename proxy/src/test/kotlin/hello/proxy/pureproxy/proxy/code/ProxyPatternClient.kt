package hello.proxy.pureproxy.proxy.code

class ProxyPatternClient(
    private val subject: Subject,
) {
    fun execute() {
        subject.operation()
    }
}
