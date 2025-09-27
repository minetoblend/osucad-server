@file:OptIn(ExperimentalTime::class)

package com.osucad.server.modules.users

import com.osucad.server.dao.User
import com.osucad.server.utils.TransactionProvider
import com.osucad.server.utils.createOrUpdate
import kotlin.time.ExperimentalTime

interface IUserService {
    suspend fun findById(id: Int): User?

    suspend fun createOrUpdate(id: Int, block: User.() -> Unit): User
}

class UserService(private val transaction: TransactionProvider) : IUserService {

    override suspend fun findById(id: Int): User? = transaction {
        User.findById(id)
    }

    override suspend fun createOrUpdate(
        id: Int,
        block: User.() -> Unit,
    ) = transaction {
        User.createOrUpdate(id, block)
    }
}
