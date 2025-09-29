package com.osucad.server.modules.documents

import com.osucad.server.dao.Blob
import com.osucad.server.dao.Document
import com.osucad.server.dao.DocumentBlobUsage
import com.osucad.server.dao.DocumentSnapshot
import com.osucad.server.database.BlobsTable
import com.osucad.server.database.DocumentBlobUsagesTable
import com.osucad.server.database.DocumentSnapshotsTable
import com.osucad.server.modules.documents.CreateSnapshotResult.*
import com.osucad.server.utils.IsolationLevel.RepeatableRead
import com.osucad.server.utils.TransactionProvider
import org.jetbrains.exposed.v1.core.Max
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.dao.load
import org.jetbrains.exposed.v1.jdbc.batchInsert
import kotlin.uuid.Uuid

sealed interface CreateSnapshotResult {
    class Success(val snapshot: DocumentSnapshot) : CreateSnapshotResult

    sealed interface Failure : CreateSnapshotResult

    class MissingBlobs(val missingIds: List<Uuid>) : CreateSnapshotResult, Failure
    class SequenceNumberNotIncremented(val currentSequenceNumber: Long) : CreateSnapshotResult, Failure
    object SequenceNumberNegative : CreateSnapshotResult, Failure
}

interface IDocumentSnapshotService {
    fun getSnapshot(documentId: Int, sequenceNumber: Long): DocumentSnapshot?

    fun getLatestSnapshot(documentId: Int): DocumentSnapshot?

    fun create(document: Document, summary: DocumentSummary): CreateSnapshotResult
}

class DocumentSnapshotService(private val transaction: TransactionProvider) : IDocumentSnapshotService {
    override fun getSnapshot(documentId: Int, sequenceNumber: Long): DocumentSnapshot? = transaction {
        val query = DocumentSnapshotsTable.let { snapshot ->
            DocumentSnapshot.find {
                (snapshot.documentId eq documentId) and (snapshot.sequenceNumber eq sequenceNumber)
            }
        }

        query.singleOrNull()
    }

    override fun getLatestSnapshot(documentId: Int): DocumentSnapshot? = transaction {
        val query = DocumentSnapshotsTable.let { snapshot ->
            DocumentSnapshot.find {
                (snapshot.documentId eq documentId) and
                        (snapshot.sequenceNumber eq Max(snapshot.sequenceNumber, snapshot.sequenceNumber.columnType))
            }
        }

        query.singleOrNull()
    }

    override fun create(document: Document, summary: DocumentSummary): CreateSnapshotResult {
        if (summary.sequenceNumber < 0)
            return SequenceNumberNegative

        return transaction(transactionIsolation = RepeatableRead) {
            val lastSnapshot = getLatestSnapshot(document.id.value)

            if (lastSnapshot != null && summary.sequenceNumber <= lastSnapshot.sequenceNumber)
                return@transaction SequenceNumberNotIncremented(lastSnapshot.sequenceNumber)

            val blobs = Blob.find { BlobsTable.id inList summary.blobs }

            val missingIds = blobs.map { it.id.value } - summary.blobs

            if (missingIds.isNotEmpty())
                return@transaction MissingBlobs(missingIds.toList())

            val snapshot = DocumentSnapshot.new {
                this.document = document
                this.sequenceNumber = summary.sequenceNumber
                this.summary = summary
                this.referencedBlobs = blobs
            }

            snapshot.refresh(true)
            snapshot.load(DocumentSnapshot::document, DocumentSnapshot::referencedBlobs)

            Success(snapshot)
        }
    }
}