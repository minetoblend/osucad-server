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

class DocumentSnapshot(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DocumentSnapshot>(DocumentSnapshotsTable)

    var document by Document referencedOn DocumentSnapshotsTable.documentId
    var documentId by DocumentSnapshotsTable.documentId

    var sequenceNumber by DocumentSnapshotsTable.sequenceNumber

    var summary by DocumentSnapshotsTable.summary

    var referencedBlobs by Blob via DocumentBlobUsagesTable
}

class DocumentBlobUsage(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<DocumentBlobUsage>(DocumentBlobUsagesTable)

    var snapshot by DocumentSnapshot referencedOn DocumentBlobUsagesTable.snapshotId
    var blob by Blob referencedOn DocumentBlobUsagesTable.blobId
}