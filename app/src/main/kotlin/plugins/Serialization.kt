package com.osucad.server.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SerializationConfig(
    val prettyPrint: Boolean = false
)

fun Application.configureSerialization(
    config: SerializationConfig = propertyOrNull("serialization") ?: SerializationConfig()
) {

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = config.prettyPrint
        })
    }
}