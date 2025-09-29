package com.osucad.server.utils


import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.uuid.Uuid

fun <T> T?.orNotFound(): T = this ?: throw NotFoundException()

context(ctx: RoutingContext)
suspend inline fun <reified T> T.sendAsResponse() {
    ctx.call.respond(this, typeInfo<T>())
}

inline fun <reified R : Any> Parameters.getOrNull(name: String): R? {
    return try {
        getOrFail<R>(name)
    } catch (_: MissingRequestParameterException) {
        null
    }
}


context(ctx: RoutingContext)
inline fun <reified T : Any> queryParameter(
    name: String? = null,
): Parameter<T> {
    return QueryParameter(
        source = { name -> ctx.call.queryParameters.getOrFail<T>(name) },
        name = name,
    )
}

interface Parameter<out T> : ReadOnlyProperty<Any?, T> {
    val name: String?

    fun validate(
        message: String,
        check: (T) -> Boolean,
    ): Parameter<T> = object : Parameter<T> {
        override val name = this@Parameter.name

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val value = this@Parameter.getValue(thisRef, property)

            if (!check(value))
                throw BadRequestException("Invalid request parameter ${name ?: property.name}: $message")

            return value
        }
    }
}

class QueryParameter<out T>(
    private val source: (name: String) -> T,
    override val name: String?,
) : Parameter<T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.source(name ?: property.name)
    }
}

fun <T> Parameter<List<T>>.maxLength(length: Int) = validate("Exceeded max length ($length)") { it.size <= length }

