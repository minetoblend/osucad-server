package com.osucad.server.modules.documents

import com.osucad.server.database.DocumentId
import com.osucad.server.modules.documents.DocumentScope.Read
import com.osucad.server.modules.documents.DocumentScope.Summarize
import com.osucad.server.utils.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.documentsRouting() {
    val documentService: IDocumentService by dependencies
    val snapshotService: IDocumentSnapshotService by dependencies

    routing {
        authenticate("auth-documents") {
            apiRoute("documents") {
                post {
                    when (val result = documentService.createDocument(call.receive())) {
                        is Success -> call.respondSuccess()
                        is CreateSnapshotFailure -> call.respondCreateSnapshotError(result.error)
                    }
                }

                get("{documentId}/snapshots/latest") {
                    val documentId: DocumentId by call.parameters

                    val principal = call.principal<DocumentPrincipal>()!!

                    if (!principal.can(documentId, Read))
                        return@get call.respond(ForbiddenResponse())

                    snapshotService.getLatestSnapshot(documentId)
                        .orNotFound()
                        .toDto()
                        .sendAsResponse()
                }

                get("{documentId}/snapshots/{sequenceNumber}") {
                    val documentId: DocumentId by call.parameters
                    val sequenceNumber: Long by call.parameters

                    val principal = call.principal<DocumentPrincipal>()!!

                    if (!principal.can(documentId, Read))
                        return@get call.respond(ForbiddenResponse())

                    snapshotService.getSnapshot(documentId, sequenceNumber)
                        .orNotFound()
                        .toDto()
                        .sendAsResponse()
                }

                put("{documentId}/snapshots") {
                    val documentId: DocumentId by call.pathParameters

                    val principal = call.principal<DocumentPrincipal>()!!

                    if (!principal.can(documentId, Summarize))
                        return@put call.respond(ForbiddenResponse())

                    val document = documentService.findById(documentId).orNotFound()

                    when (val result = snapshotService.create(document, call.receive())) {
                        is CreateSnapshotResult.Success -> call.respondSuccess()
                        is CreateSnapshotResult.Failure -> call.respondCreateSnapshotError(result)
                    }
                }
            }
        }
    }
}

private suspend fun ApplicationCall.respondCreateSnapshotError(result: CreateSnapshotResult.Failure) = when (result) {
    is CreateSnapshotResult.MissingBlobs -> respondFailure(
        "Missing blob ids",
        result.missingIds,
    )

    is CreateSnapshotResult.SequenceNumberNegative -> respondFailure(
        "Sequence number is negative",
    )

    is CreateSnapshotResult.SequenceNumberNotIncremented -> respondFailure(
        "Sequence number is same or equal to previous snapshot",
        mapOf("currentSequenceNumber" to result.currentSequenceNumber),
    )
}