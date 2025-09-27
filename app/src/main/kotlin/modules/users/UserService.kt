package com.osucad.server.modules.users

import com.osucad.server.dao.User
import com.osucad.server.database.UsersTable
import com.osucad.server.utils.ITransactionProvider
import com.osucad.server.utils.createOrUpdate
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.jdbc.update

interface IUserService {
    suspend fun findById(id: Int): User?

    suspend fun createOrUpdate(id: Int, block: User.() -> Unit): User

    suspend fun updateLastLoginTime(id: Int)
}

class UserService(private val transaction: ITransactionProvider) :
    IUserService {

    override suspend fun findById(id: Int): User? = transaction {
        User.findById(id)
    }

    override suspend fun createOrUpdate(
        id: Int,
        block: User.() -> Unit,
    ) = transaction {
        User.createOrUpdate(id, block)
    }

    override suspend fun updateLastLoginTime(id: Int): Unit = transaction {
        UsersTable.update(id) {
            it[UsersTable.lastLoginTime] = CurrentTimestamp
        }
    }
}
