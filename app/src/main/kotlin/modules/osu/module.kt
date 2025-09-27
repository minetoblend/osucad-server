package com.osucad.server.modules.osu

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

fun Application.osuModule() {
    dependencies {
        provide<OsuApiFactory> { OsuApiFactory.Default }
    }
}
