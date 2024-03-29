package com.example.kotlintestcodewitharchitecture

import com.example.kotlintestcodewitharchitecture.mock.FakeMailSender
import com.example.kotlintestcodewitharchitecture.mock.FakePostRepository
import com.example.kotlintestcodewitharchitecture.mock.FakeUserRepository
import com.example.kotlintestcodewitharchitecture.mock.StubClockHolder
import com.example.kotlintestcodewitharchitecture.mock.StubUuidHolder
import com.example.kotlintestcodewitharchitecture.post.controller.port.PostServiceImpl
import com.example.kotlintestcodewitharchitecture.user.service.UserServiceImpl
import kotlin.reflect.KClass

class StandaloneTestContext(
    namedInstances: Map<String, Any> = emptyMap(),
    instances: List<Any> = emptyList(),
    implTypes: List<Class<out Any>> = emptyList(),
    constructorByType: Map<KClass<out Any>, TestContext.() -> Any?> = mapOf(),
) {

    val testContext = TestContext(
        constructorByType = constructorByType,
        instances = instances,
        instanceByName = namedInstances,
        implTypes = defaultImplTypes + implTypes,
//        scanBasePackages = listOf("com.example.kotlintestcodewitharchitecture")
    )
    inline fun <reified T : Any> ref(): T = testContext.ref()
}

private val defaultImplTypes = listOf(
    // repositories
    FakeUserRepository::class,
    FakePostRepository::class,

    // clients
    FakeMailSender::class,

    // services
    StubUuidHolder::class,
    StubClockHolder::class,
    UserServiceImpl::class,
    PostServiceImpl::class,

).map { it.javaObjectType }
