package com.osucad.server.dao

import com.osucad.server.database.BlobsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*

class Blob(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Blob>(BlobsTable)

    var size by BlobsTable.size
}