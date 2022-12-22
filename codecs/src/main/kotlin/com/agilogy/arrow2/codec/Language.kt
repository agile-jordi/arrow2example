package com.agilogy.arrow2.codec

import arrow.core.NonEmptyList
import arrow.core.continuations.Raise
import kotlin.experimental.ExperimentalTypeInference

data class RW<Lang, out E, A>(val reader: Reader<Lang, E, A>, val writer: Writer<A, Lang>) :
    Reader<Lang, E, A>, Writer<A, Lang> by writer {
    companion object {
        operator fun <Lang, E, A> invoke(read: context(Raise<NonEmptyList<ReadError<E>>>) (Lang) -> A, write: (A) -> Lang): RW<Lang, E, A> =
            RW(Reader(read), Writer(write))
    }

    fun <B> xmap(afterRead: (A) -> B, beforeWrite: (B) -> A): RW<Lang, E, B> =
        RW(reader.map(afterRead), writer.cmap(beforeWrite))

    context(Raise<NonEmptyList<ReadError<E>>>) override fun read(doc: Lang): A =reader.read(doc)
}


@OptIn(ExperimentalTypeInference::class)
fun <Lang, E, A, B> RW<Lang,E,A>.xmap(@BuilderInference afterRead: context(Raise<E>) (A) -> B, beforeWrite: (B) -> A)
: RW<Lang, E, B> =
    RW(reader.map(afterRead), writer.cmap(beforeWrite))


interface Member<Lang, KObj, A> {
    val name: String
    val property: (KObj) -> A
    val rw: Writer<A, Lang>
}

data class MemberRW<Lang, KObj, out E, A>(
    override val name: String,
    override val property: (KObj) -> A,
    override val rw: RW<Lang, E, A>,
) : Member<Lang, KObj, A> {
    internal val read: MemberReader<Lang, E, A> get() = name to rw
    val writer: MemberWriter<Lang, KObj, A> = MemberWriter(name, property, rw)
}

interface Language<Lang> {
    infix fun <Lang, KObj, E, A> Pair<String, (KObj) -> A>.ofType(rw: RW<Lang, E, A>): MemberRW<Lang, KObj, E, A> =
        MemberRW(first, second, rw)

    infix fun <Lang, A, KObj> Pair<String, (KObj) -> A>.ofType(w: Writer<A, Lang>): MemberWriter<Lang, KObj, A> =
        MemberWriter(first, second, w)

    val reader: LanguageReader<Lang>
    val writer: LanguageWriter<Lang>

    fun <E, A> list(readWrite: RW<Lang, E, A>): RW<Lang, E, List<A>> =
        RW(reader.list(readWrite.reader), writer.list(readWrite.writer))

    fun <E, A, KObj> objRw(member0: MemberRW<Lang, KObj, E, A>, f: (A) -> KObj): RW<Lang, E, KObj> =
        RW(reader.obj(member0.read, f), writer.obj(member0.writer))

    fun <E, A, B, KObj> objRw(
        member0: MemberRW<Lang, KObj, E, A>,
        member1: MemberRW<Lang, KObj, E, B>,
        constructor: (A, B) -> KObj,
    ): RW<Lang, E, KObj> =
        RW(
            reader.obj(member0.read, member1.read, constructor),
            writer.obj(member0.writer, member1.writer)
        )

    fun <E, A, B, C, KObj> objRw(
        member0: MemberRW<Lang, KObj, E, A>,
        member1: MemberRW<Lang, KObj, E, B>,
        member2: MemberRW<Lang, KObj, E, C>,
        constructor: (A, B, C) -> KObj,
    ): RW<Lang, E, KObj> =
        RW(
            reader.obj(member0.read, member1.read, member2.read, constructor),
            writer.obj(member0.writer, member1.writer, member2.writer)
        )

}