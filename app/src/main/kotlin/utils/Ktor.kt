package com.osucad.server.utils


import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

fun <T> T?.orNotFound(): T = this ?: throw NotFoundException()

context(ctx: RoutingContext)
suspend inline fun <reified T> T.sendAsResponse() {
    ctx.call.respond(this, typeInfo<T>())
}
