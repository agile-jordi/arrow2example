package com.agilogy.arrow2.domain

sealed interface NoteEvent {
    val id: NoteId
}

data class NoteCreated(val note: Note) : NoteEvent {
    override val id: NoteId = note.id
}

data class NoteUpdated(val note: Note) : NoteEvent {
    override val id: NoteId = note.id
}

data class NoteDeleted(override val id: NoteId) : NoteEvent