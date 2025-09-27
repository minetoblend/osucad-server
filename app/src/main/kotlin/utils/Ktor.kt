package com.osucad.server.utils

import com.osucad.server.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

fun <T> T?.orNotFound(): T = this ?: throw NotFoundException()

val ApplicationCall.user get() = principal<UserPrincipal>().expectNotNull("No active session")

val ApplicationCall.userId get() = user.userId

context(ctx: RoutingContext)
suspend inline fun <reified T> T.sendAsResponse() {
    ctx.call.respond(this, typeInfo<T>())
}
