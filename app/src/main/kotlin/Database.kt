package com.osucad.server

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.*
import kotlinx.serialization.Serializable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

const val MIGRATIONS_DIRECTORY = "app/src/main/kotlin/migrations"

@Serializable
class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String,
)

@Serializable
class FlywayConfig(
    val url: String,
    val user: String,
    val password: String,
)

fun Application.configureDatabase() {
    val config: DatabaseConfig = property("database")

    val database = R2dbcDatabase.connect(config)

    runMigrations()

    dependencies {
        provide<R2dbcDatabase> { database }
    }
}

fun Application.runMigrations() {
    val config: FlywayConfig = property("flyway")

    val flyway = Flyway.configure()
        .dataSource(
            config.url,
            config.user,
            config.password,
        )
        .locations("filesystem:$MIGRATIONS_DIRECTORY")
        .baselineOnMigrate(true)
        .load()

    flyway.migrate()
}

fun R2dbcDatabase.Companion.connect(config: DatabaseConfig) = R2dbcDatabase.connect(
    url = config.url,
    driver = config.driver,
    user = config.user,
    password = config.password,
)