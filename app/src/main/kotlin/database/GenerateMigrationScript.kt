@file:OptIn(ExperimentalDatabaseMigrationApi::class)

package com.osucad.server.database

import com.osucad.server.plugins.DatabaseConfig
import com.osucad.server.plugins.MIGRATIONS_DIRECTORY
import com.osucad.server.plugins.runMigrations
import com.osucad.server.utils.toHikariConfig
import com.zaxxer.hikari.HikariDataSource
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

    val dataSource = HikariDataSource(DatabaseConfig.Presets.H2.toHikariConfig())

    runMigrations(dataSource)

    val db = Database.connect(dataSource)

    transaction(db) {
        generateMigrationScript(migrationName)
    }
}

private fun lastMigrationVersion(): Int? =
    File(MIGRATIONS_DIRECTORY)
        .listFiles()
        .mapNotNull { it.name.split("__").firstOrNull() }
        .mapNotNull { it.trimStart('V').toIntOrNull() }
        .maxOrNull()

private fun generateMigrationScript(migrationName: String) {
    val lastMigrationIndex = lastMigrationVersion() ?: 0

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
