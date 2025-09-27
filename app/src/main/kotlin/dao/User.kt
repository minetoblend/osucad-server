package com.osucad.server.dao

import com.osucad.server.database.UsersTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(UsersTable)

    var username by UsersTable.username
    var lastLoginTime by UsersTable.lastLoginTime
}