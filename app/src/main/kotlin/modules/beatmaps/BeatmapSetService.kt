package com.osucad.server.modules.beatmaps

import com.osucad.server.dao.BeatmapSet
import com.osucad.server.utils.EntityService
import com.osucad.server.utils.IEntityService
import com.osucad.server.utils.TransactionProvider

interface IBeatmapSetService : IEntityService<Int, BeatmapSet>

class BeatmapSetService(
    transaction: TransactionProvider
) : IBeatmapSetService,
    IEntityService<Int, BeatmapSet> by EntityService(BeatmapSet, transaction)