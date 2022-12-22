@file:JvmMultifileClass
@file:JvmName("EitherKt")

package com.agilogy.arrow2.utils

import arrow.core.NonEmptyList
import arrow.core.continuations.Raise
import arrow.core.continuations.either
import arrow.core.zip

val unit: context (Raise<Nothing>) () -> Unit = { }

context (Raise<NonEmptyList<E>>)
fun <E, A, B, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    transform: (A, B) -> Z,
): Z = zip(a, b, unit, unit, unit, unit, unit, unit, unit, unit) { aa, bb, _, _, _, _, _, _, _, _ ->
    transform(aa, bb)
}

context (Raise<NonEmptyList<E>>)
fun <E, A, B, C, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    transform: (A, B, C) -> Z,
): Z = zip(a, b, c, unit, unit, unit, unit, unit, unit, unit) { aa, bb, cc, _, _, _, _, _, _, _ ->
    transform(aa, bb, cc)
}

context (Raise<NonEmptyList<E>>)
fun <E, A, B, C, D, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    d: context(Raise<NonEmptyList<E>>)() -> D,
    transform: (A, B, C, D) -> Z,
): Z = zip(a, b, c, d, unit, unit, unit, unit, unit, unit) { aa, bb, cc, dd, _, _, _, _, _, _ ->
    transform(aa, bb, cc, dd)
}

context (Raise<NonEmptyList<E>>)
fun <E, A, B, C, D, EE, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    d: context(Raise<NonEmptyList<E>>)() -> D,
    e: context(Raise<NonEmptyList<E>>)() -> EE,
    transform: (A, B, C, D, EE) -> Z,
): Z = zip(a, b, c, d, e, unit, unit, unit, unit, unit) { aa, bb, cc, dd, ee, _, _, _, _, _ ->
    transform(aa, bb, cc, dd, ee)
}

context (Raise<NonEmptyList<E>>)
fun <E, A, B, C, D, EE, F, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    d: context(Raise<NonEmptyList<E>>)() -> D,
    e: context(Raise<NonEmptyList<E>>)() -> EE,
    f: context(Raise<NonEmptyList<E>>)() -> F,
    transform: (A, B, C, D, EE, F) -> Z,
): Z = zip(a, b, c, d, e, f, unit, unit, unit, unit) { aa, bb, cc, dd, ee, ff, _, _, _, _ ->
    transform(aa, bb, cc, dd, ee, ff)
}

context (Raise<NonEmptyList<E>>)
fun <E, A, B, C, D, EE, F, G, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    d: context(Raise<NonEmptyList<E>>)() -> D,
    e: context(Raise<NonEmptyList<E>>)() -> EE,
    f: context(Raise<NonEmptyList<E>>)() -> F,
    g: context(Raise<NonEmptyList<E>>)() -> G,
    transform: (A, B, C, D, EE, F, G) -> Z,
): Z = zip(a, b, c, d, e, f, g, unit, unit, unit) { aa, bb, cc, dd, ee, ff, gg, _, _, _ ->
    transform(aa, bb, cc, dd, ee, ff, gg)
}

context (Raise<NonEmptyList<E>>)
fun <E, A, B, C, D, EE, F, G, H, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    d: context(Raise<NonEmptyList<E>>)() -> D,
    e: context(Raise<NonEmptyList<E>>)() -> EE,
    f: context(Raise<NonEmptyList<E>>)() -> F,
    g: context(Raise<NonEmptyList<E>>)() -> G,
    h: context(Raise<NonEmptyList<E>>)() -> H,
    transform: (A, B, C, D, EE, F, G, H) -> Z,
): Z = zip(a, b, c, d, e, f, g, h, unit, unit) { aa, bb, cc, dd, ee, ff, gg, hh, _, _ ->
    transform(aa, bb, cc, dd, ee, ff, gg, hh)
}

context (Raise<NonEmptyList<E>>)
fun <E, A, B, C, D, EE, F, G, H, I, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    d: context(Raise<NonEmptyList<E>>)() -> D,
    e: context(Raise<NonEmptyList<E>>)() -> EE,
    f: context(Raise<NonEmptyList<E>>)() -> F,
    g: context(Raise<NonEmptyList<E>>)() -> G,
    h: context(Raise<NonEmptyList<E>>)() -> H,
    i: context(Raise<NonEmptyList<E>>)() -> I,
    transform: (A, B, C, D, EE, F, G, H, I) -> Z,
): Z = zip(a, b, c, d, e, f, g, h, i, unit) { aa, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
    transform(aa, bb, cc, dd, ee, ff, gg, hh, ii)
}

context (Raise<NonEmptyList<E>>)
        @Suppress("DuplicatedCode")
fun <E, A, B, C, D, EE, F, G, H, I, J, Z> zip(
    a: context(Raise<NonEmptyList<E>>)() -> A,
    b: context(Raise<NonEmptyList<E>>)() -> B,
    c: context(Raise<NonEmptyList<E>>)() -> C,
    d: context(Raise<NonEmptyList<E>>)() -> D,
    e: context(Raise<NonEmptyList<E>>)() -> EE,
    f: context(Raise<NonEmptyList<E>>)() -> F,
    g: context(Raise<NonEmptyList<E>>)() -> G,
    h: context(Raise<NonEmptyList<E>>)() -> H,
    i: context(Raise<NonEmptyList<E>>)() -> I,
    j: context(Raise<NonEmptyList<E>>)() -> J,
    transform: (A, B, C, D, EE, F, G, H, I, J) -> Z,
): Z = either(a).zip(either(b), either(c), either(d), either(e), either(f), either(g), either(h), either(i), either(j), transform)
    .bind()

