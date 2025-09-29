package com.osucad.server.dao

import com.osucad.server.database.UserRelationsTable
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass

class UserRelation(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<UserRelation>(UserRelationsTable)

    var from by User referencedOn UserRelationsTable.fromId
    var to by User referencedOn UserRelationsTable.toId
    var kind by UserRelationsTable.kind
}

