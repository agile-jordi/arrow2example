package com.agilogy.arrow2.domain

import arrow.core.continuations.Raise
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.either

class NoteHandler(private val notesStore: NotesStore, private val eventPublisher: NoteEventPublisher) {

    context(Raise<CreateNoteError>)
    suspend fun createNote(id: String, title: String, publisherId: String) {
        val note: Note = either{ Note.create(id, title, publisherId) }.mapLeft { NoteValidationError(it) }.bind()
        notesStore.insert(note)
        eventPublisher.publish(NoteCreated(note))
    }
}

