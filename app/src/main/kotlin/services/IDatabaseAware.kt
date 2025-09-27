package com.osucad.server.services

import io.r2dbc.spi.IsolationLevel
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

interface IDatabaseAware {
    suspend fun <T> withTransaction(statement: suspend R2dbcTransaction.() -> T): T

    suspend fun <T> withTransaction(
        transactionIsolation: IsolationLevel,
        readOnly: Boolean = false,
        statement: suspend R2dbcTransaction.() -> T
    ): T
}

class DatabaseAware(private val database: R2dbcDatabase) : IDatabaseAware {
    override suspend fun <T> withTransaction(statement: suspend R2dbcTransaction.() -> T): T =
        suspendTransaction(database, statement)

    override suspend fun <T> withTransaction(
        transactionIsolation: IsolationLevel,
        readOnly: Boolean,
        statement: suspend R2dbcTransaction.() -> T
    ): T = suspendTransaction(transactionIsolation, readOnly, database, statement)
}