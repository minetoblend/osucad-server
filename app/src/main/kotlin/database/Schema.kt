@file:OptIn(ExperimentalTime::class)

package com.osucad.server.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime

val tables = arrayOf<Table>(
    UsersTable,
    BeatmapSetsTable,
    BeatmapsTable,
    BlobsTable,
    DocumentsTable,
    DocumentSnapshotsTable,
    DocumentBlobUsagesTable,
)

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 255)
    val lastLoginTime = timestamp("last_login_time").nullable()
}

object BeatmapSetsTable : IntIdTable("beatmapsets") {
    val creatorId = reference("creator_id", UsersTable.id)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    val artist = varchar("artist", 255)
    val title = varchar("title", 255)
}

object BeatmapsTable : IntIdTable("beatmaps") {
    val beatmapSetId = reference("beatmapset_id", BeatmapSetsTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val difficultyOwnerId = reference("difficulty_owner_id", UsersTable.id).nullable()

    val artist = varchar("artist", 255)
    val title = varchar("title", 255)
    val difficultyName = varchar("difficulty_name", 255)

    val documentId = reference("document_id", DocumentsTable.id)
}

object BlobsTable : UUIDTable("blobs") {
    val size = integer("size")
}

object DocumentsTable : IntIdTable("documents")

object DocumentSnapshotsTable : CompositeIdTable("document_snapshots") {
    val documentId = reference("document_id", DocumentsTable.id)
    val sequenceNumber = long("sequence_number").entityId()

    val summaryBlobId = reference("summary_blob_id", BlobsTable.id)

    init {
        addIdColumn(documentId)
    }


    override val primaryKey = PrimaryKey(documentId, sequenceNumber)
}

object DocumentBlobUsagesTable : CompositeIdTable("document_blob_usages") {
    val documentId = reference("document_id", DocumentsTable.id)
    val blobId = reference("blob_id", BlobsTable.id)

    init {
        addIdColumn(documentId)
        addIdColumn(blobId)
    }

    override val primaryKey = PrimaryKey(documentId, blobId)
}