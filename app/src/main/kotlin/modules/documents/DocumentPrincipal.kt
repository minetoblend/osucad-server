package com.osucad.server.modules.documents

import com.osucad.server.database.DocumentId

interface DocumentPrincipal {
    fun can(documentId: DocumentId, scope: DocumentScope): Boolean

    fun canRead(documentId: DocumentId) = can(documentId, Read)
    fun canWrite(documentId: DocumentId) = can(documentId, Write)
    fun canSummarize(documentId: DocumentId) = can(documentId, Summarize)

    object UnsafeAllowAll : DocumentPrincipal {
        override fun can(documentId: DocumentId, scope: DocumentScope) = true
    }
}

open class DefaultDocumentPrincipal(
    val documentId: DocumentId,
    val scopes: Set<DocumentScope>,
) : DocumentPrincipal {
    override fun can(documentId: DocumentId, scope: DocumentScope) =
        documentId == this.documentId && scope in scopes
}


enum class DocumentScope(val value: String) {
    Read("document:read"),
    Write("document:write"),
    Summarize("document:summarize");

    companion object {
        fun parse(value: String) = DocumentScope.entries.find { it.value == value }
    }

    override fun toString(): String = value
}