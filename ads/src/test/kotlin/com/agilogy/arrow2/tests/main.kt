package com.agilogy.arrow2.tests

import arrow.core.continuations.effect
import com.agilogy.arrow2.domain.NoteHandler
import com.agilogy.arrow2.utils.getOrElse
import java.util.logging.Logger

suspend fun main() {
    val logger = Logger.getAnonymousLogger()
    val store = InMemoryNotesStore(logger)
    val eventPublisher = InMemoryNoteEventPublisher(logger)
    val noteHandler = NoteHandler(store, eventPublisher)
    effect { noteHandler.createNote("", "", "") } getOrElse  { logger.severe(it.toString()) }
    effect { noteHandler.createNote("12", "Kotlin", "A") } getOrElse { logger.severe(it.toString()) }
    effect { noteHandler.createNote("12", "Cotlin", "B") } getOrElse { logger.severe(it.toString()) }
}