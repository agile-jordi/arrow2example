package com.agilogy.arrow2.tests

import arrow.core.continuations.Raise
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.fold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail

fun <A> assertReturns(expected: A, program: context(Raise<Any>) () -> A) = eagerEffect {
    program()
}.fold(
    { fail("Unexpected failure $it") },
    { assert(it == expected) }
)

fun <E> assertRaises(expected: E, program: context(Raise<E>) () -> Any) = eagerEffect {
    program()
}.fold(
    { assertEquals(expected, it) },
    { fail("Unexpected success $it") }
)
