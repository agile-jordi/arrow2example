package com.agilogy.arrow2.codec

import arrow.core.NonEmptyList
import arrow.core.continuations.Raise
import arrow.core.nonEmptyListOf

data class ReadError<out E>(val location: Location, val cause: ReadErrorCause<E>) {
    companion object {
        fun <E> root(error: ReadErrorCause<E>): ReadError<E> = ReadError(Location.Root, error)
    }
}

sealed interface ReadErrorCause<out E> {

    sealed interface SimpleReadErrorCause: ReadErrorCause<Nothing>

    data class PrimitiveExpected(val name: String) : SimpleReadErrorCause {
        override fun toString(): String = "${name.capitalize()}Expected"
    }

    object ArrayExpected : SimpleReadErrorCause {
        override fun toString(): String = "ArrayExpected"
    }

    object ObjectExpected : SimpleReadErrorCause {
        override fun toString(): String = "ObjectExpected"
    }

    object AttributeExpected : SimpleReadErrorCause {
        override fun toString(): String = "AttributeExpected"
    }

    data class MapError<E>(val error: E) : ReadErrorCause<E>
}

context(Raise<NonEmptyList<ReadError<Nothing>>>)
fun raisePrimitiveExpected(primitiveName: String): Nothing = raiseSingle(ReadErrorCause.PrimitiveExpected(primitiveName))

context(Raise<NonEmptyList<ReadError<E>>>)
fun <E> raiseSingle(cause: ReadErrorCause<E>): Nothing = raise(nonEmptyListOf(ReadError.root(cause)))
