package com.osucad.server.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

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