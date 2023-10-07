package com.example.kotlintestcodewitharchitecture

import com.example.kotlintestcodewitharchitecture.mock.FakeMailSender
import com.example.kotlintestcodewitharchitecture.mock.FakePostRepository
import com.example.kotlintestcodewitharchitecture.mock.FakeUserRepository
import com.example.kotlintestcodewitharchitecture.mock.StubClockHolder
import com.example.kotlintestcodewitharchitecture.mock.StubUuidHolder
import com.example.kotlintestcodewitharchitecture.post.controller.PostController
import com.example.kotlintestcodewitharchitecture.post.controller.PostCreateController
import com.example.kotlintestcodewitharchitecture.post.service.PostServiceImpl
import com.example.kotlintestcodewitharchitecture.user.controller.MyInfoController
import com.example.kotlintestcodewitharchitecture.user.controller.UserController
import com.example.kotlintestcodewitharchitecture.user.controller.UserCreateController
import com.example.kotlintestcodewitharchitecture.user.service.CertificationService
import com.example.kotlintestcodewitharchitecture.user.service.UserServiceImpl
import java.time.Clock
import java.util.UUID

class StandaloneTestContext {
    val userRepository = FakeUserRepository()
    val postRepository = FakePostRepository()

    private val mailSender = FakeMailSender()
    private val certificationService = CertificationService(mailSender)
    val uuidHolder = StubUuidHolder(UUID.randomUUID().toString())
    val clockHolder = StubClockHolder(Clock.systemUTC().millis())

    val userService = UserServiceImpl(
        userRepository = userRepository,
        certificationService = certificationService,
        uuidHolder = uuidHolder,
        clockHolder = clockHolder,
    )

    val postService = PostServiceImpl(
        postRepository = postRepository,
        userRepository = userRepository,
        clockHolder = clockHolder,
    )

    val userController = UserController(
        userService = userService,
    )
    val userCreateController = UserCreateController(
        userService = userService,
    )
    val postController = PostController(
        postService = postService,
    )
    val postCreateController = PostCreateController(
        postService = postService,
    )
    val myInfoController = MyInfoController(
        userService = userService,
    )
}
