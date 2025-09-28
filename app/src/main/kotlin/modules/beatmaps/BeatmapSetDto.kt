@file:OptIn(ExperimentalTime::class)

package com.osucad.server.modules.beatmaps

import com.osucad.server.dao.BeatmapSet
import com.osucad.server.modules.users.UserDto
import com.osucad.server.modules.users.UserMapper
import kotlinx.serialization.Serializable
import tech.mappie.api.ObjectMappie
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
class BeatmapSetDto(
    val id: Int,
    val creator: UserDto,
    val createdAt: Instant,
    val artist: String,
    val title: String,
    val beatmaps: List<BeatmapDto>
)

object BeatmapSetMapper : ObjectMappie<BeatmapSet, BeatmapSetDto>() {
    override fun map(from: BeatmapSet) = mapping {
        to::id fromProperty from::id transform { it.value }
        to::creator fromProperty from::creator via UserMapper
        to::beatmaps fromProperty from::beatmaps transform { it.map(BeatmapMapper::map) }
    }
}

fun BeatmapSet.toDto(): BeatmapSetDto = BeatmapSetMapper.map(this)

fun List<BeatmapSet>.toDtos() = BeatmapSetMapper.mapList(this)