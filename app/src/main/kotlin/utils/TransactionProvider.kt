package com.osucad.server.utils

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

interface TransactionProvider {
    fun <T> withTransaction(statement: JdbcTransaction.() -> T): T

    fun <T> withTransaction(
        transactionIsolation: Int,
        readOnly: Boolean = false,
        statement: JdbcTransaction.() -> T,
    ): T

    operator fun <T> invoke(statement: JdbcTransaction.() -> T) = withTransaction(statement)

    operator fun <T> invoke(
        transactionIsolation: Int,
        readOnly: Boolean = false,
        statement: JdbcTransaction.() -> T,
    ): T = withTransaction(
        transactionIsolation = transactionIsolation,
        readOnly = readOnly,
        statement = statement,
    )
}

class DefaultTransactionProvider(private val database: Database) : TransactionProvider {
    override fun <T> withTransaction(statement: JdbcTransaction.() -> T): T =
        transaction(database, statement)

    override fun <T> withTransaction(
        transactionIsolation: Int,
        readOnly: Boolean,
        statement: JdbcTransaction.() -> T,
    ): T = transaction(transactionIsolation, readOnly, database, statement)
}

fun Database.asTransactionProvider() = DefaultTransactionProvider(this)
