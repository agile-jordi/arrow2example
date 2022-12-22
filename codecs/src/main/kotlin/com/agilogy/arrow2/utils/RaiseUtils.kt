package com.agilogy.arrow2.utils

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.continuations.EagerEffect
import arrow.core.continuations.Effect
import arrow.core.continuations.Raise
import arrow.core.continuations.fold
import arrow.core.flatten
import arrow.core.identity
import arrow.core.mapOrAccumulate


suspend inline infix fun <E, A> Effect<E, A>.getOrElse(crossinline f: (E) -> A): A = this.fold({ f(it) }, { it })
inline infix fun <E, A> EagerEffect<E, A>.getOrElse(crossinline f: (E) -> A): A = this.fold({ f(it) }, { it })


context(Raise<NonEmptyList<E>>) fun <E, A, B> List<A>.mapOrRaiseNelF(f: context(Raise<NonEmptyList<E>>) (A) -> B): List<B> =
    mapOrAccumulate(f).mapLeft { it.flatten() }.bind()

context(Raise<NonEmptyList<E>>)
fun <E, A, B> List<A>.mapOrRaiseNel(f: context(Raise<E>) (A) -> B): List<B> =
    mapOrAccumulate(f).bind()

suspend inline fun <A> Effect<A, A>.merge(): A = fold(::identity, ::identity)
