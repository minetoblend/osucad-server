package com.osucad.server.modules.users

import com.osucad.server.LoginEvent
import com.osucad.server.eventBus
import com.osucad.server.services.IUserService
import com.osucad.server.services.UserService
import com.osucad.server.utils.orNotFound
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction


fun Application.usersModule() {
    dependencies {
        provide<IUserService>(UserService::class)
    }

    eventBus.subscribe(LoginEvent) { user ->
        val userService: UserService by dependencies

        suspendTransaction {
            userService.createOrUpdate(user.id, user.username)
            userService.updateLastLoginTime(user.id)
        }
    }

    routing {
        route("api/v1/users") {
            get("{id}") {
                val id: Int by call.pathParameters

                val userService: UserService by dependencies

                val user = userService.findById(id).orNotFound()

                call.respond(UserMapper.map(user))
            }
        }
    }
}