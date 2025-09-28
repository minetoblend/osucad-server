package com.osucad.server.utils

import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.dao.load
import org.jetbrains.exposed.v1.dao.with
import org.jetbrains.exposed.v1.jdbc.SizedIterable
import kotlin.reflect.KProperty1


interface IEntityService<ID : Any, T : Entity<ID>> {
    fun findById(id: ID, vararg relations: KProperty1<out Entity<*>, Any?>): T?

    fun findByIds(ids: Iterable<ID>, vararg relations: KProperty1<out Entity<*>, Any?>): List<T>

    fun create(init: T.() -> Unit): T

    fun create(id: ID, init: T.() -> Unit): T

    fun find(
        vararg relations: KProperty1<out Entity<*>, Any?>,
        offset: Long? = null,
        limit: Long? = null,
        condition: () -> Op<Boolean>
    ): List<T>
}

open class EntityService<ID : Any, T : Entity<ID>>(
    protected val entityClass: EntityClass<ID, T>,
    protected val transaction: TransactionProvider,
) : IEntityService<ID, T> {
    override fun findById(id: ID, vararg relations: KProperty1<out Entity<*>, Any?>) = transaction {
        entityClass.findById(id)?.load(*relations)
    }

    override fun findByIds(ids: Iterable<ID>, vararg relations: KProperty1<out Entity<*>, Any?>) = transaction {
        entityClass.find { entityClass.table.id inList ids }.with(*relations).toList()
    }

    override fun create(init: T.() -> Unit) = transaction {
        entityClass.new(init)
    }

    override fun create(id: ID, init: T.() -> Unit) = transaction {
        entityClass.new(id, init)
    }

    override fun find(
        vararg relations: KProperty1<out Entity<*>, Any?>,
        offset: Long?,
        limit: Long?,
        condition: () -> Op<Boolean>
    ): List<T> = transaction {
        entityClass.find { condition() }
            .offset(offset)
            .limit(limit)
            .with(*relations).toList()
    }
}

fun <T> SizedIterable<T>.offset(offset: Long?): SizedIterable<T> = if (offset == null) this else offset(offset)
fun <T> SizedIterable<T>.limit(limit: Long?): SizedIterable<T> = if (limit == null) this else limit(limit)
