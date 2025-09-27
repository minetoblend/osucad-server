package com.osucad.server.services

import com.osucad.server.dao.User
import com.osucad.server.database.UsersTable
import com.osucad.server.utils.createOrUpdate
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.update

interface IUserService {
    suspend fun findById(id: Int): User?

    suspend fun createOrUpdate(id: Int, username: String): User

    suspend fun updateLastLoginTime(id: Int)
}

class UserService(database: R2dbcDatabase) :
    IUserService,
    IDatabaseAware by DatabaseAware(database) {

    override suspend fun findById(id: Int): User? = withTransaction {
        User.findById(id)
    }

    override suspend fun createOrUpdate(id: Int, username: String) = withTransaction {
        User.createOrUpdate(id) {
            it.username = username
        }
    }

    override suspend fun updateLastLoginTime(id: Int): Unit = withTransaction {
        UsersTable.update(id) {
            it[UsersTable.lastLoginTime] = CurrentTimestamp
        }
    }
}