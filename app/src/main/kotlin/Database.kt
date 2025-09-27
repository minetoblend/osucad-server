package com.osucad.server

import com.osucad.server.utils.ITransactionProvider
import com.osucad.server.utils.TransactionProvider
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.*
import kotlinx.serialization.Serializable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

const val MIGRATIONS_DIRECTORY = "src/main/kotlin/migrations"

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

fun Application.configureDatabase(config: DatabaseConfig = property("database")) {
    val database = Database.connect(config)

    runMigrations(database, config)

    dependencies {
        provide<Database> { database }
        provide<ITransactionProvider> { TransactionProvider(database) }
    }
}

fun runMigrations(
    database: Database,
    config: DatabaseConfig,
) {
    val flyway = Flyway.configure()
        .dataSource(
            config.url,
            config.user,
            config.password,
        )
        .locations("filesystem:$MIGRATIONS_DIRECTORY")
        .baselineOnMigrate(true)
        .load()

    transaction(database) {
        flyway.migrate()
    }
}

fun Database.Companion.connect(config: DatabaseConfig) = Database.connect(
    url = config.url,
    driver = config.driver,
    user = config.user,
    password = config.password,
)
