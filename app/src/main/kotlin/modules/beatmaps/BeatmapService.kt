package com.osucad.server.modules.beatmaps

import com.osucad.server.dao.Beatmap
import com.osucad.server.utils.EntityService
import com.osucad.server.utils.IEntityService
import com.osucad.server.utils.TransactionProvider

interface IBeatmapService : IEntityService<Int, Beatmap>

class BeatmapService(
    transaction: TransactionProvider
) : IBeatmapService, IEntityService<Int, Beatmap> by EntityService(Beatmap, transaction)