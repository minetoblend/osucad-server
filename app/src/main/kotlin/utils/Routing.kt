package com.osucad.server.utils

import io.ktor.server.routing.*
import io.ktor.utils.io.*

@KtorDsl
fun Route.apiRoute(build: Route.() -> Unit): Route =
    route("api/v1", build)

@KtorDsl
fun Route.apiRoute(path: String, build: Route.() -> Unit): Route =
    apiRoute { route(path, build) }

