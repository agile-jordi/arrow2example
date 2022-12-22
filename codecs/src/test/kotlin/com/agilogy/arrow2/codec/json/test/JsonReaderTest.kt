package com.agilogy.arrow2.codec.json.test

import arrow.core.nonEmptyListOf
import com.agilogy.arrow2.codec.LanguageReader.Companion.read
import com.agilogy.arrow2.codec.Location
import com.agilogy.arrow2.codec.ReadError
import com.agilogy.arrow2.codec.ReadErrorCause
import com.agilogy.arrow2.codec.ReadErrorCause.PrimitiveExpected
import com.agilogy.arrow2.json.Json
import com.agilogy.arrow2.json.Json.json
import com.agilogy.arrow2.json.Json.ofType
import com.agilogy.arrow2.tests.assertRaises
import com.agilogy.arrow2.tests.assertReturns
import io.kotest.core.spec.style.FunSpec
import java.time.Instant
import java.time.temporal.ChronoUnit

class JsonReaderTest : FunSpec() {

    init {
        test("Read primitives") {
            assertReturns(true) { true.json.read(Json.boolean) }
            assertReturns(false) { false.json.read(Json.boolean) }
            assertRaises(nonEmptyListOf(ReadError.root(PrimitiveExpected("boolean")))) { 1.json.read(Json.boolean) }
            assertReturns(1) { 1.json.read(Json.int) }
            assertRaises(nonEmptyListOf(ReadError.root(PrimitiveExpected("number")))) { false.json.read(Json.int) }
            assertReturns(1L) { 1.json.read(Json.long) }
            assertRaises(nonEmptyListOf(ReadError.root(PrimitiveExpected("number")))) { false.json.read(Json.long) }
            assertReturns("1") { "1".json.read(Json.string) }
            assertRaises(nonEmptyListOf(ReadError.root(PrimitiveExpected("string")))) { false.json.read(Json.string) }
        }

        val booleanList = Json.list(Json.boolean)

        test("Read array") {
            assertReturns(listOf(true, false)) { listOf(true.json, false.json).json.read(booleanList) }
            assertRaises(nonEmptyListOf(ReadError(Location.Root / 0, PrimitiveExpected("boolean")))) {
                listOf(23.json, false.json).json.read(booleanList)
            }
            assertRaises(
                nonEmptyListOf(
                    ReadError(Location.Root / 0, PrimitiveExpected("boolean")),
                    ReadError(Location.Root / 1, PrimitiveExpected("boolean")),
                )
            ) {
                listOf(23.json, "hi".json).json.read(booleanList)
            }
        }

        test("Read array errors accumulate") {
            assertRaises(
                nonEmptyListOf(
                    ReadError(Location.Root / 0, PrimitiveExpected("boolean")),
                    ReadError(Location.Root / 1, PrimitiveExpected("boolean"))
                )
            ) {
                listOf(3.json, "hi".json).json.read(booleanList)
            }
        }

        val talkRW = Json.objRw(
            "title" to Talk::title ofType Json.string,
            "startTime" to Talk::startTime ofType Json.instantMillis,
            ::Talk
        )


        test("Read json objects") {
            val talk = Talk("A talk", Instant.now().truncatedTo(ChronoUnit.MILLIS))
            assertReturns(talk) {
                Json.obj("title" to talk.title.json, "startTime" to talk.startTime.toEpochMilli().json).read(talkRW)
            }

        }

        test("Read json object errors accumulate") {
            assertRaises(
                nonEmptyListOf(
                    ReadError(Location.Root / "title", PrimitiveExpected("string")),
                    ReadError(Location.Root / "startTime", ReadErrorCause.AttributeExpected)
                )
            ) {
                Json.obj("title" to 1.json).read(talkRW)
            }

        }
    }
}