@file:OptIn(ExperimentalTime::class)

package com.osucad.server.modules.beatmaps

import com.osucad.server.dao.Beatmap
import kotlinx.serialization.Serializable
import tech.mappie.api.ObjectMappie
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class BeatmapDto(
    val id: Int,
    val beatmapSetId: Int,
    val createdAt: Instant,
    val artist: String,
    val title: String,
    val difficultyName: String,
)

object BeatmapMapper : ObjectMappie<Beatmap, BeatmapDto>() {
    override fun map(from: Beatmap) = mapping {
        to::id fromProperty from::id transform { it.value }
        to::beatmapSetId fromProperty from::beatmapSetId transform { it.value }
    }
}

fun Beatmap.toDto() = BeatmapMapper.map(this)

fun List<Beatmap>.toDtos() = BeatmapMapper.mapList(this)