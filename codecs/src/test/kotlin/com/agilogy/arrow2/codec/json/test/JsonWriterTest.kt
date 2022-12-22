package com.agilogy.arrow2.codec.json.test

import com.agilogy.arrow2.json.Json
import com.agilogy.arrow2.json.Json.instantMillis
import com.agilogy.arrow2.json.Json.json
import com.agilogy.arrow2.json.Json.ofType
import com.agilogy.arrow2.json.JsonRW
import com.agilogy.arrow2.tests.assertReturns
import io.kotest.core.spec.style.FunSpec
import java.time.Instant
import java.time.temporal.ChronoUnit

class JsonWriterTest : FunSpec() {
    init {
        val talkRW: JsonRW<Nothing, Talk> = Json.objRw(
            "title" to Talk::title ofType Json.string,
            "startTime" to Talk::startTime ofType instantMillis,
            ::Talk
        )
        val roomScheduleRW: JsonRW<Nothing, RoomSchedule> = Json.objRw(
            "room" to RoomSchedule::room ofType Json.string,
            "talks" to RoomSchedule::talks ofType Json.list(talkRW),
            ::RoomSchedule
        )

        test("Write an object") {
            val talk = Talk("FP in Kotlin with Arrow", Instant.now().truncatedTo(ChronoUnit.MILLIS))
            val json = talkRW.write(talk)
            val expected = Json.obj("title" to talk.title.json, "startTime" to talk.startTime.toEpochMilli().json)
            assert(json == expected)
            assertReturns(talk) { talkRW.read(json) }
        }

        test("Write a complex object") {
            val talk1 = Talk("FP in Kotlin with Arrow", Instant.now().truncatedTo(ChronoUnit.MILLIS))
            val talk2 = Talk("Infra magic galore", Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.MILLIS))
            val schedule = RoomSchedule("TheRoom", listOf(talk1, talk2))
            val json = roomScheduleRW.write(schedule)
            assertReturns(schedule) { roomScheduleRW.read(json) }
        }
    }
}