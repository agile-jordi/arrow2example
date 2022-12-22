package com.agilogy.arrow2.domain

import arrow.core.continuations.Raise

interface NotesStore {
    context(Raise<NoteAlreadyExists>)
    suspend fun insert(note: Note)
}

