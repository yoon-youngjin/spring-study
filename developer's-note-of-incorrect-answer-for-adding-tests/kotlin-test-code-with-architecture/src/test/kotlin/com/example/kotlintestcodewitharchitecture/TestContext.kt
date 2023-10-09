package com.example.kotlintestcodewitharchitecture

import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.jvm.isAccessible

class TestContext(
    private val constructorByType: Map<KClass<out Any>, TestContext.() -> Any?> = mapOf(),
    private val constructorByName: Map<String, TestContext.() -> Any?> = mapOf(),
    instances: List<Any> = emptyList(),
    instanceByName: Map<String, Any> = mapOf(),
    private val implTypes: List<Class<out Any>> = emptyList(),
    private val scanBasePackages: List<String> = emptyList(),
) {

    private val instanceByName: MutableMap<String, Any> = instanceByName.toMutableMap()

    companion object {
        private val typesByBasePackage: MutableMap<String, List<Class<out Any>>> = mutableMapOf()

        private fun scanImplTypes(basePackage: String): List<Class<out Any>> {
            return typesByBasePackage.getOrPut(basePackage) {
                getAllClassesInPackage(basePackage).filter { it.constructors.isNotEmpty() }
            }
        }
    }

    private fun getImplTypeOrNull(type: KClass<out Any>): KClass<out Any>? {
        val scannedImplTypes = (scanBasePackages.flatMap { scanImplTypes(it) })
        return (implTypes + scannedImplTypes).firstOrNull {
            type.javaObjectType.isAssignableFrom(it)
        }?.kotlin
    }

    private fun <T : Any> dummy(type: KClass<out T>): T? {
        return try {
            DummyFactory.default.create(type)
        } catch (e: UnsupportedOperationException) {
            null
        }
    }

    inline fun <reified T : Any> ref(name: String? = null): T = ref(T::class, name)

    fun <T : Any> ref(type: KType, name: String?): T {
        val classifier = type.classifier as KClass<*>
        return if (type.arguments.isNotEmpty() &&
            Collection::class.java.isAssignableFrom(classifier.javaObjectType) &&
            instanceByName.contains(name).not() &&
            constructorByName.contains(name).not()
        ) {
            val elementClassifier = type.arguments[0].type!!.classifier as KClass<*>
            val result = refAll(elementClassifier, name)
            if (classifier == Set::class) {
                result.toSet()
            } else {
                result
            }
        } else {
            ref(classifier, name)
        } as T
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun <T : Any> refAll(kClass: KClass<T>, name: String?): List<T> = try {
        findAll(kClass, name).ifEmpty {
            try {
                listOf(create(kClass, name))
            } catch (e: ConstructorRequiredException) {
                emptyList()
            }
        } as List<T>
    } catch (e: Throwable) {
        throw IllegalArgumentException("Failed to get instances of $kClass", e)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun <T : Any> ref(kClass: KClass<T>, name: String?): T = try {
        findOrNull(kClass, name) ?: create(kClass, name)
    } catch (e: Throwable) {
        throw IllegalArgumentException("Failed to get an instance of $kClass", e)
    } as T

    private fun <T : Any> create(kClass: KClass<T>, name: String?): Any {
        if (name != null) {
            constructorByName[name]
                ?.invoke(this)
                ?.also { instanceByName[name] = it }
                ?.takeIf { kClass.isAssignableFrom(it) }
                ?.let { return it }
        }

        return (
            constructorByType[kClass]?.invoke(this) ?: when {
                kClass.java.isEnum -> kClass.java.enumConstants.first()
                else -> createFromClass(kClass)
            }
            ).also {
                instanceByType[it::class] = it
            }
    }

    private fun <T : Any> findOrNull(kClass: KClass<T>, name: String?) =
        name?.let { instanceByName[name]?.takeIf { kClass.isAssignableFrom(it) } }
            ?: instanceByType[kClass]
            ?: instanceByType.values.firstOrNull { kClass.isAssignableFrom(it) }

    private fun <T : Any> findAll(kClass: KClass<T>, name: String?): List<Any> {
        val single = (
            name?.let { instanceByName[name]?.takeIf { kClass.isAssignableFrom(it) } }
                ?: instanceByType[kClass]
            )

        return if (single != null) {
            listOf(single)
        } else {
            instanceByType.values.filter { kClass.isAssignableFrom(it) }
        }
    }

    private fun <T : Any> KClass<T>.isAssignableFrom(other: Any) =
        javaObjectType.isAssignableFrom(other::class.javaObjectType)

    private fun <T : Any> createFromClass(kClass: KClass<T>): T {
        val constructor = getBestConstructor(kClass)
            ?: return dummy(kClass)
                ?: throw ConstructorRequiredException("Constructor required: ${kClass.qualifiedName}")

        val args = resolveDependency(constructor)
        constructor.isAccessible = true
        return constructor.callBy(args)
    }

    private fun <T : Any> resolveDependency(constructor: KFunction<T>): Map<KParameter, Any?> {
        val params = constructor.parameters
        val args = params.mapNotNull { param ->
            val value =
                kotlin.runCatching {
                    when {
                        param.type.isMarkedNullable -> null
                        param.type.classifier is KClass<*> -> ref(param.type, param.name) as T
                        else -> null
                    }
                }.onFailure {
                    if (!param.isOptional) throw it
                }.getOrNull()
            // optional 이라도 이미 정의된 value가 있다면 그것이 우선이다.
            if (!param.isOptional || value != null) Pair(param, value) else null
        }.toMap()
        return args
    }

    private fun <T : Any> getBestConstructor(kClass: KClass<T>): KFunction<T>? =
        when {
            kClass.isAbstract -> getDefaultImplType(kClass)?.let { getBestConstructor(it) } as KFunction<T>?
            else -> kClass.constructors.minByOrNull { it.parameters.size }
        }

    private fun getDefaultImplType(kClass: KClass<out Any>): KClass<out Any>? {
        return getImplTypeOrNull(kClass) ?: getDefaultInnerClass(kClass)
    }

    private fun <T : Any> getDefaultInnerClass(kClass: KClass<T>) =
        kClass.nestedClasses.firstOrNull { kClass.javaObjectType.isAssignableFrom(it.javaObjectType) }

    private val instanceByType = instances.associateBy { it::class }.toMutableMap()
}

class ConstructorRequiredException(message: String) : RuntimeException(message)
