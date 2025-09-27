import com.osucad.server.plugins.DatabaseConfig
import com.osucad.server.plugins.configureDatabase
import com.osucad.server.utils.toHikariConfig
import io.ktor.server.application.*
import org.testcontainers.containers.PostgreSQLContainer

fun Application.h2Database() {
    configureDatabase(
        config = DatabaseConfig(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL",
            driver = "org.h2.Driver",
            user = "root",
            password = "",
        )
    )
}

fun Application.testModule() {
    h2Database()
}

fun PostgreSQLContainer<*>.toDbConfig() = DatabaseConfig(
    url = jdbcUrl,
    user = username,
    password = password,
    driver = driverClassName,
)

fun PostgreSQLContainer<*>.toHikariConfig() = toDbConfig().toHikariConfig()