package integration

import com.osucad.server.module
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*

class ApplicationTest : FunSpec({
    test("it should work") {
        testApplication {
            environment {
                config = ApplicationConfig("application.yaml")
            }

            application {
                module()
            }

            client.get("/") shouldHaveStatus HttpStatusCode.OK
        }
    }
})