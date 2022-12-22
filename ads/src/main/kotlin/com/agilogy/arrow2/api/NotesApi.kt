package com.agilogy.arrow2.api

import arrow.core.continuations.Raise
import arrow.core.continuations.catch
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.effect
import com.agilogy.arrow2.api.protocol.ApiValidationErrors
import com.agilogy.arrow2.api.protocol.NoteRW
import com.agilogy.arrow2.api.protocol.NoteRW.noteRW
import com.agilogy.arrow2.codec.Reader
import com.agilogy.arrow2.domain.NotesStore
import com.agilogy.arrow2.utils.merge
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class NotesApi(private val notesStore: NotesStore) {

    context(Raise<Nothing>)
            suspend fun postNotes(body: String): Response = respondWith {
        val note = body.readRequestBody(noteRW)
        effect { notesStore.insert(note) }.recover { Response(ResponseStatus.ClientError, "Note already exists") }
        Response(ResponseStatus.Ok, note.id.value)
    }

    context(Raise<Response>)
    private fun <A> String.readRequestBody(reader: Reader<JsonElement, ApiValidationErrors, A>): A {
        val json = kotlin.runCatching { Json.parseToJsonElement(this@readRequestBody) }
            .getOrElse { raise(Response.clientError("Invalid json")) }
        return eagerEffect { reader.read(json) }
            .recover { errors -> raise(Response.clientError(NoteRW.readErrorsWriter.write(errors).toString())) }
    }

    private suspend fun respondWith(f: suspend context(Raise<Response>) () -> Response): Response =
        effect { f() }
            .catch { e -> Response(ResponseStatus.InternalServerError, e.toString()) }
            .merge()
}

enum class ResponseStatus {
    Ok, NotFound, InternalServerError, ClientError
}

data class Response(val status: ResponseStatus, val body: String) {
    companion object {
        fun clientError(body: String): Response = Response(ResponseStatus.ClientError, body)
    }
}