package com.osucad.server

import com.osucad.server.services.OsuApiFactory
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

fun Application.configureServices() {
    dependencies {
        provide<OsuApiFactory> { OsuApiFactory.Default }
    }
}