@file:OptIn(InternalAPI::class)

package com.osucad.server.plugins

import io.ktor.events.EventDefinition
import io.ktor.server.application.*
import io.ktor.util.*
import io.ktor.util.collections.*
import io.ktor.util.internal.*
import io.ktor.utils.io.*
import kotlinx.coroutines.DisposableHandle

val EventBusKey = AttributeKey<EventBus>("EventBus")

val Application.eventBus: EventBus
    get() {
        return attributes.computeIfAbsent(EventBusKey) { EventBus() }
    }

class EventBus {
    @OptIn(InternalAPI::class)
    private val handlers = CopyOnWriteHashMap<EventDefinition<*>, LockFreeLinkedListHead>()


    suspend fun <T> publish(
        definition: EventDefinition<T>,
        value: T
    ) {
        var exception: Throwable? = null
        handlers[definition]?.forEach<HandlerRegistration> { registration ->
            try {
                @Suppress("UNCHECKED_CAST")
                (registration.handler as EventHandler<T>)(value)
            } catch (e: Throwable) {
                exception?.addSuppressed(e) ?: run { exception = e }
            }
        }
        exception?.let { throw it }
    }

    fun <T> subscribe(
        definition: EventDefinition<T>,
        handler: EventHandler<T>
    ): DisposableHandle {
        val registration = HandlerRegistration(handler)
        handlers.computeIfAbsent(definition) { LockFreeLinkedListHead() }.addLast(registration)

        return registration
    }

    private class HandlerRegistration(val handler: EventHandler<*>) : LockFreeLinkedListNode(), DisposableHandle {
        override fun dispose() {
            remove()
        }
    }
}

typealias EventHandler<T> = suspend (T) -> Unit
