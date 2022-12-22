@file:Suppress("DataClassPrivateConstructor")

package com.agilogy.arrow2.domain

import arrow.core.NonEmptyList
import arrow.core.continuations.Raise
import arrow.core.continuations.ensure
import arrow.core.nonEmptyListOf
import com.agilogy.arrow2.utils.zip

val validIdsRe = Regex("[0-9a-zA-Z_.]+")

@JvmInline
value class NoteId private constructor(val value: String) {
    companion object {
        context(Raise<NonEmptyList<ValidationError>>)
        operator fun invoke(value: String): NoteId {
            ensure(validIdsRe.matchEntire(value) != null) {
                nonEmptyListOf(ValidationError(value, "Illegal id"))
            }
            return NoteId(value)
        }
    }
}

@JvmInline
value class Tag private constructor(val value: String) {
    companion object {
        context(Raise<NonEmptyList<ValidationError>>)
                operator fun invoke(value: String): Tag {
            val trim = value.trim()
            ensure(trim.isNotEmpty()) { nonEmptyListOf(ValidationError(value, "Illegal title")) }
            return Tag(trim)
        }
    }
}

@JvmInline
value class Title private constructor(val value: String) {

    companion object {
        context(Raise<NonEmptyList<ValidationError>>)
                operator fun invoke(value: String): Title {
            val trim = value.trim()
            ensure(trim.isNotEmpty()) { nonEmptyListOf(ValidationError(value, "Illegal title")) }
            return Title(trim)
        }
    }
}

@JvmInline
value class CategoryId private constructor(val value: String) {
    companion object {
        context(Raise<NonEmptyList<ValidationError>>)
                operator fun invoke(value: String): CategoryId {
            ensure(validIdsRe.matchEntire(value) != null) { nonEmptyListOf(ValidationError(value, "Illegal publisher id")) }
            return CategoryId(value)
        }
    }
}

data class ValidationError(val value: String, val cause: String)

data class Note (override val id: NoteId, val title: Title, val category: CategoryId, val tags: Set<Tag>) : NoteEvent {
    companion object {
        context(Raise<NonEmptyList<ValidationError>>)
        fun create(id: String, title: String, publisherId: String): Note =
            zip({ NoteId(id) }, { Title(title) }, { CategoryId(publisherId) }) { i, t, p -> Note(i, t, p, emptySet()) }
    }
}

