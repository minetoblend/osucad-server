@file:OptIn(ExperimentalTime::class)

package com.osucad.server.dao

import com.osucad.server.database.BeatmapsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

class Beatmap(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Beatmap>(BeatmapsTable)

    var beatmapSet by BeatmapSet referencedOn BeatmapsTable.beatmapSetId
    var beatmapSetId by BeatmapsTable.id

    var difficultyOwner by User optionalReferencedOn BeatmapsTable.difficultyOwnerId

    var createdAt by BeatmapsTable.createdAt
    var artist by BeatmapsTable.artist
    var title by BeatmapsTable.title
    var difficultyName by BeatmapsTable.difficultyName
}