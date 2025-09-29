package com.osucad.server.modules.documents

import com.osucad.server.dao.Document
import com.osucad.server.dao.DocumentSnapshot
import com.osucad.server.database.DocumentId
import com.osucad.server.utils.EntityService
import com.osucad.server.utils.IEntityService
import com.osucad.server.utils.TransactionProvider

interface IDocumentService : IEntityService<DocumentId, Document> {
    fun createDocument(initialSummary: InitialDocumentSummary): CreateDocumentResult
}

sealed interface CreateDocumentResult {
    class Success(val document: Document, val snapshot: DocumentSnapshot) : CreateDocumentResult

    sealed interface Failure
    class CreateSnapshotFailure(val error: CreateSnapshotResult.Failure) : CreateDocumentResult, Failure
}

class DocumentService(
    private val transaction: TransactionProvider,
    private val snapshotService: IDocumentSnapshotService,
) :
    IDocumentService,
    IEntityService<DocumentId, Document> by EntityService(Document, transaction) {
    override fun createDocument(initialSummary: InitialDocumentSummary): CreateDocumentResult =
        transaction(transactionIsolation = RepeatableRead) {

            val document = Document.new { }

            val summary = DocumentSummary(
                sequenceNumber = 0L,
                audience = AudienceSummary.Empty,
                attributes = initialSummary.attributes,
                type = initialSummary.type,
                root = initialSummary.root,
                entries = initialSummary.entries,
                blobs = initialSummary.blobs,
            )

            when (val result = snapshotService.create(document, summary)) {
                is CreateSnapshotResult.Success -> CreateDocumentResult.Success(document, result.snapshot)
                is CreateSnapshotResult.Failure -> CreateDocumentResult.CreateSnapshotFailure(result)
            }
        }
}