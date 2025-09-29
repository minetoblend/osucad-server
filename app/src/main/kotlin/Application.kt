package com.osucad.server

import com.osucad.server.modules.osu.osuModule
import com.osucad.server.modules.users.usersModule
import com.osucad.server.plugins.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureSecurity()
    configureErrorHandling()
    configureMonitoring()
    configureDatabase()

    usersModule()
    osuModule()

    routing {
        get {
            call.respond("Hello, world!")
        }
    }
}
