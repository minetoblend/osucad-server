package com.osucad.server.dao

import com.osucad.server.database.BlobsTable
import com.osucad.server.utils.UuidEntity
import com.osucad.server.utils.UuidEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import kotlin.uuid.Uuid

class Blob(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<Blob>(BlobsTable)

    var size by BlobsTable.size
}