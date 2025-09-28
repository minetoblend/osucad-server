package com.osucad.server.modules.beatmaps

import com.osucad.server.utils.apiRoute
import com.osucad.server.utils.orNotFound
import com.osucad.server.utils.sendAsResponse
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.beatmapRouting() {
    val beatmapService: IBeatmapService by dependencies

    routing {
        authenticate {
            apiRoute("beatmaps") {
                get("{id}") {
                    val id: Int by call.parameters

                    beatmapService.findById(id)
                        .orNotFound()
                        .toDto()
                        .sendAsResponse()
                }

                get {
                    val ids: List<Int> by call.queryParameters

                    beatmapService.findByIds(ids.take(50))
                        .toDtos()
                        .sendAsResponse()
                }
            }
        }
    }
}