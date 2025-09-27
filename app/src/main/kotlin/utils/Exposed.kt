package com.osucad.server.utils

import com.osucad.server.plugins.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

fun <ID : Any, T : Entity<ID>> EntityClass<ID, T>.createOrUpdate(
    id: ID,
    block: (T) -> Unit
): T = findByIdAndUpdate(id, block) ?: new(id, block)

fun DatabaseConfig.toHikariConfig(block: HikariConfig.() -> Unit = {}) = let { config ->
    HikariConfig().apply {
        jdbcUrl = config.url
        driverClassName = config.driver
        username = config.user
        password = config.password
        block()
    }
}