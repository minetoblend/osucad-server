package com.osucad.server.plugins

import com.osucad.server.utils.TransactionProvider
import com.osucad.server.utils.asTransactionProvider
import com.osucad.server.utils.toHikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.*
import kotlinx.serialization.Serializable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import javax.sql.DataSource

const val MIGRATIONS_DIRECTORY = "src/main/kotlin/migrations"

@Serializable
class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String,
) {
    object Presets {
        val H2 = DatabaseConfig(
            url = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
            user = "root",
            password = "",
        )
    }
}

fun Application.configureDatabase(config: DatabaseConfig = property("database")) {
    val dataSource = HikariDataSource(config.toHikariConfig())

    val database = Database.connect(dataSource)

    runMigrations(dataSource)

    dependencies {
        provide<Database> { database }
        provide<TransactionProvider> { database.asTransactionProvider() }
    }
}

fun runMigrations(dataSource: DataSource) {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("filesystem:$MIGRATIONS_DIRECTORY")
        .baselineOnMigrate(true)
        .executeInTransaction(true)
        .load()

    flyway.migrate()
}