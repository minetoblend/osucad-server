package com.osucad.server.dao

import com.osucad.server.database.DocumentBlobUsagesTable
import com.osucad.server.database.DocumentSnapshotsTable
import com.osucad.server.database.DocumentsTable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Document(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Document>(DocumentsTable)

    val snapshots by DocumentSnapshot referrersOn DocumentSnapshotsTable.documentId orderBy (DocumentSnapshotsTable.sequenceNumber to SortOrder.DESC)
}

class DocumentSnapshot(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<DocumentSnapshot>(DocumentSnapshotsTable)

    var document by Document referencedOn DocumentSnapshotsTable.documentId
    var sequenceNumber by DocumentSnapshotsTable.sequenceNumber
    val blobs by Blob via DocumentBlobUsagesTable
}