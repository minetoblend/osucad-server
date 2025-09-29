package com.osucad.server.modules.osu

import com.osucad.server.modules.users.IUserService
import com.osucad.server.plugins.LoginEvent
import com.osucad.server.plugins.UserSession
import com.osucad.server.plugins.eventBus
import com.osucad.server.utils.expectNotNull
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

@Serializable
class OAuthConfig(
    val redirectUrl: String,
    val clientId: String,
    val clientSecret: String,
)

fun Application.configureOsuOauth(config: OAuthConfig = property("oauth.osu")) {
    val redirects = mutableMapOf<String, String>()

    authentication {
        oauth("oauth-osu") {
            urlProvider = { config.redirectUrl }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "osu",
                    authorizeUrl = "https://osu.ppy.sh/oauth/authorize",
                    accessTokenUrl = "https://osu.ppy.sh/oauth/token",
                    requestMethod = HttpMethod.Post,
                    clientId = config.clientId,
                    clientSecret = config.clientSecret,
                    defaultScopes = listOf("identify"),
                    onStateCreated = { call, state ->
                        call.parameters["redirectUrl"]?.let {
                            redirects[state] = it
                        }
                    }
                )
            }
            client = HttpClient(CIO)
        }
    }

    val osuApi: OsuApiFactory by dependencies
    val userService: IUserService by dependencies

    routing {
        authenticate("oauth-osu") {
            route("auth/osu") {
                get("login") {
                    // noop
                }

                get("callback") {
                    val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                        .expectNotNull("Expected oauth2 token principal")

                    val osuUser = osuApi.create(principal.accessToken).me()

                    val user = userService.getForLoginWithOsu(osuUser)

                    log.info("User {} logged in via osu oauth", user.username)

                    eventBus.publish(LoginEvent, user)

                    call.sessions.set(UserSession(userId = user.id.value))

                    call.respondRedirect(redirects.remove(principal.state) ?: "/")
                }
            }
        }
    }
}