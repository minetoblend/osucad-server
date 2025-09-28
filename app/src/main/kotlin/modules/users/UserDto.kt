package com.osucad.server.modules.users

import com.osucad.server.dao.User
import kotlinx.serialization.Serializable
import tech.mappie.api.ObjectMappie

@Serializable
class UserDto(
    val id: Int,
    val username: String,
)

object UserMapper : ObjectMappie<User, UserDto>() {
    override fun map(from: User): UserDto = mapping {
        to::id fromProperty from::id transform { it.value }
    }
}

fun User.toDto() = UserMapper.map(this)
