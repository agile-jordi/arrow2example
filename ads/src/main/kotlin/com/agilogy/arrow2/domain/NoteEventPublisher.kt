package com.agilogy.arrow2.domain

interface NoteEventPublisher{
    suspend fun publish(noteEvent: NoteEvent)
}

