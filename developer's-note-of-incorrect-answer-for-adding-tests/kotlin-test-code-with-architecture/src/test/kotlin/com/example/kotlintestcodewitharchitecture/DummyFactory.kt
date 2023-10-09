package com.example.kotlintestcodewitharchitecture

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

class DummyFactory {

    private val dummyMakers = mutableListOf<MakeDummy>()

    private val dummies = mutableMapOf<KClass<*>, Any>()

    /**
     * 더미를 생성하는 함수를 등록한다.
     *
     * 나중에 등록한 것이 우선순위가 높다.
     */
    fun registerDummyMaker(makeDummy: MakeDummy): DummyFactory {
        dummyMakers.add(0, makeDummy)
        return this
    }

    /**
     * 더미를 등록한다.
     *
     * [registerDummyMaker]로 등록된 더미 생성 함수보다 우선순위가 높다.
     */
    fun register(dummy: Any): DummyFactory {
        register(dummy::class, dummy)
        return this
    }

    /**
     * 더미를 등록한다.
     *
     * [registerDummyMaker]로 등록된 더미 생성 함수보다 우선순위가 높다.
     */
    fun register(type: KClass<*>, dummy: Any): DummyFactory {
        dummies[type] = dummy
        return this
    }

    /**
     * 등록한 더미와 더미 생성 함수를 모두 삭제한다.
     *
     * built-in으로 정의된 더미는 삭제되지 않는다.
     */
    fun unregisterAll(): DummyFactory {
        dummyMakers.clear()
        dummies.clear()
        return this
    }

    inline fun <reified T : Any> create(): T = create(T::class)

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun <T : Any> create(kClass: KClass<T>): T {
        dummies[kClass]?.let { return it as T }
        dummyMakers.firstNotNullOfOrNull { it.invoke(kClass) }?.let { return it as T }

        return when (kClass) {
            // Kotlin Types
            Unit::class -> Unit
            String::class -> ""
            Boolean::class -> false
            LocalDate::class -> theFirstDay
            LocalDateTime::class -> theFirstDay.atStartOfDay()
            Period::class -> Period.ofDays(1)
            Long::class -> 0L
            Int::class -> 0
            Float::class -> 0f
            Double::class -> 0f
            Char::class -> '0'
            Byte::class -> 0
            BigInteger::class -> BigInteger.ZERO
            BigDecimal::class -> BigDecimal.ZERO
            ZoneId::class -> ZoneId.systemDefault()

            // Collections
            MutableList::class -> mutableListOf<Any>()
            MutableSet::class -> mutableSetOf<Any>()
            MutableMap::class -> mutableMapOf<Any, Any>()
            MutableCollection::class -> mutableListOf<Any>()
            MutableIterator::class,
            MutableIterable::class,
            -> mutableListOf<Any>().iterator()
            List::class -> emptyList<Any>()
            Set::class -> emptySet<Any>()
            Map::class -> emptyMap<Any, Any>()
            Collection::class -> emptyList<Any>()
            Iterator::class,
            Iterable::class,
            -> emptyList<Any>().iterator()

            // enums and unsupported type
            else ->
                when {
                    kClass.java.isEnum ->
                        kClass.java.enumConstants.first()
                    kClass.isAbstract ->
                        throw UnsupportedOperationException("Unsupported type: ${kClass.qualifiedName}")
                    else -> {
                        val constructor = kClass.constructors.minByOrNull { it.parameters.size }
                            ?: throw UnsupportedOperationException("Constructor required: ${kClass.qualifiedName}")
                        val params = constructor.parameters
                        val args = params.filter { !it.isOptional }.associateWith {
                            when {
                                it.type.isMarkedNullable -> null
                                it.type.classifier is KClass<*> -> create(it.type.classifier as KClass<*>)
                                else -> null
                            }
                        }
                        constructor.isAccessible = true
                        constructor.callBy(args)
                    }
                }
        } as T
    }

    private val theFirstDay = LocalDate.of(1, 1, 1)

    companion object {
        val default = DummyFactory()
    }
}

/**
 * [Dummy.create]의 shortcut 함수.
 */
inline fun <reified T : Any> dummy(): T = DummyFactory.default.create()

/**
 * 더미를 반환한다.
 *
 * 지원하지 않는 type의 경우 null을 반환한다.
 */
typealias MakeDummy = (KClass<*>) -> Any?
