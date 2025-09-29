package com.osucad.server.utils

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.Connection

enum class IsolationLevel {
    ReadUncommitted,
    ReadCommitted,
    RepeatableRead,
    Serializable;

    fun toInt() = when (this) {
        ReadUncommitted -> Connection.TRANSACTION_READ_UNCOMMITTED
        ReadCommitted -> Connection.TRANSACTION_READ_COMMITTED
        RepeatableRead -> Connection.TRANSACTION_REPEATABLE_READ
        Serializable -> Connection.TRANSACTION_SERIALIZABLE
    }
}

interface TransactionProvider {
    fun <T> withTransaction(statement: JdbcTransaction.() -> T): T

    fun <T> withTransaction(
        transactionIsolation: IsolationLevel,
        readOnly: Boolean = false,
        statement: JdbcTransaction.() -> T,
    ): T

    operator fun <T> invoke(statement: JdbcTransaction.() -> T) = withTransaction(statement)

    operator fun <T> invoke(
        transactionIsolation: IsolationLevel,
        readOnly: Boolean = false,
        statement: JdbcTransaction.() -> T,
    ): T = withTransaction(
        transactionIsolation = transactionIsolation,
        readOnly = readOnly,
        statement = statement,
    )
}

class DatabaseTransactionProvider(private val database: Database) : TransactionProvider {
    override fun <T> withTransaction(statement: JdbcTransaction.() -> T): T =
        transaction(database, statement)

    override fun <T> withTransaction(
        transactionIsolation: IsolationLevel,
        readOnly: Boolean,
        statement: JdbcTransaction.() -> T,
    ): T = transaction(transactionIsolation.toInt(), readOnly, database, statement)
}

fun Database.asTransactionProvider() = DatabaseTransactionProvider(this)
