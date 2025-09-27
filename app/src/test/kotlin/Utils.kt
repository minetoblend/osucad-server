import com.osucad.server.DatabaseConfig
import com.osucad.server.configureDatabase
import io.ktor.server.application.*

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
