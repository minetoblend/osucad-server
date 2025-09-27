@file:OptIn(ExperimentalTime::class)

package com.osucad.server.plugins

import com.osucad.osuapi.models.OsuApiUser
import com.osucad.server.modules.osu.OsuApiFactory
import com.osucad.server.modules.users.IUserService
import com.osucad.server.security.UserPrincipal
import com.osucad.server.utils.expectNotNull
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.events.EventDefinition
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val osu_oauth = "oauth-osu"

object LoginEvent : EventDefinition<OsuApiUser>()

@Serializable
data class UserSession(val userId: Int, val accessToken: String)

fun Application.configureSecurity() {
    @Serializable
    class OAuthConfig(
        val redirectUrl: String,
        val clientId: String,
        val clientSecret: String,
    )

    val config: OAuthConfig = property("oauth")

    install(Sessions) {
        cookie<UserSession>("user-session", SessionStorageMemory()) {
            cookie.extensions["SameSite"] = "lax"
            cookie.secure = true
            cookie.httpOnly = true
        }
    }
    
    val redirects = mutableMapOf<String, String>()
    authentication {
        session<UserSession> {
            validate { session -> UserPrincipal(session.userId) }
        }

        oauth(osu_oauth) {
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
        authenticate(osu_oauth) {
            route("auth/osu") {
                get("login") {
                    // noop
                }

                get("callback") {
                    val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                        .expectNotNull("Expected oauth2 token principal")

                    val user = osuApi.create(principal.accessToken).me()

                    log.info("User {} logged in via oauth", user.username)

                    userService.createOrUpdate(user.id) {
                        username = user.username
                        lastLoginTime = Clock.System.now()
                    }

                    eventBus.publish(LoginEvent, user)

                    call.sessions.set(UserSession(userId = user.id, accessToken = principal.accessToken))

                    call.respondRedirect(redirects.remove(principal.state) ?: "/")
                }
            }
        }
    }
}
