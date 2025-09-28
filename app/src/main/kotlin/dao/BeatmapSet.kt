@file:OptIn(ExperimentalTime::class)

package com.osucad.server.dao

import com.osucad.server.database.BeatmapSetsTable
import com.osucad.server.database.BeatmapsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

class BeatmapSet(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeatmapSet>(BeatmapSetsTable)

    val beatmaps by Beatmap referrersOn BeatmapsTable.beatmapSetId

    var creator by User referencedOn BeatmapSetsTable.creatorId
    var createdAt by BeatmapSetsTable.createdAt
    var artist by BeatmapSetsTable.artist
    var title by BeatmapSetsTable.title
}

