package com.osucad.server.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime

val tables = arrayOf<Table>(
    UsersTable,
)

object UsersTable : IntIdTable() {
    val username = varchar("username", 255)
    val lastLoginTime = timestamp("last_login_time").nullable()
}