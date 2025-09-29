package com.osucad.server.utils

import org.flywaydb.core.Flyway
import javax.sql.DataSource

const val MIGRATIONS_DIRECTORY = "db/migrations"

const val MIGRATIONS_SOURCE_DIRECTORY = "src/main/resources/$MIGRATIONS_DIRECTORY"

fun createFlyway(dataSource: DataSource): Flyway =
    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:$MIGRATIONS_DIRECTORY")
        .executeInTransaction(true)
        .load()

fun Flyway.currentMigrationVersion(): Int? = info().all().maxOfOrNull { it.version.major }?.intValueExact()