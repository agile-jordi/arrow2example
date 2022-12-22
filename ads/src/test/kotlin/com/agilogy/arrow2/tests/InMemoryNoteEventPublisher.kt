package com.agilogy.arrow2.tests

import com.agilogy.arrow2.domain.NoteEvent
import com.agilogy.arrow2.domain.NoteEventPublisher
import java.util.logging.Logger

class InMemoryNoteEventPublisher(private val logger: Logger): NoteEventPublisher {

    private val publishedEvents: MutableList<NoteEvent> = mutableListOf()

    override suspend fun publish(noteEvent: NoteEvent) {
        publishedEvents += noteEvent
        logger.info("Published $noteEvent")
    }
}