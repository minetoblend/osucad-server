@file:OptIn(ExperimentalTime::class)

package com.osucad.server.modules.users

import com.osucad.server.dao.User
import com.osucad.server.utils.TransactionProvider
import com.osucad.server.utils.createOrUpdate
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.load
import kotlin.reflect.KProperty1
import kotlin.time.ExperimentalTime

interface IUserService {
    suspend fun findById(id: Int, vararg relations: KProperty1<out Entity<*>, Any?>): User?

    suspend fun createOrUpdate(id: Int, block: User.() -> Unit): User
}

class UserService(private val transaction: TransactionProvider) : IUserService {
    override suspend fun findById(id: Int, vararg relations: KProperty1<out Entity<*>, Any?>): User? = transaction {
        User.findById(id)?.load(*relations)
    }

    override suspend fun createOrUpdate(
        id: Int,
        block: User.() -> Unit,
    ) = transaction {
        User.createOrUpdate(id, block)
    }
}
