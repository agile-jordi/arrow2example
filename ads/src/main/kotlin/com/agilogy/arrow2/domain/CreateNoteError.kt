package com.agilogy.arrow2.domain

import arrow.core.NonEmptyList

sealed interface CreateNoteError
data class NoteAlreadyExists(val id: NoteId) : CreateNoteError
data class NoteValidationError(val errors: NonEmptyList<ValidationError>) : CreateNoteError