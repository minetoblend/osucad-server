package com.osucad.server.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
sealed interface ResultDto<T> {
    val success: Boolean
    val detail: T?

    @Serializable
    class Success<T>(
        override val detail: T? = null,
    ) : ResultDto<T> {
        @Required
        override val success = true
    }

    @Serializable
    class Failure<T>(
        val message: String,
        override val detail: T? = null,
    ) : ResultDto<T> {
        @Required
        override val success = false
    }
}

suspend fun ApplicationCall.respondSuccess() =
    respond<ResultDto<Nothing>>(HttpStatusCode.OK, ResultDto.Success())

suspend inline fun <reified T : Any> ApplicationCall.respondSuccess(detail: T?) =
    respond<ResultDto<T>>(HttpStatusCode.OK, ResultDto.Success(detail))

suspend fun ApplicationCall.respondFailure(
    message: String,
    status: HttpStatusCode = HttpStatusCode.BadRequest
) = respond<ResultDto<Nothing>>(status, ResultDto.Failure(message))

suspend inline fun <reified T> ApplicationCall.respondFailure(
    message: String,
    detail: T?,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) = respond<ResultDto<T>>(status, ResultDto.Failure(message, detail))
