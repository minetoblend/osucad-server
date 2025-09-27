package com.osucad.server

import com.osucad.server.plugins.configureDatabase
import com.osucad.server.plugins.configureErrorHandling
import com.osucad.server.plugins.configureHTTP
import com.osucad.server.plugins.configureMonitoring
import com.osucad.server.plugins.configureSecurity
import com.osucad.server.plugins.configureSerialization
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

    routing {
        get {
            call.respond("Hello, world!")
        }
    }
}
