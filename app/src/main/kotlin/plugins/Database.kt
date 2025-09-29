package com.osucad.server.plugins

import com.osucad.server.utils.TransactionProvider
import com.osucad.server.utils.asTransactionProvider
import com.osucad.server.utils.createFlyway
import com.osucad.server.utils.toHikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager

@Serializable
class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String,
)

fun Application.configureDatabase(config: DatabaseConfig = property("database")) {
    val dataSource = HikariDataSource(config.toHikariConfig())

    val database = Database.connect(dataSource)

    createFlyway(dataSource).migrate()

    log.info("default transaction level: ${database.transactionManager.defaultIsolationLevel}")

    dependencies {
        provide<Database> { database }
        provide<TransactionProvider> { database.asTransactionProvider() }
    }
}