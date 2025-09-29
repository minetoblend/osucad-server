package com.osucad.server.modules.documents

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.osucad.server.database.DocumentId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.*
import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Serializable
data class JwtSettings(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
)

fun Application.configureDocumentSecurity(config: JwtSettings = property("documents.jwt")) {
    dependencies {
        provide<IDocumentTokenGenerator> { DocumentTokenGenerator(config) }
    }

    authentication {
        jwt("auth-documents") {
            realm = config.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .withClaimPresence("scope")
                    .build()
            )
            validate { credential ->
                runCatching {
                    DefaultDocumentPrincipal(
                        documentId = credential.payload.subject.toInt(),
                        scopes = credential.getListClaim("scope", String::class)
                            .map { DocumentScope.parse(it)!! }
                            .toSet()
                    )
                }.getOrNull()
            }
        }
    }
}

interface IDocumentTokenGenerator {
    fun generateToken(documentId: DocumentId, scopes: Set<DocumentScope>): String
}

class DocumentTokenGenerator(private val config: JwtSettings) : IDocumentTokenGenerator {
    override fun generateToken(documentId: DocumentId, scopes: Set<DocumentScope>): String =
        JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withSubject(documentId.toString())
            .withExpiresAt(Instant.now() + 1.hours.toJavaDuration())
            .withArrayClaim("scope", scopes.map { it.value }.toTypedArray())
            .sign(Algorithm.HMAC256(config.secret))
}