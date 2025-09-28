package com.osucad.server.modules.beatmaps

import com.osucad.server.dao.BeatmapSet
import com.osucad.server.database.BeatmapSetsTable
import com.osucad.server.utils.apiRoute
import com.osucad.server.utils.orNotFound
import com.osucad.server.utils.sendAsResponse
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.jetbrains.exposed.v1.core.eq

fun Application.beatmapSetRouting() {
    val beatmapSetService: IBeatmapSetService by dependencies

    routing {
        authenticate {
            apiRoute("beatmapsets") {
                get("{id}") {
                    val id: Int by call.pathParameters

                    beatmapSetService.findById(id, BeatmapSet::creator, BeatmapSet::beatmaps)
                        .orNotFound()
                        .toDto()
                        .sendAsResponse()
                }

                get {
                    val ids: List<Int> by call.queryParameters

                    beatmapSetService.findByIds(ids.take(50))
                        .toDtos()
                        .sendAsResponse()
                }
            }
        }
    }
}