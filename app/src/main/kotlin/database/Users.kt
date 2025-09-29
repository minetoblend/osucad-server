package com.osucad.server.database

import com.osucad.server.modules.users.UserRelationKind
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 255)
    val lastLoginTime = timestamp("last_login_time").nullable()
}

object UserRelationsTable : CompositeIdTable("user_relations") {
    val fromId = reference("from_id", UsersTable.id)
    val toId = reference("to_id", UsersTable.id)
    val kind = enumeration<UserRelationKind>("kind")

    init {
        addIdColumn(fromId)
        addIdColumn(toId)
    }

    override val primaryKey = PrimaryKey(fromId, toId)
}

fun userRelationId(
    fromId: Int,
    toId: Int,
): CompositeID = CompositeID {
    it[UserRelationsTable.fromId] = fromId
    it[UserRelationsTable.toId] = toId
}