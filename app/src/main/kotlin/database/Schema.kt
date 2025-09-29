@file:OptIn(ExperimentalTime::class)

package com.osucad.server.database

import org.jetbrains.exposed.v1.core.Table
import kotlin.time.ExperimentalTime

val tables = arrayOf<Table>(
    UsersTable,
    UserRelationsTable,
    BeatmapSetsTable,
    BeatmapsTable,
    BlobsTable,
    DocumentsTable,
    DocumentSnapshotsTable,
    DocumentBlobUsagesTable,
)