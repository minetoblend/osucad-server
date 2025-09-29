@file:OptIn(ExperimentalTime::class)

package com.osucad.server.dao

import com.osucad.server.database.BeatmapSetsTable
import com.osucad.server.database.UserRelationsTable
import com.osucad.server.database.UsersTable
import com.osucad.server.modules.users.UserRelationKind.Friend
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(UsersTable)

    var username by UsersTable.username
    var osuUserId by UsersTable.osuUserId
    var lastLoginTime by UsersTable.lastLoginTime

    val beatmapSets by BeatmapSet referrersOn BeatmapSetsTable.creatorId
    val outgoingRelations by UserRelation referrersOn UserRelationsTable.fromId
    val incomingRelations by UserRelation referrersOn UserRelationsTable.toId

    val friends get() = outgoingRelations.filter { it.kind == Friend }.map { it.to }
}