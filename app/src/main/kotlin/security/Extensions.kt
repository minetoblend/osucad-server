package com.osucad.server.security

import com.osucad.server.dao.User
import com.osucad.server.modules.users.IUserService
import com.osucad.server.utils.expectNotNull
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*

val ApplicationCall.userPrincipal get() = principal<UserPrincipal>().expectNotNull("No active session")

val ApplicationCall.userId get() = userPrincipal.userId

suspend fun ApplicationCall.currentUserOrNull(): User? {
    val principal = principal<UserPrincipal>() ?: return null

    val userService: IUserService by application.dependencies

    return userService.findById(principal.userId)
}

suspend fun ApplicationCall.currentUser(message: String = "No user found") = currentUserOrNull().expectNotNull(message)