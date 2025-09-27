package com.osucad.server.modules.users

import com.osucad.server.LoginEvent
import com.osucad.server.apiRoute
import com.osucad.server.eventBus
import com.osucad.server.utils.orNotFound
import com.osucad.server.utils.sendAsResponse
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlin.time.Clock

fun Application.usersModule() {
    dependencies {
        provide<IUserService>(UserService::class)
    }

    val userService: IUserService by dependencies

    eventBus.subscribe(LoginEvent) { osuUser ->
        log.info("User logged in $osuUser")

        userService.createOrUpdate(osuUser.id) {
            username = osuUser.username
            lastLoginTime = Clock.System.now()
        }
    }

    routing {
        apiRoute("users") {
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
