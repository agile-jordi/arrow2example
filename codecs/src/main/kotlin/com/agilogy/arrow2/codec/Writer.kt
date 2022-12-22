package com.agilogy.arrow2.codec

import arrow.core.NonEmptyList

fun interface Writer<A, Lang> {
    fun write(value: A): Lang
    fun <B> cmap(beforeWrite: (B) -> A): Writer<B, Lang> = Writer { write(beforeWrite(it)) }
}

data class MemberWriter<Lang, KObj, A>(
    override val name: String,
    override val property: (KObj) -> A,
    override val rw: Writer<A, Lang>
) : Member<Lang, KObj, A>

interface LanguageWriter<Lang> {

    infix fun <Lang, KObj, A> Pair<String, (KObj) -> A>.ofType(rw: Writer<A, Lang>): MemberWriter<Lang, KObj, A> =
        MemberWriter(first, second, rw)

    companion object {
        fun <A, S> A.write(writer: Writer<A, S>): S = writer.write(this)
        operator fun <Lang> invoke(obj: Writer<Map<String, Lang>, Lang>, list: Writer<List<Lang>, Lang>) =
            object : LanguageWriter<Lang> {
                override val obj: Writer<Map<String, Lang>, Lang> = obj
                override val list: Writer<List<Lang>, Lang> = list
            }
    }

    val obj: Writer<Map<String, Lang>, Lang>
    val list: Writer<List<Lang>, Lang>

    fun <A> list(writer: Writer<A, Lang>): Writer<List<A>, Lang> = Writer { l ->
        l.map { writer.write(it) }.write(list)
    }

    fun <A> nonEmptyList(writer: Writer<A, Lang>): Writer<NonEmptyList<A>, Lang> = list(writer).cmap { it.toList() }

    private operator fun <A, KObj> MemberWriter<Lang, KObj, A>.invoke(value: KObj) = name to rw.write(property(value))

    fun <KObj, A> obj(member0: MemberWriter<Lang, KObj, A>): Writer<KObj, Lang> = Writer { value ->
        mapOf(member0(value)).write(obj)
    }

    fun <KObj, A, B> obj(
        member0: MemberWriter<Lang, KObj, A>,
        member1: MemberWriter<Lang, KObj, B>
    ): Writer<KObj, Lang> = Writer { value ->
        mapOf(member0(value), member1(value)).write(obj)
    }

    fun <KObj, A, B, C> obj(
        member0: MemberWriter<Lang, KObj, A>,
        member1: MemberWriter<Lang, KObj, B>,
        member2: MemberWriter<Lang, KObj, C>,
    ): Writer<KObj, Lang> = Writer { value ->
        mapOf(member0(value), member1(value), member2(value)).write(obj)
    }

    fun <KObj, A, B, C, D> obj(
        member0: MemberWriter<Lang, KObj, A>,
        member1: MemberWriter<Lang, KObj, B>,
        member2: MemberWriter<Lang, KObj, C>,
        member3: MemberWriter<Lang, KObj, D>,
    ): Writer<KObj, Lang> = Writer { value ->
        mapOf(member0(value), member1(value), member2(value), member3(value)).write(obj)
    }
}