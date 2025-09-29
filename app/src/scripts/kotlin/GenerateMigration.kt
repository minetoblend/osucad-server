import com.osucad.server.database.tables
import com.osucad.server.utils.MIGRATIONS_SOURCE_DIRECTORY
import com.osucad.server.utils.createFlyway
import com.osucad.server.utils.currentMigrationVersion
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer
import java.io.File
import javax.sql.DataSource

private val logger = LoggerFactory.getLogger("Migrations")

fun main(args: Array<String>) {
    val migrationName = args.firstOrNull() ?: run {
        print("Migration name: ")
        readln()
    }

    val postgres = PostgreSQLContainer("postgres:18-alpine").apply {
        startupAttempts = 1
        start()
    }

    val dataSource = HikariDataSource(postgres)

    val flyway = createFlyway(dataSource)

    flyway.migrate()

    val version = (flyway.currentMigrationVersion() ?: 0) + 1

    generateMigrationScript(
        dataSource = dataSource,
        filename = "V${version}__${migrationName.replace(' ', '_')}"
    )
}

fun generateMigrationScript(dataSource: DataSource, filename: String): Boolean {
    val db = Database.connect(dataSource)

    File(MIGRATIONS_SOURCE_DIRECTORY).mkdirs()

    val file = transaction(db) {
        MigrationUtils.generateMigrationScript(
            *tables,
            scriptDirectory = MIGRATIONS_SOURCE_DIRECTORY,
            scriptName = filename,
        )
    }

    if (file.readText().isBlank()) {
        logger.warn("No changes detected, no migration was generated.")
        file.delete()
        return false
    }

    return true
}