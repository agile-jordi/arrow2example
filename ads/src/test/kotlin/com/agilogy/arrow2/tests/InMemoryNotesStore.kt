package com.agilogy.arrow2.tests

import arrow.core.continuations.Raise
import arrow.core.continuations.ensure
import com.agilogy.arrow2.domain.NoteAlreadyExists
import com.agilogy.arrow2.domain.NotesStore
import com.agilogy.arrow2.domain.Note
import java.util.logging.Logger

class InMemoryNotesStore(private val logger: Logger) : NotesStore {

    private val notes: MutableList<Note> = mutableListOf()

    context(Raise<NoteAlreadyExists>)
    override suspend fun insert(note: Note) {
        ensure(notes.none { it.id == note.id }) { NoteAlreadyExists(note.id) }
        notes += note
        logger.info("Inserted ad $note")
    }
}