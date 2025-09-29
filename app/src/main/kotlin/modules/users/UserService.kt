@file:OptIn(ExperimentalTime::class)

package com.osucad.server.modules.users

import com.osucad.server.dao.User
import com.osucad.server.dao.UserRelation
import com.osucad.server.database.UserRelationsTable
import com.osucad.server.database.userRelationId
import com.osucad.server.modules.users.UserRelationKind.Blocked
import com.osucad.server.modules.users.UserRelationKind.Friend
import com.osucad.server.utils.EntityService
import com.osucad.server.utils.IEntityService
import com.osucad.server.utils.IsolationLevel.RepeatableRead
import com.osucad.server.utils.TransactionProvider
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.time.ExperimentalTime

interface IUserService : IEntityService<Int, User> {
    fun addFriend(userId: Int, otherUserId: Int): AddFriendResult

    fun removeFriend(userId: Int, otherUserId: Int): Boolean

    fun block(userId: Int, otherUserId: Int)

    fun unblock(userId: Int, otherUserId: Int): Boolean

    fun isFriend(userId: Int, otherUserId: Int): Boolean

    fun isBlocked(userId: Int, otherUserId: Int): Boolean
}

sealed interface AddFriendResult {
    object Success : AddFriendResult
    object IsBlocked : AddFriendResult
}


class UserService(private val transaction: TransactionProvider) : IUserService,
    IEntityService<Int, User> by EntityService(User, transaction) {

    override fun addFriend(userId: Int, otherUserId: Int): AddFriendResult =
        transaction(transactionIsolation = RepeatableRead) {
            when (UserRelation.find(userId, otherUserId)?.kind) {
                Blocked -> AddFriendResult.IsBlocked
                Friend -> AddFriendResult.Success
                else -> {
                    UserRelationsTable.insert {
                        it[fromId] = userId
                        it[toId] = otherUserId
                        it[kind] = Friend
                    }
                    AddFriendResult.Success
                }
            }
        }

    override fun removeFriend(userId: Int, otherUserId: Int): Boolean = transaction {
        val relation = UserRelation.find(userId, otherUserId)

        if (relation?.kind == Friend) {
            relation.delete()
            true
        } else {
            false
        }
    }

    override fun block(userId: Int, otherUserId: Int): Unit = transaction {
        UserRelationsTable.upsert {
            it[fromId] = userId
            it[toId] = otherUserId
            it[kind] = Blocked
        }
    }

    override fun unblock(userId: Int, otherUserId: Int): Boolean = transaction {
        val relation = UserRelation.find(userId, otherUserId)

        if (relation?.kind == Blocked) {
            relation.delete()
            true
        } else {
            false
        }
    }

    override fun isFriend(userId: Int, otherUserId: Int): Boolean = transaction {
        UserRelation.find(userId, otherUserId)?.kind == Friend
    }

    override fun isBlocked(userId: Int, otherUserId: Int): Boolean = transaction {
        UserRelation.find(userId, otherUserId)?.kind == Blocked
    }
}

private fun UserRelation.Companion.find(
    fromId: Int,
    toId: Int,
) = UserRelation.findById(userRelationId(fromId, toId))