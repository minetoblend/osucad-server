package com.osucad.server.utils

import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

fun <ID : Any, T : Entity<ID>> EntityClass<ID, T>.createOrUpdate(
    id: ID,
    block: (T) -> Unit
): T = findByIdAndUpdate(id, block) ?: new(id, block)