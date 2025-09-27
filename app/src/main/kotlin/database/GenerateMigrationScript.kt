@file:OptIn(ExperimentalDatabaseMigrationApi::class)

package com.osucad.server.database

import com.osucad.server.DatabaseConfig
import com.osucad.server.MIGRATIONS_DIRECTORY
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger("Migrations")

fun main(args: Array<String>) {
    val migrationName = args.firstOrNull() ?: run {
        print("Migration name: ")
        readln()
    }

    val config = DatabaseConfig(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver",
        user = "root",
        password = "",
    )

    val h2db = Database.connect(
        url = config.url,
        driver = config.driver,
        user = config.user,
        password = config.password,
    )

    val flyway = Flyway.configure()
        .dataSource(
            config.url,
            config.user,
            config.password,
        )
        .locations("filesystem:$MIGRATIONS_DIRECTORY")
        .baselineOnMigrate(true)
        .load()

    transaction(h2db) {
        flyway.migrate()
    }

    transaction(h2db) {
        generateMigrationScript(migrationName)
    }
}

private fun generateMigrationScript(migrationName: String) {
    val lastMigrationIndex = File(MIGRATIONS_DIRECTORY)
        .listFiles()
        .mapNotNull { it.name.split("__").firstOrNull() }
        .firstNotNullOfOrNull { it.trimStart('V').toIntOrNull() }
        ?: 0

    val migrationIndex = lastMigrationIndex + 1

    val statements = MigrationUtils.statementsRequiredForDatabaseMigration(*tables, withLogs = false)
    if (statements.isEmpty()) {
        logger.warn("No changes detected, no migration was generated.")
        return
    }

    MigrationUtils.generateMigrationScript(
        *tables,
        scriptDirectory = MIGRATIONS_DIRECTORY,
        scriptName = "V${migrationIndex}__${migrationName.replace(' ', '_')}",
    )
}