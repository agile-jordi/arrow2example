package com.agilogy.arrow2.codec

import arrow.core.NonEmptyList
import arrow.core.continuations.Raise
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.either
import arrow.core.flatten
import arrow.core.mapOrAccumulate
import arrow.core.nonEmptyListOf
import arrow.core.zip
import com.agilogy.arrow2.utils.zip

interface Reader<Lang, out E, out A> {
    context(Raise<NonEmptyList<ReadError<E>>>)
    fun read(doc: Lang): A

    fun <B> map(afterRead: (A) -> B): Reader<Lang, E, B> = object : Reader<Lang, E, B> {
        context(Raise<NonEmptyList<ReadError<E>>>) override fun read(doc: Lang): B =
            afterRead(this@Reader.read(doc))
    }

    companion object {
        operator fun <Lang, E, A> invoke(f: context(Raise<NonEmptyList<ReadError<E>>>) (Lang) -> A) = object : Reader<Lang, E, A> {
            context(Raise<NonEmptyList<ReadError<E>>>) override fun read(doc: Lang): A = f(this@Raise, doc)
        }
    }
}


fun <Lang, E, A, B> Reader<Lang,E,A>.map(afterRead: context(Raise<E>) (A) -> B): Reader<Lang, E, B> = object : Reader<Lang, E, B> {
    context(Raise<NonEmptyList<ReadError<E>>>) override fun read(doc: Lang): B {
        val v = this@Reader.read(doc)
        return eagerEffect<E, B> { afterRead(this, v) } recover { raiseSingle(ReadErrorCause.MapError(it)) }
    }
}



typealias MemberReader<A, E, B> = Pair<String, Reader<A, E, B>>

interface LanguageReader<Lang> {

    companion object {
        context(Raise<NonEmptyList<ReadError<E>>>)
        fun <S, E, A> S.read(reader: Reader<S, E, A>): A = reader.read(this)

        operator fun <Lang> invoke(
            obj: Reader<Lang, Nothing, Map<String, Lang>>,
            list: Reader<Lang, Nothing, List<Lang>>,
        ): LanguageReader<Lang> = object : LanguageReader<Lang> {
            override val obj: Reader<Lang, Nothing, Map<String, Lang>> = obj
            override val list: Reader<Lang, Nothing, List<Lang>> = list
        }
    }

    val obj: Reader<Lang, Nothing, Map<String, Lang>>

    val list: Reader<Lang, Nothing, List<Lang>>

    fun <A, E> list(reader: Reader<Lang, E, A>): Reader<Lang, E, List<A>> = Reader { doc ->
        doc.read(list).mapIndexed { index, e -> index to e }
            .mapOrAccumulate { (index, e) ->
                eagerEffect { e.read(reader) } recover { errors -> raise(errors.map { it.copy(location = it.location / index) }) }
            }.mapLeft { it.flatten() }.bind()
    }

    context(Raise<NonEmptyList<ReadError<E>>>)
            private fun <E, A> read(map: Map<String, Lang>, member: MemberReader<Lang, E, A>): A {
        val value = map[member.first] ?: raise((nonEmptyListOf(ReadError(Location.Root / member.first, ReadErrorCause.AttributeExpected))))
        return eagerEffect { member.second.read(value) } recover { errors ->
            raise(errors.map { it.copy(location = it.location / member.first) })
        }
    }

    fun <E, A, Res> obj(member0: MemberReader<Lang, E, A>, f: (A) -> Res): Reader<Lang, E, Res> = Reader { doc ->
        f(read(doc.read(obj), member0))
    }

    fun <E, A, B, Res> obj(
        member0: MemberReader<Lang, E, A>,
        member1: MemberReader<Lang, E, B>,
        f: (A, B) -> Res,
    ): Reader<Lang, E, Res> = Reader { doc ->
        val map = doc.read(obj)
        zip({ read(map, member0) }, { read(map, member1) }, f)
        either { read(map, member0) }.zip(either { read(map, member1) }, f).bind()
    }

    fun <E, A, B, C, Res> obj(
        member0: MemberReader<Lang, E, A>,
        member1: MemberReader<Lang, E, B>,
        member2: MemberReader<Lang, E, C>,
        f: (A, B, C) -> Res,
    ): Reader<Lang, E, Res> = Reader { doc ->
        val map = doc.read(obj)
        zip({ read(map, member0) }, { read(map, member1) }, { read(map, member2) }, f)
    }
}