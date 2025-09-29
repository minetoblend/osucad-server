package com.osucad.server.modules.beatmaps

import com.osucad.server.dao.Beatmap
import com.osucad.server.database.BeatmapSetsTable
import com.osucad.server.utils.EntityService
import com.osucad.server.utils.IEntityService
import com.osucad.server.utils.TransactionProvider
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

interface IBeatmapService : IEntityService<Int, Beatmap>

class BeatmapService(
    transaction: TransactionProvider
) : IBeatmapService, IEntityService<Int, Beatmap> by EntityService(Beatmap, transaction) {
    fun foo() = transaction {
        Beatmap.findById(0)!!.artist = "5"
    }
}