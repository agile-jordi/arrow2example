package com.agilogy.arrow2.sql

sealed interface SqlPrimitive {
    companion object {
        val String.sql: SqlPrimitive get() = SqlString(this)
        val Int.sql: SqlPrimitive get() = SqlInt(this)
    }
}

@JvmInline
value class SqlInt(val value: Int) : SqlPrimitive
@JvmInline
value class SqlString(val value: String) : SqlPrimitive