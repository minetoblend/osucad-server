import com.osucad.server.database.tables
import com.osucad.server.plugins.runMigrations
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.testcontainers.containers.PostgreSQLContainer

class MigrationTest : FunSpec({
    test("Database matches expected schema after migrations are run") {
        val postgres = PostgreSQLContainer("postgres:16-alpine").apply {
            startupAttempts = 1
            start()
        }

        val dataSource = HikariDataSource(postgres.toHikariConfig())

        runMigrations(dataSource)

        val db = Database.connect(dataSource)

        val statements = transaction(db) {
            MigrationUtils.statementsRequiredForDatabaseMigration(*tables, withLogs = false)
        }

        statements.shouldBeEmpty()
    }
})