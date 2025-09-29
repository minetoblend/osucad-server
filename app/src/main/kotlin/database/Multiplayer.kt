package com.osucad.server.database

import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

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