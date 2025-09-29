import com.osucad.server.plugins.DatabaseConfig
import com.osucad.server.utils.toHikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.testcontainers.containers.PostgreSQLContainer

fun PostgreSQLContainer<*>.toDbConfig() = DatabaseConfig(
    url = jdbcUrl,
    user = username,
    password = password,
    driver = driverClassName,
)

fun PostgreSQLContainer<*>.toHikariConfig() = toDbConfig().toHikariConfig()

fun HikariDataSource(config: DatabaseConfig) = HikariDataSource(config.toHikariConfig())

fun HikariDataSource(postgres: PostgreSQLContainer<*>) = HikariDataSource(postgres.toDbConfig())