package com.osucad.server.modules.documents

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.uuid.Uuid

@Serializable
data class DocumentSummary(
    val sequenceNumber: Long,
    val audience: AudienceSummary,
    val attributes: JsonObject,
    val type: List<DDSAttributes>,
    val root: String,
    val entries: Map<String, ObjectSummary>,
    val blobs: Set<Uuid>,
)

@Serializable
data class InitialDocumentSummary(
    val attributes: JsonObject,
    val type: List<DDSAttributes>,
    val root: String,
    val entries: Map<String, ObjectSummary>,
    val blobs: Set<Uuid>,
)

@Serializable
data class ObjectSummary(
    val attributes: DDSAttributes,
    val content: JsonElement,
)

@Serializable
data class DDSAttributes(
    val type: String,
    val version: Int,
)

@Serializable
data class AudienceSummary(val clients: List<ClientInfo>) {
    companion object {
        val Empty = AudienceSummary(clients = emptyList())
    }
}

@Serializable
data class ClientInfo(val clientId: String)