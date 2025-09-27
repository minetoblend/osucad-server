package com.osucad.server.modules.users


import com.osucad.server.security.currentUser
import com.osucad.server.utils.apiRoute
import com.osucad.server.utils.orNotFound
import com.osucad.server.utils.sendAsResponse
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.userRouting() {
    val userService: IUserService by dependencies

    routing {
        authenticate {
            apiRoute("users") {
                get("me") {
                    call.currentUser()
                        .toDto()
                        .sendAsResponse()
                }

                get("{id}") {
                    val id: Int by call.pathParameters

                    userService.findById(id)
                        .orNotFound()
                        .toDto()
                        .sendAsResponse()
                }
            }
        }
    }
}