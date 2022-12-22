package com.agilogy.arrow2.json

import arrow.core.NonEmptyList
import com.agilogy.arrow2.codec.Language
import com.agilogy.arrow2.codec.LanguageReader
import com.agilogy.arrow2.codec.LanguageWriter
import com.agilogy.arrow2.codec.RW
import com.agilogy.arrow2.codec.ReadError
import com.agilogy.arrow2.codec.ReadErrorCause
import com.agilogy.arrow2.codec.Reader
import com.agilogy.arrow2.codec.Writer
import com.agilogy.arrow2.codec.raisePrimitiveExpected
import com.agilogy.arrow2.codec.raiseSingle
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import java.time.Instant

typealias JsonWriter<A> = Writer<A, JsonElement>
typealias JsonRW<E, A> = RW<JsonElement, E, A>

object Json : Language<JsonElement> {

    fun array(vararg elements: JsonElement) = JsonArray(elements.toList())
    fun obj(vararg members: Pair<String, JsonElement?>) =
        JsonObject(members.mapNotNull { (n, v) -> v?.let { n to v } }.toMap())

    val Int?.json get() = this?.let { JsonPrimitive(it) } ?: JsonNull
    val Long?.json get() = this?.let { JsonPrimitive(it) } ?: JsonNull
    val Float?.json get() = this?.let { JsonPrimitive(it) } ?: JsonNull
    val Double?.json get() = this?.let { JsonPrimitive(it) } ?: JsonNull
    val Boolean?.json get() = this?.let { JsonPrimitive(it) } ?: JsonNull
    val String?.json get() = this?.let { JsonPrimitive(it) } ?: JsonNull

    val Collection<JsonElement>.json get() = JsonArray(this.toList())
    val Map<String, JsonElement>.json get() = JsonObject(this)
    val boolean: JsonRW<Nothing, Boolean> =
        RW(
            { json -> (json as? JsonPrimitive)?.booleanOrNull ?: raisePrimitiveExpected("boolean") },
            { it.json }
        )

    val int: JsonRW<Nothing, Int> =
        RW(
            { json -> (json as? JsonPrimitive)?.intOrNull ?: raisePrimitiveExpected("number") },
            { it.json }
        )

    val long: JsonRW<Nothing, Long> =
        RW(
            { json -> (json as? JsonPrimitive)?.longOrNull ?: raisePrimitiveExpected("number") },
            { it.json }
        )

    val instantMillis: JsonRW<Nothing, Instant> = long.xmap(Instant::ofEpochMilli) { it.toEpochMilli() }

    private val JsonPrimitive.stringOrNull: String? get() = if (isString) content else null
    val string: JsonRW<Nothing, String> =
        RW(
            { json -> (json as? JsonPrimitive)?.stringOrNull ?: raisePrimitiveExpected("string") },
            { it.json }
        )

    val element: JsonRW<Nothing, JsonElement> = RW({ it }, { it })

    override val reader: LanguageReader<JsonElement> = LanguageReader(
        Reader { json -> (json as? JsonObject) ?: raiseSingle(ReadErrorCause.ObjectExpected) },
        Reader { json -> (json as? JsonArray) ?: raiseSingle(ReadErrorCause.ArrayExpected) }
    )

    override val writer: LanguageWriter<JsonElement> = LanguageWriter({ it.json }, { it.json })
}

fun <E> Json.readErrorWriter(w: JsonWriter<E>): JsonWriter<ReadError<E>> =
    writer.obj(
        ("location" to ReadError<E>::location) ofType string.cmap { it.toString() },
        ("cause" to ReadError<E>::cause) ofType element.cmap {
            when (it) {
                is ReadErrorCause.SimpleReadErrorCause -> it.toString().json
                is ReadErrorCause.MapError<E> -> w.write(it.error)
            }
        }
    )

fun <E> Json.readErrorsWriter(w: JsonWriter<E>): JsonWriter<NonEmptyList<ReadError<E>>> =
    writer.list(readErrorWriter(w)).cmap { it.toList() }
