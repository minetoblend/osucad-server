package com.osucad.server.modules.users

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

fun Application.usersModule() {
    dependencies {
        provide<IUserService>(UserService::class)
    }

    userRouting()
}
