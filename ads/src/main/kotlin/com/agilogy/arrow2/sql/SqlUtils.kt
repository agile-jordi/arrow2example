package com.agilogy.arrow2.sql

import arrow.core.continuations.Raise
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import javax.sql.DataSource

object DuplicateKey

context(Connection, Raise<Nothing>)
suspend fun executeUpdate(query: String, vararg args: SqlPrimitive): Int =
    prepareStatement(query).use { ps ->
        args.forEachIndexed { pos, arg -> ps.setArgument(pos, arg) }
        ps.executeUpdate()
    }

context(Connection, Raise<DuplicateKey>)
suspend fun executeUpdateCatchDuplicateKey(query: String, vararg args: SqlPrimitive): Int =
    try {
        executeUpdate(query, *args)
    } catch (e: SQLException) {
        if (e.isDuplicateKey()) raise(DuplicateKey)
        else throw e
    }

private fun SQLException.isDuplicateKey(): Boolean = true

private fun PreparedStatement.setArgument(pos: Int, arg: SqlPrimitive) = when (arg) {
    is SqlInt -> setInt(pos, arg.value)
    is SqlString -> setString(pos, arg.value)
}

context(Raise<E>)
@Suppress("UsePropertyAccessSyntax")
suspend fun <E, A> DataSource.withConnection(f: suspend context(Raise<E>, Connection) () -> A): A =
    with(getConnection()) { f(this@Raise, this) }