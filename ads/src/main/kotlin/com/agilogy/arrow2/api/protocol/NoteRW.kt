package com.agilogy.arrow2.api.protocol

import arrow.core.NonEmptyList
import arrow.core.continuations.Raise
import com.agilogy.arrow2.codec.RW
import com.agilogy.arrow2.codec.ReadError
import com.agilogy.arrow2.codec.xmap
import com.agilogy.arrow2.domain.CategoryId
import com.agilogy.arrow2.domain.Note
import com.agilogy.arrow2.domain.NoteId
import com.agilogy.arrow2.domain.Title
import com.agilogy.arrow2.domain.ValidationError
import com.agilogy.arrow2.json.Json
import com.agilogy.arrow2.json.Json.ofType
import com.agilogy.arrow2.json.JsonRW
import com.agilogy.arrow2.json.JsonWriter
import com.agilogy.arrow2.json.readErrorWriter
import kotlinx.serialization.json.JsonElement

typealias ApiValidationErrors = NonEmptyList<ValidationError>

object NoteRW {

    // Help type inference
    private fun <A, B> RW<JsonElement, ApiValidationErrors, A>.xm(afterRead: context(Raise<ApiValidationErrors>) (A) -> B, beforeWrite: (B) -> A): RW<JsonElement, ApiValidationErrors, B> =
        this.xmap(afterRead, beforeWrite)

    val noteRW: JsonRW<NonEmptyList<ValidationError>, Note> = Json.objRw(
        "id" to Note::id ofType Json.string.xm({ NoteId(it) }, { it.value }),
        "title" to Note::title ofType Json.string.xm({ Title.invoke(it) }, { it.value }),
        "category" to Note::category ofType Json.string.xm({ CategoryId.invoke(it) }, { it.value })
    ) { i, t, c -> Note(i, t, c, emptySet()) }

    private val validationErrorWriter: JsonWriter<ValidationError> = Json.string.cmap { it.cause }
    private val validationErrorsWriter: JsonWriter<ApiValidationErrors> = Json.writer.nonEmptyList(validationErrorWriter)
    private val readErrorWriter: JsonWriter<ReadError<ApiValidationErrors>> = Json.readErrorWriter(validationErrorsWriter)
    val readErrorsWriter: JsonWriter<NonEmptyList<ReadError<ApiValidationErrors>>> = Json.writer.nonEmptyList(readErrorWriter)
}