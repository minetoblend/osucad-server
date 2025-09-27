package com.osucad.server.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureErrorHandling() {
    val logger = log

    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respondText("400: ${cause.localizedMessage}", status = HttpStatusCode.BadRequest)
        }
        exception<NotFoundException> { call, cause ->
            call.respondText("404: ${cause.localizedMessage}", status = HttpStatusCode.NotFound)
        }
        exception<Throwable> { call, cause ->
            logger.error("Error when handling request", cause)

            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
}