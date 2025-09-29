package com.osucad.server.database

import com.osucad.server.modules.documents.DocumentSummary
import com.osucad.server.utils.UuidTable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.json.json

object BlobsTable : UuidTable("blobs") {
    val size = integer("size")
}

typealias DocumentId = Int

object DocumentsTable : IntIdTable("documents")

object DocumentSnapshotsTable : IntIdTable("document_snapshots") {
    val documentId = reference("document_id", DocumentsTable.id)
    val sequenceNumber = long("sequence_number")

    val summary = json<DocumentSummary>("summary", Json)

    init {
        uniqueIndex(documentId, sequenceNumber)
    }
}

object DocumentBlobUsagesTable : CompositeIdTable("document_blob_usages") {
    val snapshotId = reference("snapshot_id", DocumentSnapshotsTable.id)
    val blobId = reference("blob_id", BlobsTable.id)

    init {
        addIdColumn(snapshotId)
        addIdColumn(blobId)
    }

    override val primaryKey = PrimaryKey(snapshotId, blobId)
}