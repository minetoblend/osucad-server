package com.osucad.server.utils

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.vendors.H2Dialect
import org.jetbrains.exposed.v1.core.vendors.currentDialect
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import java.nio.ByteBuffer
import java.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class UuidColumnType : ColumnType<Uuid>() {
    override fun sqlType(): String = currentDialect.dataTypeProvider.uuidType()

    override fun valueFromDB(value: Any): Uuid = when (value) {
        is Uuid -> value
        is UUID -> value.toKotlinUuid()
        is ByteArray -> Uuid.fromByteArray(value)
        is String if value.matches(uuidRegexp) -> Uuid.parse(value)
        is String -> Uuid.fromByteArray(value.toByteArray())
        is ByteBuffer -> Uuid.fromLongs(value.long, value.long)
        else -> error("Unexpected value of type Uuid: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Uuid): Any {
        return ((currentDialect as? H2Dialect)?.originalDataTypeProvider ?: currentDialect.dataTypeProvider)
            .uuidToDB(value.toJavaUuid())
    }

    override fun nonNullValueToString(value: Uuid): String = "'$value'"

    companion object {
        private val uuidRegexp =
            Regex("[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}", RegexOption.IGNORE_CASE)
    }
}

fun Table.kotlinUuid(name: String) = registerColumn(name, UuidColumnType())

context(table: Table)
fun Column<Uuid>.autoGenerate(): Column<Uuid> = with(table) {
    clientDefault { Uuid.random() }
}

open class UuidTable(name: String = "") : IdTable<Uuid>(name) {
    final override val id: Column<EntityID<Uuid>> = kotlinUuid("id").autoGenerate().entityId()
    final override val primaryKey = PrimaryKey(id)
}

abstract class UuidEntity(id: EntityID<Uuid>) : Entity<Uuid>(id)

abstract class UuidEntityClass<out E : UuidEntity>(
    table: IdTable<Uuid>,
    entityType: Class<E>? = null,
    entityCtor: ((EntityID<Uuid>) -> E)? = null
) : EntityClass<Uuid, E>(table, entityType, entityCtor)