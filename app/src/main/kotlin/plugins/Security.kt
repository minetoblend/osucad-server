@file:OptIn(ExperimentalTime::class)

package com.osucad.server.plugins

import com.osucad.server.dao.User
import com.osucad.server.security.UserPrincipal
import io.ktor.events.EventDefinition
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

object LoginEvent : EventDefinition<User>()

@Serializable
class UserSession(val userId: Int)

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user-session", SessionStorageMemory()) {
            cookie.extensions["SameSite"] = "lax"
            cookie.secure = true
            cookie.httpOnly = true
        }
    }


    authentication {
        session<UserSession> {
            validate { session -> UserPrincipal(session.userId) }
        }
    }
}
