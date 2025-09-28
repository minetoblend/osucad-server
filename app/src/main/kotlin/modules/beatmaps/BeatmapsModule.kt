package com.osucad.server.modules.beatmaps

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

fun Application.beatmapsModule() {
    dependencies {
        provide<IBeatmapSetService>(BeatmapSetService::class)
        provide<IBeatmapService>(BeatmapService::class)
    }

    beatmapSetRouting()
    beatmapRouting()
}