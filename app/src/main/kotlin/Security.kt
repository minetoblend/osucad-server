package com.osucad.server

import com.osucad.osuapi.models.OsuApiUser
import com.osucad.server.services.IUserService
import com.osucad.server.services.OsuApiFactory
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

private const val osu_oauth = "oauth-osu"

object LoginEvent : EventDefinition<OsuApiUser>()

@Serializable
class UserSession(val userId: Int, val accessToken: String)

fun Application.configureSecurity() {
    @Serializable
    class OAuthConfig(
        val redirectUrl: String,
        val clientId: String,
        val clientSecret: String
    )

    val config: OAuthConfig = property("oauth")

    install(Sessions) {
        cookie<UserSession>("user-session", SessionStorageMemory()) {
            cookie.extensions["SameSite"] = "lax"
            cookie.secure = true
            cookie.httpOnly = true
        }
    }



    authentication {
        session<UserSession> {
            validate { session -> session }
            challenge {
                call.respondRedirect("/auth/osu/login")
            }
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
                    defaultScopes = listOf("identify")
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
                    call.principal<OAuthAccessTokenResponse.OAuth2>()?.let { (accessToken) ->
                        val user = osuApi.create(accessToken).me()

                        log.info("User {} logged in via oauth", user.username)

                        eventBus.publish(LoginEvent, user)

                        call.sessions.set(UserSession(userId = user.id, accessToken = accessToken))
                    }

                    call.respondRedirect("/")
                }
            }
        }
    }
}