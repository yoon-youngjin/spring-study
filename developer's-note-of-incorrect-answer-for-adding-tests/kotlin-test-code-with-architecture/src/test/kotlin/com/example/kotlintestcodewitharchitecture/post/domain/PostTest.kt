package com.example.kotlintestcodewitharchitecture.post.domain

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.mock.StubClockHolder
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.time.Clock
import org.junit.jupiter.api.Test

class PostTest {

    private val standaloneTestContext = StandaloneTestContext()

    private val clockHolder = standaloneTestContext.ref<ClockHolder>() as StubClockHolder
    @Test
    fun `Post는 PostCreate로 데이터을 만들 수 있다`() {
        // given
        val postCreate = random<PostCreate>()
        val user = random<User>()
        clockHolder.setUp(Clock.systemUTC().millis())

        // when
        val result = Post.from(user, postCreate, clockHolder)

        // then
        assertSoftly(result) {
            it.content shouldBe postCreate.content
            it.writer shouldBe user
            it.createdAt shouldBe clockHolder.millis()
        }
    }

    @Test
    fun `Post는 PostUpdate로 데이터을 업데이트 할 수 있다`() {
        // given
        val post = random<Post>()
        val postUpdate = random<PostUpdate>()
        clockHolder.setUp(Clock.systemUTC().millis())

        // when
        val result = post.update(postUpdate, clockHolder)

        // then
        assertSoftly(result) {
            it.content shouldBe postUpdate.content
            it.writer shouldBe post.writer
            it.modifiedAt shouldBe clockHolder.millis()
        }
    }
}