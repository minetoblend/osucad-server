package com.osucad.server.modules.documents

import com.osucad.server.dao.DocumentSnapshot
import com.osucad.server.database.DocumentId
import kotlinx.serialization.Serializable
import tech.mappie.api.ObjectMappie

@Serializable
class DocumentSnapshotDto(
    val documentId: DocumentId,
    val sequenceNumber: Long,
    val summary: DocumentSummary,
)

object DocumentSnapshotMapper : ObjectMappie<DocumentSnapshot, DocumentSnapshotDto>() {
    override fun map(from: DocumentSnapshot) = mapping {
        to::documentId fromProperty from::documentId transform { it.value }
        to::sequenceNumber fromProperty from::sequenceNumber
    }
}

fun DocumentSnapshot.toDto() = DocumentSnapshotMapper.map(this)

fun List<DocumentSnapshot>.toDtos() = DocumentSnapshotMapper.mapList(this)