package modules.user

import com.osucad.osuapi.models.OsuApiUser
import com.osucad.server.modules.users.IUserService
import com.osucad.server.modules.users.usersModule
import com.osucad.server.plugins.LoginEvent
import com.osucad.server.plugins.eventBus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.server.plugins.di.*
import io.ktor.server.testing.*
import testModule

class LoginTest : FunSpec({
    test("user is created on login") {
        testApplication {
            application {
                testModule()
                usersModule()
            }

            startApplication()


            val userService: IUserService by application.dependencies

            val apiUser = OsuApiUser(
                id = 6411631,
                username = "Maarvin",
                avatarUrl = ""
            )

            userService.findById(apiUser.id).shouldBeNull()

            application.eventBus.publish(LoginEvent, apiUser)

            userService.findById(apiUser.id).shouldNotBeNull {
                id.value shouldBe apiUser.id
                username shouldBe apiUser.username
            }
        }
    }
})
